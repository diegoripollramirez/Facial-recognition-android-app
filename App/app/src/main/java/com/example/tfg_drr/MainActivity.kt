package com.example.tfg_drr

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var foto: ImageView
    private lateinit var fotoAEnviar: Bitmap
    private lateinit var fotoArchivo: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tomarFotoButton=findViewById<Button>(R.id.takePictureButton)
        val saveUserButton=findViewById<Button>(R.id.saveUserButton)
        val irActivityEnviarFotoButton=findViewById<Button>(R.id.goToSendPhotoButton)
        foto= findViewById(R.id.fotoTomada)

        //Pedimos permisos si no los tuvieramos ya
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),111)
        }

        //Hacemos la foto
        tomarFoto()
        //Boton para repetir la foto
        tomarFotoButton.setOnClickListener {
            tomarFoto()
        }

        //Boton para guardar la cara en la base de datos junto con los datos personales
        saveUserButton.setOnClickListener {

            // Actualizar la interfaz de usuario con el valor de telefonos
            val intentEnviarFoto= Intent (this, UserActivity::class.java)
            intentEnviarFoto.putExtra("fotoArchivo",fotoArchivo.absolutePath)
            startActivity(intentEnviarFoto)
        }

        //Boton para aceptar la imagen y pasar a la siguiente actividad
        irActivityEnviarFotoButton.setOnClickListener {
            val progressDialog = ProgressDialog.show(this, "", "Identificando personas...", true)
            //En android las conexiones de internet deben llevarse en un hilo secundario para evitar que estas puedan bloquear la aplicacion
            val thread = Thread {
                try {//La clase cliente envia la foto al resvidor y devuelve los telefonos de las caras identificadas
                    var personas= Client().identificarTelefonos(fotoAEnviar)
                    runOnUiThread {
                        // Actualizar la interfaz de usuario con el valor de telefonos
                        progressDialog.dismiss()
                        val intentEnviarFoto= Intent (this, EnviarFotoActivity::class.java)
                        intentEnviarFoto.putExtra("fotoArchivo",fotoArchivo.absolutePath)
                        intentEnviarFoto.putExtra("personas",personas)
                        startActivity(intentEnviarFoto)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    progressDialog.dismiss()
                }
            }
            thread.start()
        }
    }

    private fun tomarFoto() {
        val intentTomarFoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fotoArchivo = crearArchivoFoto()

        if (intentTomarFoto.resolveActivity(packageManager) != null) {
            val uri = FileProvider.getUriForFile(
                this,
                "com.example.tfg_drr.fileprovider",
                fotoArchivo
            )
            intentTomarFoto.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            tomarFotoLauncher.launch(intentTomarFoto)
        } else {
            Toast.makeText(this, "No se puede tomar foto", Toast.LENGTH_SHORT).show()
        }
    }

    private val tomarFotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fotoAEnviar = BitmapFactory.decodeFile(fotoArchivo.absolutePath)
            foto.setImageBitmap(fotoAEnviar)

        } else {
            Toast.makeText(this, "Error al tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearArchivoFoto(): File {
        val nombreArchivo = "foto_${System.currentTimeMillis()}.jpg"
        val directorioAlmacenamiento: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw java.lang.Exception("No se pudo acceder al almacenamiento externo")
        return File.createTempFile(nombreArchivo, ".jpg", directorioAlmacenamiento)
    }




}