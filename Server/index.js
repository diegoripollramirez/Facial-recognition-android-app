const puerto = 3000;
const Connect_MongoDB = require("./Conexion_MongoDB.js");
const guardarImagen = require("./Guardar_Fichero.js");// Guarda la foto en una carpeta del servidor
const guardarUsuario = require("./Guardar_Usuario.js").guardarUsuario;// Almacena las fotos de un usuario
const caracteristicasFaciales= require("./Obtener_Caracteristicas.js");
const compararCaracteristicasFaciales = require("./Buscar_Coincidencias_Faciales.js");
const promediarUsuarios = require("./PromediarUsuarios.js");
const express = require("express");
const app = express();
const bodyParser = require("body-parser");
const fs = require("fs");
const multer = require("multer");
app.use("/fotos", express.static(__dirname + "/fotos"));

// Para arrancar el servidor: node servidor_tfg_drr/index.js
// Para iniciar Mongo en el pc lo hacemos con la instruccion: mongod

// Middleware para analizar el cuerpo de la solicitud en formato JSON
app.use(bodyParser.raw({limit: "50mb", type: "image/png"}));
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());
// Configurar multer para recibir mensajes multiparte (strings+foto)
const storage = multer.memoryStorage();
const upload = multer({storage: storage});


app.get("/imagen", (req, res) => {
  const path = require("path");
  const nombreImagen = req.query.nombre;
  const rutaImagen = path.join(__dirname, "fotos", nombreImagen);
  res.sendFile(rutaImagen);
});


// Ruta para guardar nuevas caras
app.post("/guardar_usuario", upload.single("foto"), async (req, res) => {
  const nombre = req.body.nombre;
  const apellidos = req.body.apellido;
  const telefono = req.body.telefono;
  let caracteristicas;
  let fotoRecibida;
  if (req.body.caracteristicas) {
    caracteristicas = req.body.caracteristicas;
    if (caracteristicas!=0) {
      Connect_MongoDB();
      promediarUsuarios(nombre, apellidos, telefono, caracteristicas);      
      const responseString = "Datos del usuario almacenados en la base de datos";
      res.set("Content-Type", "text/plain");
      res.send(responseString);
    } else {
      const responseString = "Error al almacenar los datos del usuario en la base de datos";
      res.set("Content-Type", "text/plain");
      res.send(responseString);
    }

  } else {
    fotoRecibida = req.file.buffer; // req.file.buffer contendrá el archivo de imagen en formato png
    caracteristicasRecibidas=await caracteristicasFaciales(fotoRecibida);
    caracteristicas=caracteristicasRecibidas.toString();    
    if (caracteristicas!=0) {
      Connect_MongoDB();
      guardarUsuario(nombre, apellidos, telefono, caracteristicas);      
      const responseString = "Datos del usuario almacenados en la base de datos";
      res.set("Content-Type", "text/plain");
      res.send(responseString);
    } else {
      const responseString = "Error al almacenar los datos del usuario en la base de datos";
      res.set("Content-Type", "text/plain");
      res.send(responseString);
    }
  }  
});


app.post("/buscar_caras", async (req, res) => {
  const fotoRecibida = req.body; // req.body contendrá la imagen en formato png
  console.log("Recibimos la conexion");

  const caracteristicas=await caracteristicasFaciales(fotoRecibida);
  const nombreFoto=await guardarImagen(fotoRecibida);
  if (caracteristicas!=0) {
    Connect_MongoDB();
    const resultadosSet = await compararCaracteristicasFaciales(caracteristicas, nombreFoto);
    
    if(resultadosSet){
      const resultadosArray = Array.from(resultadosSet);
      res.set("Content-Type", "application/json"); // Establecer el tipo de contenido como JSON
      res.send(JSON.stringify(resultadosArray));
    }else {
      const responseString = "No se ha encontrado ninguna coincidencia";
      res.set("Content-Type", "text/plain");
      res.send(responseString);
    }
    
  } else {
    const responseString = "No se ha encontrado ninguna cara que comparar";
    res.set("Content-Type", "text/plain");
    res.send(responseString);
  }
});


// Gracias a expressJS el servidor se monta solo y solo tengo que iniciarlo y gestionar sus metodos de comunicacion
app.listen(puerto, () => {
  console.log(`Servidor escuchando en el puerto ${puerto}`);
});


