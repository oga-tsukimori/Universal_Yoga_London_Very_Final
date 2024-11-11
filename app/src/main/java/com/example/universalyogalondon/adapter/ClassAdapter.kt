package com.example.universalyogalondon.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyogalondon.R
import com.example.universalyogalondon.data.db.entry.ClassEntry
import com.example.universalyogalondon.model.ClassInfo
import java.text.SimpleDateFormat
import java.util.*

class ClassAdapter(private val classes: MutableList<ClassEntry>,var delegate : (ClassEntry, String,Int) -> Unit) : RecyclerView.Adapter<ClassAdapter.ViewHolder>() {

    fun update(datalist : List<ClassEntry>) {
        classes.clear()
        classes.addAll(datalist)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val classNameTextView: TextView = view.findViewById(R.id.classNameTextView)
        val teacherNameTextView: TextView = view.findViewById(R.id.teacherNameTextView)
        val image : ImageView = view.findViewById(R.id.imvClass)
        val btnEdit: RelativeLayout = view.findViewById(R.id.rlEdit)
        val btnDelete: RelativeLayout = view.findViewById(R.id.rlDelete)
        //val dateTextView: TextView = view.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classInfo = classes[position]
        holder.classNameTextView.text = classInfo.className
        holder.teacherNameTextView.text = classInfo.teacherName
        holder.image.setImageURI(Uri.parse(classInfo.image))

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        //holder.dateTextView.text = dateFormat.format(Date(classInfo.date))

        holder.btnEdit.setOnClickListener {
            delegate.invoke(classInfo,"edit",position)
        }

        holder.btnDelete.setOnClickListener {
            delegate.invoke(classInfo,"delete",position)
        }

    }

    override fun getItemCount() = classes.size
}
