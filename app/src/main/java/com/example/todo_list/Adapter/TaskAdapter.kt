package com.example.todo_list.Adapter

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_list.Model.Task
import com.example.todo_list.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskAdapter(private var context: Context) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    private var tasks: MutableList<Task> = mutableListOf()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    fun setTask(tasks: MutableList<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_layout, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tasks[position]
        holder.checkBox.text = item.getDescription()
        holder.checkBox.isChecked = item.getStatus()
        val originalPaint = holder.checkBox.paintFlags
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid
        val personTaskRef = db.collection("tasks").document(currentUserID.toString()).collection("personalTask")
        holder.checkBox.setOnClickListener {
            val isChecked = holder.checkBox.isChecked
            if (isChecked) {
                // Update UI
                holder.checkBox.setBackgroundColor(context.resources.getColor(R.color.background_checkbox_ischeck))
                holder.checkBox.setTextColor(context.resources.getColor(R.color.white))
                holder.checkBox.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                // Update status to firebase
                personTaskRef.whereEqualTo("description",holder.checkBox.text)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val documentID = document.id

                                personTaskRef.document(documentID).update("status",isChecked)
                                Log.d("Status check","Status TRUE")
                            }
                        }
                    }

                    
            } else {
                holder.checkBox.setBackgroundColor(context.resources.getColor(R.color.white))
                holder.checkBox.setTextColor(context.resources.getColor(R.color.black))
                holder.checkBox.paintFlags = originalPaint
                Log.d("TextViewFlags", "Paint flags: ${holder.checkBox.paintFlags}")
                // Update status to firebase
                personTaskRef.whereEqualTo("description",holder.checkBox.text)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val documentID = document.id

                                personTaskRef.document(documentID).update("status",isChecked)
                                Log.d("Status check","Status FALSE")
                            }
                        }
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

}