const mongoose = require("mongoose");

// Definir el esquema de la colección de usuarios
const usuarioSchema = new mongoose.Schema({
  _id: mongoose.Schema.Types.ObjectId,
  nombre: String,
  apellidos: String,
  telefono: String,
  caracteristicasFaciales: String,
}, {collection: "Usuarios"}); // 'usuarios' es el nombre de la colección en MongoDB

// Crear el modelo de datos a partir del esquema
const Usuario = mongoose.model("Usuario", usuarioSchema);

async function guardarUsuario(nombre, apellidos, telefono, caracteristicasFaciales) {
  
  // Generar un nuevo ObjectId
  const nuevoId = new mongoose.Types.ObjectId();

  const nuevoUsuario = new Usuario({
    _id: nuevoId,
    nombre: nombre,
    apellidos: apellidos,
    telefono: telefono,
    caracteristicasFaciales: caracteristicasFaciales,
  });

  try {
    // Guardar el nuevo usuario en la colección
    const usuarioGuardado = await nuevoUsuario.save();    
  } catch (err) {
    console.error(err);
  }
}

module.exports = {
  Usuario: Usuario,
  guardarUsuario: guardarUsuario,
};
