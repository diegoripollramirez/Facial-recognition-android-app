const faceapi = require("face-api.js");
const canvas = require("canvas");
const {Canvas, Image, ImageData} = canvas;
faceapi.env.monkeyPatch({Canvas, Image, ImageData});
const tf = require("@tensorflow/tfjs"); // La version compatible es la 1.7.4 no se puede instalar la ultima, revisar la documentacion por si sacaran otra conmpatible


// Cargar los modelos de face-api.js
async function obtenerCaracteristicasFaciales(fotoRecibida) {
  try {
    // Cargar los modelos de face-api.js
    await Promise.all([
      faceapi.nets.faceRecognitionNet.loadFromDisk(__dirname + "/models"), // Cargar el modelo de detección de caras
      faceapi.nets.faceLandmark68Net.loadFromDisk(__dirname + "/models"), // Cargar el modelo de detección de landmarks faciales
      faceapi.nets.ssdMobilenetv1.loadFromDisk(__dirname + "/models"), // Cargar el modelo de extracción de características faciales
    ]);    
    const img = await canvas.loadImage(fotoRecibida); // Cargar la imagen utilizando la biblioteca canvas    
    const caras = await faceapi.detectAllFaces(img).withFaceLandmarks().withFaceDescriptors(); // Detectar caras, puntos de referencia y caracteristicas faciales de cada una de ellas
        
    /*
    const carasConBarba = [];//la posicion de caras que tiene barba
    const carasConGafas = [];//la posicion de caras que tiene gafas
    //Detectar en las fotos si la cara lleva accesorios usando las distancias de landmarks extraidos en caras
    caras.forEach((cara) => {
      // Comprobar si la cara tiene barba
      const barbilla = cara.landmarks._positions[9];
      const labioInferior = cara.landmarks._positions[58];
      const distanciaBarbaLabio = faceapi.euclideanDistance(barbilla, labioInferior);
      if (distanciaBarbaLabio > 60) { // Valor de distancia a ajustar
        console.log('Cara sin barba');
        return;
      }else{
        carasConBarba.push(caras.indexOf(cara));
      }

      // Comprobar si la cara lleva gafas midiendo la distancia entre el extremo exterior del ojo y la nariz
      const ojoIzquierdo = cara.landmarks.positions[37];
      const ojoDerecho = cara.landmarks.positions[46];
      const nariz = cara.landmarks.positions[28];
      const distanciaGafaIzquierda = faceapi.euclideanDistance(ojoIzquierdo, nariz);
      const distanciaGafaDerecha = faceapi.euclideanDistance(ojoDerecho, nariz);
      if (distanciaGafaIzquierda < 40 && distanciaGafaDerecha < 40) { // Valor de distancia a ajustar
        console.log('Cara sin gafas');
        return;
      }else{
        carasConGafas.push(caras.indexOf(cara));
      }      
    });
*/

    if (caras.length > 0) {
      const faceDescriptors = caras.map((detection) => detection.descriptor); // Obtener los descriptores de características faciales de todas las caras detectadas
      console.log("Caracteristicas extraidas con exito");
      return faceDescriptors;// devolvemos el arreglo con las caracteristicas de todas las caras, cada una en un elemento del arreglo
    } else {
      console.log("No se encontraron caras en la imagen");
      return 0;
    }
  } catch (err) {
    console.error(err);
  }
}

module.exports = obtenerCaracteristicasFaciales;
