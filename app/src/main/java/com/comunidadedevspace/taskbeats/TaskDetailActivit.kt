package com.comunidadedevspace.taskbeats

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class TaskDetailActivit : AppCompatActivity() {

    private lateinit var task: Task

    companion object {
     private const  val Task_DETAIL_EXTRA = "task.extra.detail"

        fun start(context: Context, task:Task): Intent {
            val intent = Intent(context,TaskDetailActivit::class.java)
                .apply{
                    putExtra(Task_DETAIL_EXTRA, task)
                }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        //recupera a string da tela anterior

        task= requireNotNull(intent.getSerializableExtra(Task_DETAIL_EXTRA) as Task?)


        //recuperar campo do xml
        val tvTitle = findViewById<TextView>(R.id.tv_task_title_detail)

        tvTitle.text = task.title

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_detail,menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.Delete_Task_Detail -> {
               val intent= Intent()
                    .apply {
                        val actionType= ActionType.DELETE
                        val taskAction= TaskAction(task, actionType)
                        putExtra(TASK_ACTION_RESULT, taskAction)
                    }
                setResult(RESULT_OK, intent)
                finish()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }


        }


    }
}



