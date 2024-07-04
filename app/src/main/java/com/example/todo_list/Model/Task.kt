package com.example.todo_list.Model

class Task() {
    private var status: Boolean = false
    private var description: String = ""

    fun getStatus(): Boolean {
        return status
    }

    fun getDescription(): String {
        return description
    }

    fun setStatus(status: Boolean?) {
        if (status != null) {
            this.status = status
        }
    }

    fun setDescription(description: String?) {
        if (description != null) {
            this.description = description
        }
    }
}
