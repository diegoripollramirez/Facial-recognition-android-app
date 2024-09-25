const faceapi = require("face-api.js");
const Usuario = require("./Guardar_Usuario.js").Usuario; // Importa el modelo Usuario desde el archivo models/user.js

async function compararCaracteristicasFaciales(caracteristicas, nombreFoto) {
  try {

    const resultadoFinal = new Set();
    // Buscar en la base de datos los usuarios con características faciales similares a las características nuevas
    for (let i = 0; i < caracteristicas.length; i++) {
      const descriptoresDeCadaCara = caracteristicas[i];// el arreglo "caracteristicas guardaba los datos de cada persona de la foto como Float32Array"
      // Inicializar la mejor coincidencia encontrada hasta el momento como null
      let bestMatch = null;
      // Distancia euclidea va de 0 a 1 segun pierde parecido
      // Distancia coseno va de 1 a -1 segun pierde parecido, si uso esta tengo que cambiar la desigualdad para quedarme con la mejor
      const distanciaMaximaAceptable = 0.5;
      await Usuario.find({})
          .then((usuarios) => {
            usuarios.forEach((usuario) => {
              // Convertimos el String en Float32 para compararlo con el Float32 de las caracteristicas
              const faceDescriptorsUsuario = new Float32Array(usuario.caracteristicasFaciales.split(",").map(Number));

              if (faceDescriptorsUsuario.length === descriptoresDeCadaCara.length) {
                const distancia = faceapi.euclideanDistance(faceDescriptorsUsuario, descriptoresDeCadaCara);
                if (distancia <= distanciaMaximaAceptable && (!bestMatch || distancia < bestMatch.similitud)) {
                  // Si la distancia es baja y es mejor que la actual mejor coincidencia, actualizar la variable bestMatch
                  bestMatch = {
                    nombre: usuario.nombre,
                    apellidos: usuario.apellidos,
                    telefono: usuario.telefono,
                    caracteristicas: descriptoresDeCadaCara.toString(),                    
                    nombreFoto: nombreFoto,
                    similitud: distancia,
                  };
                }
              }
            });
          });

      // Agregar la mejor coincidencia encontrada al set de resultado final
      if (bestMatch) {
        resultadoFinal.add(bestMatch);
      }
    }
    console.log(`Mejores coincidencias encontradas: ${resultadoFinal.size}`);
    console.log(resultadoFinal);    
    if(resultadoFinal.size>0){
      return resultadoFinal; // Devolver el array de mejores coincidencias encontradas
    }else{
      return null; // Devolver un array vacío en caso de error
    }
    
  } catch (err) {
    console.error(err);
    return null; // Devolver un array vacío en caso de error
  }
}


module.exports = compararCaracteristicasFaciales;


