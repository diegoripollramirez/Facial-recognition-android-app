package com.example.tfg_drr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView

class Adapter(context: Context, items: Array<String>) :
    ArrayAdapter<String>(context, 0, items) {

    //Lista para controlar cuales de los checkboxes estan marcados
    private val checkedPositions = mutableListOf<Int>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listado_telefonos, parent, false)
            holder = ViewHolder(view.findViewById(R.id.check_box), view.findViewById(R.id.text_view))
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.textView.text = getItem(position)
        holder.checkBox.isChecked = checkedPositions.contains(position)

        //añadimos a la variable de control las posiciones de los checkboxes marcados
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!checkedPositions.contains(position)) {
                    checkedPositions.add(position)
                }
            } else {
                checkedPositions.remove(position)
            }
        }

        return view!!
    }
    fun getCheckedPositions(): MutableList<Int> {
        return checkedPositions
    }

    private class ViewHolder(val checkBox: CheckBox, val textView: TextView)
}
