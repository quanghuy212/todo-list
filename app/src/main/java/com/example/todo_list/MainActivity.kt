package com.example.todo_list



import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_list.Adapter.TaskAdapter
import com.example.todo_list.Interface.DialogCloseListenr
import com.example.todo_list.Model.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class MainActivity : AppCompatActivity(), DialogCloseListenr  {

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var addTaskFAB: FloatingActionButton
    private lateinit var todoList: MutableList<Task>

    // Init firestore
    private var db = Firebase.firestore
    // Init firebase auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoList = mutableListOf()

        addTaskFAB = findViewById(R.id.addTaskFAB)
        taskRecyclerView = findViewById(R.id.taskRecyclerView)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth

        adapter = TaskAdapter(this)
        taskRecyclerView.adapter = adapter
        loadTasks()

        // Init ItemHelperCallback
        val itemTouchHelperCallback = object : ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition

                    when(direction) {
                        ItemTouchHelper.LEFT -> {
                            deleteTask(position)
                        }
                        ItemTouchHelper.RIGHT -> {
                            editTask(position)
                        }
                    }
                }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator
                    .Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.delete_color))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.edit_color))
                    .addSwipeRightActionIcon(R.drawable.ic_edit)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(taskRecyclerView)

        addTaskFAB.setOnClickListener{
            // In your MainActivity or any other Activity/Fragment
            val addNewTaskFragment = AddNewTask()
            addNewTaskFragment.show(supportFragmentManager, "AddNewTask")

        }


    }

    private fun loadTasks() {
        // Get userID from Firebase Auth
        val currentID = auth.currentUser?.uid
        // Get data task
        currentID?.let { id ->
            // Read data
            db.collection("tasks").document(id).collection("personalTask")
                .whereEqualTo("status",false)
                .get()
                .addOnSuccessListener { result ->
                    todoList.clear()
                    for (document in result) {
                        val task = Task()  // Create a new Task object for each document
                        document.data.let {
                            task.setStatus(it["status"] as? Boolean ?: false) // Handle nullable Boolean
                            task.setDescription(it["description"] as? String ?: "") // Handle nullable String
                            Log.d(TAG, "Task: status = ${task.getStatus()}, description = ${task.getDescription()}")
                            todoList.add(task)
                            Log.d(TAG, "Add done ${todoList.size}")
                        }
                    }
                    Log.d(TAG, "READ SUCCESS")
                    Toast.makeText(this, "READ SUCCESS", Toast.LENGTH_LONG).show()
                    adapter.setTask(todoList) // Notify adapter after all items are added
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "READ FAILURE", exception)
                    Toast.makeText(this, "READ FAIL", Toast.LENGTH_LONG).show()
                }
        } ?: run {
            Log.e(TAG, "User ID is null")
            Toast.makeText(this, "User ID is null", Toast.LENGTH_LONG).show()
        }
    }
    override fun onStart() {
        super.onStart()
        // Check login user
        val currentUser = auth.currentUser

        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun deleteTask(position: Int) {
        val currentUserID = auth.currentUser?.uid
        val personalTaskRef = db.collection("tasks").document(currentUserID.toString()).collection("personalTask")
        val task = todoList[position]
        AlertDialog.Builder(this)
            .setTitle("Confirm to delete!")
            .setMessage("Do you wanna delete this task?")
            .setPositiveButton("Yes") { dialog, which ->


                // Delete at Firebase
                personalTaskRef.whereEqualTo("description",task.getDescription())
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val documentID = document.id

                                personalTaskRef.document(documentID).delete()
                                Log.d("Doc check","DELETE DOCUMENT: ${documentID}")
                            }
                        }
                    }
                todoList.removeAt(position)
                taskRecyclerView.adapter?.notifyItemRemoved(position)
                Log.d("List task","size = ${todoList.size}")
            }
            .setNegativeButton("No") { dialog, which ->
                taskRecyclerView.adapter?.notifyItemChanged(position)
            }
            .create()
            .show()
    }

    private fun editTask(position: Int) {
        val currentUserID = auth.currentUser?.uid
        val personalTaskRef = db.collection("tasks").document(currentUserID.toString()).collection("personalTask")
        val task = todoList[position]

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit task")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, which ->
            // Update in firebase
            personalTaskRef.whereEqualTo("description",task.getDescription())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val documentID = document.id

                            personalTaskRef.document(documentID).update("description",input.text.toString())
                            Log.d("Check UPDATE","Update document ${documentID}")
                        }
                    }
                }
            task.setDescription(input.text.toString())
            taskRecyclerView.adapter?.notifyItemChanged(position)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            taskRecyclerView.adapter?.notifyItemChanged(position)
            dialog.cancel()
        }

        builder.create().show()
    }

    override fun handleDialogClose(dialog: DialogInterface) {
        val currentID = auth.currentUser?.uid
        currentID?.let { id ->
            // Read data
            db.collection("tasks").document(id).collection("personalTask")
                .whereEqualTo("status",false)
                .get()
                .addOnSuccessListener { result ->
                    todoList.clear()
                    for (document in result) {
                        val task = Task()  // Create a new Task object for each document
                        document.data.let {
                            task.setStatus(it["status"] as? Boolean ?: false) // Handle nullable Boolean
                            task.setDescription(it["description"] as? String ?: "") // Handle nullable String
                            Log.d(TAG, "Task: status = ${task.getStatus()}, description = ${task.getDescription()}")
                            todoList.add(task)
                            Log.d(TAG, "Add done ${todoList.size}")
                        }
                    }
                    Log.d(TAG, "READ SUCCESS")
                    Toast.makeText(this, "READ SUCCESS", Toast.LENGTH_LONG).show()
                    adapter.setTask(todoList) // Notify adapter after all items are added
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "READ FAILURE", exception)
                    Toast.makeText(this, "READ FAIL", Toast.LENGTH_LONG).show()
                }
        } ?: run {
            Log.e(TAG, "User ID is null")
            Toast.makeText(this, "User ID is null", Toast.LENGTH_LONG).show()
        }
    }
}
