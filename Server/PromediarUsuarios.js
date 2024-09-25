// Importar los módulos necesarios
const mongoose = require('mongoose');
const guardarUsuario = require("./Guardar_Usuario.js").guardarUsuario;
const Usuario = require("./Guardar_Usuario.js").Usuario;

// Definir el esquema de la colección de usuarios
const CaracteristicasSchema = new mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    nombre: String,
    apellidos: String,
    telefono: String,
    caracteristicasFaciales: String,
}, {collection: "Caracteristicas"}); // 'caracteristicas' es el nombre de la colección en MongoDB
  
// Crear el modelo de datos a partir del esquema
const Caracteristicas = mongoose.model("Caracteristicas", CaracteristicasSchema);
 
// Función para obtener la media de los descriptores de un usuario
async function promediarUsuario(nombre, apellidos, telefonoRecibido, caracteristicasFaciales) {   
    
    //Guardamos las nuevas caracteristicas en la tabla de caracteristicas
    const nuevoId = new mongoose.Types.ObjectId();
    const nuevoUsuarioCaracteristicas = new Caracteristicas({
        _id: nuevoId,
        nombre: nombre,
        apellidos: apellidos,
        telefono: telefonoRecibido,
        caracteristicasFaciales: caracteristicasFaciales,
    });
    try {
        // Guardar el nuevo usuario en la colección
        await nuevoUsuarioCaracteristicas.save();        
    } catch (err) {
        console.error(err);
    } 

    
    // Obtener todos los usuarios de la coleccion caracteristicas que coincidan con el telefono de quien acabamos de guardar en caracteristicas
    const caracteristicasCargadas = await Caracteristicas.find({ telefono: telefonoRecibido });

    // Objeto para almacenar los descriptores separados por característica
    const descriptoresPorCaracteristica = {};
    for (const registro of caracteristicasCargadas) {
        const descriptores = registro.caracteristicasFaciales.split(",").map(Number);
        for (let i = 0; i < descriptores.length; i++) {        
            if (!descriptoresPorCaracteristica[i]) {
                descriptoresPorCaracteristica[i] = [];
            }
            descriptoresPorCaracteristica[i].push(descriptores[i]);              
        }
    }
    
    // Calcular la media de cada conjunto de descriptores
    const descriptoresPromedio = [];
    for (const descriptor of Object.values(descriptoresPorCaracteristica)) {        
        let descriptorIndividual=0
        for (let i = 0; i < descriptor.length; i++) {        
            descriptorIndividual+=descriptor[i]        
        }
        descriptorIndividual=descriptorIndividual/descriptor.length
        descriptoresPromedio.push(descriptorIndividual);
    }
    const descriptoresPromedioString = descriptoresPromedio.join(", ");
    
    // Actualizar las características faciales del usuario en la colección 'Usuarios'  
    const usuario = await Usuario.findOne({ telefono: telefonoRecibido });
    
    if(usuario){
        await Usuario.updateOne({ telefono: telefonoRecibido }, { caracteristicasFaciales: descriptoresPromedioString });
    }else{
    // Si no hay usuario previo, lo creamos    
        guardarUsuario(nombre, apellidos, telefonoRecibido, descriptoresPromedioString);
    }
} 

    module.exports = promediarUsuario