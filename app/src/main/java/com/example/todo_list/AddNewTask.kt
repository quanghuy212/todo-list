package com.example.todo_list

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.example.todo_list.Interface.DialogCloseListenr
import com.example.todo_list.Model.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

/*
class AddNewTask : BottomSheetDialogFragment() {

    private lateinit var newTaskEditText: EditText
    private lateinit var addTaskButton: Button
    private var listener: OnTaskAddedListener? = null

    // Init firestore
    private var db = Firebase.firestore
    // Init firebase auth
    private lateinit var auth: FirebaseAuth

    interface OnTaskAddedListener {
        fun onTaskAdded()
    }

    fun setOnTaskAddedListener(listener: OnTaskAddedListener) {
        this.listener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_task,container, false)
        newTaskEditText = view.findViewById(R.id.newTaskEditText)
        addTaskButton = view.findViewById(R.id.addTaskButton)

        addTaskButton.setOnClickListener {
            addTask()
        }

        return view
    }

    private fun addTask() {
        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth
        val currentUserID = auth.currentUser?.uid
        val taskDesc = newTaskEditText.text.toString().trim()

        if (taskDesc.isNotEmpty()) {
            val newTask = Task().apply {
                setStatus(false)
                setDescription(taskDesc)
            }

            // Create a map to store the task details
            val taskMap = hashMapOf(
                "status" to newTask.getStatus(),
                "description" to newTask.getDescription()
            )

            db.collection("tasks").document(currentUserID.toString()).collection("personalTask")
                .add(taskMap)
                .addOnSuccessListener {
                    listener?.onTaskAdded()
                    dismiss()
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        } else {
            newTaskEditText.error = "Task description is empty"
        }
    }
}*/
class AddNewTask : BottomSheetDialogFragment() {
    private lateinit var newTaskEditText: EditText
    private lateinit var addTaskButton: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    fun newInstance(): AddNewTask {
        return AddNewTask()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_task, container, false)
        dialog?.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newTaskEditText = view.findViewById(R.id.newTaskEditText)
        addTaskButton = view.findViewById(R.id.addTaskButton)

        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth
        val currentUserID = auth.currentUser?.uid
        val taskDesc = newTaskEditText.text.toString().trim()
        val isUpdate = false

        addTaskButton.setOnClickListener {
            val taskDesc = newTaskEditText.text.toString().trim()
            if (taskDesc.isNotEmpty()) {
                val newTask = Task().apply {
                    setStatus(false)
                    setDescription(taskDesc)
                }

                // Create a map to store the task details
                val taskMap = hashMapOf(
                    "status" to newTask.getStatus(),
                    "description" to newTask.getDescription()
                )

                db.collection("tasks").document(auth.currentUser?.uid.toString()).collection("personalTask")
                    .add(taskMap)
                    .addOnSuccessListener {
                        dismiss()
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
            } else {
                newTaskEditText.error = "Task description is empty"
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity = activity
        if (activity is DialogCloseListenr) {
            activity.handleDialogClose(dialog)
        }
    }
}
