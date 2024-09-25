const mongoose = require("mongoose");

async function Connect_MongoDB() {
  // Usamos mongoose para conectar
  try {
    await mongoose.connect("mongodb://127.0.0.1:27017/TFG_DRR", {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log("Conexión a la base de datos establecida");
    return mongoose.connection; // Devuelve el objeto de conexión
  } catch (err) {
    console.error("Error al conectar a la base de datos:", err);
    return null; // Devuelve null en caso de error
  }
}

module.exports = Connect_MongoDB;
