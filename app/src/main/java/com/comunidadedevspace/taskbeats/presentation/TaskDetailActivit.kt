package com.comunidadedevspace.taskbeats.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.data.Task
import com.google.android.material.snackbar.Snackbar


class TaskDetailActivit : AppCompatActivity() {

    private var task: Task? = null
    private lateinit var btnDone: Button

    companion object {
        private const val Task_DETAIL_EXTRA = "task.extra.detail"

        fun start(context: Context, task: Task?): Intent {
            val intent = Intent(context, TaskDetailActivit::class.java)
                .apply {
                    putExtra(Task_DETAIL_EXTRA, task)
                }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

         setSupportActionBar(findViewById(R.id.toolbar))
        //recupera a string da tela anterior

        task = intent.getSerializableExtra(Task_DETAIL_EXTRA) as Task?

        val edtTitle = findViewById<EditText>(R.id.edt_task_title)
        val edtDescription = findViewById<EditText>(R.id.edt_Task_description)
         btnDone = findViewById<Button>(R.id.btn_Done)

        if(task!=null) {
            edtTitle.setText(task!!.title)
            edtDescription.setText(task!!.description)
        }

        btnDone.setOnClickListener {
            val title = edtTitle.text.toString()
            val desc = edtDescription.text.toString()

            if (title.isNotEmpty() && desc.isNotEmpty()) {
                if(task == null){
                    addOrUpdateTask(0,title,desc, ActionType.CREATE)
                }else{
                    addOrUpdateTask(task!!.id,title,desc, ActionType.UPDATE)
                }

            } else {
                showMessage(it,"Fields are required")
            }
        }
        //recuperar campo do xml
     //  tvTitle = findViewById <TextView>(R.id.tv_task_title_detail)

    //    tvTitle.text = task?.title ?: " Adicione uma Tarefa"

    }

    private fun addOrUpdateTask(
        id: Int,
        title:String,
        description:String,
        actionType: ActionType
    ) {
        val task = Task(id,title,description)
        returAction(task,actionType)
    }

    //ciclo de vida da activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_detail, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Delete_Task_Detail -> {
                if (task != null) {
                   returAction(task!!, ActionType.DELETE)

                } else {
                    showMessage(btnDone, "item not found")

                }
                true
            }

            else -> super.onOptionsItemSelected(item)
            }
        }
        private fun returAction(task: Task, actionType: ActionType) {
            val intent = Intent()
                .apply {
                    val taskAction = TaskAction(task, actionType.name)
                    putExtra(TASK_ACTION_RESULT, taskAction)
                }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        private fun showMessage(view: View, message: String) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }
    }




