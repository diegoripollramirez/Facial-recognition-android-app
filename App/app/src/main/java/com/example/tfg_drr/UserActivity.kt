package com.example.tfg_drr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class UserActivity : AppCompatActivity() {

    private lateinit var fotoAEnviar: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val fotoRecibida= findViewById<ImageView>(R.id.fotoTomada)
        val nombreEditText = findViewById<EditText>(R.id.editTextNombre)
        val apellidosEditText = findViewById<EditText>(R.id.editTextApellido)
        val telefonoEditText = findViewById<EditText>(R.id.editTextTelefono)
        val saveUserButton=findViewById<Button>(R.id.saveUserButton)
        val returnButton=findViewById<Button>(R.id.returnButton)

        val intent = intent
        val extras = intent.extras
        val pathFoto = extras?.getString("fotoArchivo") as String
        val fotoArchivo = File(pathFoto)
        val foto = BitmapFactory.decodeFile(fotoArchivo.absolutePath)
        fotoRecibida.setImageBitmap(foto)
        fotoAEnviar = foto

        saveUserButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val apellidos = apellidosEditText.text.toString()
            val telefono = telefonoEditText.text.toString()

            //En android las conexiones de internet deben llevarse en un hilo secundario para evitar que estas puedan bloquear la aplicacion
            val thread = Thread {
                try {
                    val cliente= Client()
                    cliente.guardarUsuario(nombre,apellidos,telefono,fotoAEnviar,null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
            Toast.makeText(this, "Usuario enviado a la base de datos", Toast.LENGTH_SHORT).show()
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
        returnButton.setOnClickListener{
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
    }
}