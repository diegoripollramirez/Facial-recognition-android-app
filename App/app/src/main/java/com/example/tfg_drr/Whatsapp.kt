package com.example.tfg_drr

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class Whatsapp {

        fun enviarWhatsapp(context: Context, mobileNumber: String,nombreFoto:String) {
            val cliente=  Client()
            val url ="https://api.whatsapp.com/send?phone=${mobileNumber}&text=Foto%20de%20TFG_DRR.%20%0A%0Ahttp%3A//${cliente.getIp()}%3A${cliente.getPuerto()}/imagen%3Fnombre%3D${nombreFoto}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                this.data = Uri.parse(url)
                this.`package` = "com.whatsapp"
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
            }
        }
    }
