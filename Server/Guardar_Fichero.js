const fs = require("fs");
const { nanoid } = require('nanoid');

async function guardarFichero(foto) {
  // Convertir fs.writeFile a una función promisificada
  
  const directorio = "./servidor_tfg_drr/fotos/"; // Directorio de destino
  const nombreArchivo = nanoid() + ".png";

  if (!fs.existsSync(directorio)) {
    fs.mkdirSync(directorio); // Crea el directorio si no existe
  }

  try {
    await fs.promises.writeFile(directorio + nombreArchivo, foto, "binary");
    // Éxito, devolver un resultado
    return nombreArchivo;
  } catch (err) {
    // Manejar errores
    console.error(`Error al guardar la foto: ${err}`);
    throw err;
  }
}

module.exports = guardarFichero;
