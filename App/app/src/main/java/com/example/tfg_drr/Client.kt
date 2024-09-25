package com.example.tfg_drr

import android.graphics.Bitmap
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class Client  {
    private val ip="192.168.1.138"
    //private val ip="139.47.90.142"
    private val puerto=3000

    fun getIp():String{
        return ip
    }
    fun getPuerto():Int{
        return puerto
    }

    fun identificarTelefonos(foto:Bitmap): String {

        //Convertir el fichero en ByteArray
        val stream = ByteArrayOutputStream()
        foto.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val  fotoaenviar: ByteArray = stream.toByteArray()

        //Preparamos el envío para enviar con POST la imagen guardada como ByteArray
        val requestBody =fotoaenviar.toRequestBody("image/png".toMediaTypeOrNull(), 0, fotoaenviar.size)

        //Enviamos el paquete al servidor
        val request = Request.Builder()
            .url("http://$ip:$puerto/buscar_caras")
            .post(requestBody)
            .build()

        //Esperamos la respuesta
        val client = OkHttpClient()
        val response = client.newCall(request).execute()
        var respuesta=""
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                respuesta=responseBody
            }
        } else {
            Log.d("RESPONSE", "Error: ${response.code}")
        }
        return respuesta
    }

    fun guardarUsuario(nombre:String, apellido:String, telefono:String, foto: Bitmap?,caracteristicas:String?): String {
        //Convertir el fichero en ByteArray
        var  fotoaenviar: ByteArray? =null

        if(foto!=null){
            val stream = ByteArrayOutputStream()
            foto.compress(Bitmap.CompressFormat.PNG, 100, stream)
            fotoaenviar = stream.toByteArray()
        }

        //Preparamos el envío para enviar con POST la imagen guardada como ByteArray
        // Crear un objeto MultipartBody.Builder
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        // Agregar los campos del formulario
        builder.addFormDataPart("nombre", nombre)
        builder.addFormDataPart("apellido", apellido)
        builder.addFormDataPart("telefono", telefono)

        // Agregar la foto como un RequestBody separado y especificar el tipo de archivo
        if(fotoaenviar!=null){
            val fotoRequestBody =
                fotoaenviar.toRequestBody("image/png".toMediaTypeOrNull(), 0, fotoaenviar.size)
            builder.addFormDataPart("foto", "foto.png", fotoRequestBody)
        }


        if(caracteristicas!=null){
            builder.addFormDataPart("caracteristicas", caracteristicas)
        }

        // Crear el objeto MultipartBody final
        val requestBody = builder.build()

        // Crear el objeto Request con el objeto MultipartBody
        val request = Request.Builder()
            .url("http://$ip:$puerto/guardar_usuario")
            .post(requestBody)
            .build()

        //Esperamos la respuesta
        val client = OkHttpClient()
        val response = client.newCall(request).execute()
        var respuesta=""

        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                respuesta=responseBody.toString()
            }
        } else {
            respuesta="No se recibe respuesta del servidor"
            Log.d("RESPONSE", "Error: ${response.code}")
        }

        return respuesta
    }

}