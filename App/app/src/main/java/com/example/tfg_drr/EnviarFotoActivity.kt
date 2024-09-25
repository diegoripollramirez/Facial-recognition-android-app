package com.example.tfg_drr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.io.File
import java.util.concurrent.Semaphore


class EnviarFotoActivity : AppCompatActivity() {
    private lateinit var fotoRecibida: ImageView
    private lateinit var enviarFotoButton: Button
    private lateinit var returnButton: Button
    private lateinit var listView: ListView
    private lateinit var fotoAEnviar: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enviar_foto)

        fotoRecibida= findViewById(R.id.fotoRecibida)
        enviarFotoButton=findViewById(R.id.enviarFotoButton)
        returnButton=findViewById(R.id.returnButton)
        listView = findViewById<View>(R.id.listaContactos) as ListView

        //Ajustamos el tamaño de la lista de nombres
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val maxHeight = screenHeight / 2
        val layoutParams = listView.layoutParams
        layoutParams.height = listView.height.coerceAtMost(maxHeight)
        listView.layoutParams = layoutParams


        //Recibimos los datos (foto y usuarios) en esta segunda actividad
        val intent = intent
        val extras = intent.extras
        val pathFoto = extras?.getString("fotoArchivo") as String
        val fotoArchivo = File(pathFoto)
        fotoAEnviar = BitmapFactory.decodeFile(fotoArchivo.absolutePath)
        fotoRecibida.setImageBitmap(fotoAEnviar)

        val personas:String = intent.getStringExtra("personas") as String
        if(personas != "No se ha encontrado ninguna coincidencia" && personas != "No se ha encontrado ninguna cara que comparar"){
            val jsonPersonas = JSONArray(personas)
            val personasBuilder = StringBuilder()//llevamos una lista para mostrar los nombres por pantalla
            val telefonosBuilder = StringBuilder()// y otra para tener los telefonos a los que enviar la imagen

            for (i in 0 until jsonPersonas.length()) {
                val jsonObject = jsonPersonas.getJSONObject(i)
                val nombre = jsonObject.getString("nombre")
                personasBuilder.append("$nombre ")
                val apellidos = jsonObject.getString("apellidos")
                personasBuilder.append(apellidos)
                val telefono = jsonObject.getString("telefono")
                telefonosBuilder.append(telefono)
                // Agregar una coma después del telefono, excepto para el último elemento
                if (i < jsonPersonas.length() - 1) {
                    personasBuilder.append(",")
                    telefonosBuilder.append(",")
                }
            }
            val personasString = personasBuilder.toString()
            val telefonosString = telefonosBuilder.toString()
            val personasStringArray:Array<String> = personasString.split(",").toTypedArray()
            val telefonosStringArray:Array<String> = telefonosString.split(",").toTypedArray()

            //Creamos la lista de personas a enviar la foto con opcion de eliminar los que sean erroneos y añadir los que falten desde los contactos
            val listView = findViewById<ListView>(R.id.listaContactos)
            val adapter = Adapter(this, personasStringArray)
            listView.adapter = adapter

            //Boton para enviar la imagen por whatsapp a todos los contactos incluidos en el listado
            enviarFotoButton.setOnClickListener {

                //Hacemos el listado de los telefonos marcados para enviar
                val checkedPositions = adapter.getCheckedPositions()
                val checkedTelefonos = mutableListOf<String>()

                for (position in checkedPositions) {
                    checkedTelefonos.add(telefonosStringArray[position])
                    //Enviamos las caras aceptadas a la base de datos para alimentarla con cada nueva foto
                    val thread = Thread {
                        try {

                            val cliente= Client()
                            cliente.guardarUsuario(jsonPersonas.getJSONObject(position).getString("nombre"),jsonPersonas.getJSONObject(position).getString("apellidos"),jsonPersonas.getJSONObject(position).getString("telefono"),null,jsonPersonas.getJSONObject(position).getString("caracteristicas"))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    thread.start()
                }
                //Enviamos el enlace a la foto por whatsapp a todos los numeros de las personas reconocidas y validadas
                val whatsapp= Whatsapp()
                val semaphore = Semaphore(1)
                checkedTelefonos.forEach { telefono ->
                    Thread {
                        try {
                            semaphore.acquire()
                            whatsapp.enviarWhatsapp( this, telefono, jsonPersonas.getJSONObject(0).getString("nombreFoto"))
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        } finally {
                            semaphore.release()
                        }
                    }.start()
                }
            }
        }else{
            Toast.makeText(this, personas, Toast.LENGTH_SHORT).show()
        }

        //Tras enviar la imagen volvemos a la ventana inicial por si quieren hacer otra foto
        returnButton.setOnClickListener{
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

    }

}