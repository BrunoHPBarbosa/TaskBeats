package com.comunidadedevspace.taskbeats

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable


class MainActivity : AppCompatActivity() {
    //kotlin
   private val taskList = arrayListOf(
        Task(0,"Academia", "Treinar as 20h"),
        Task(1,"Mercado", "Comprar arroz e macarrao"),
        Task(2,"Curso", "Estudar 4h DevSpace"),
        Task(3,"Trabalho", "Entras as 8:30h"),
    )
    private lateinit var ctncontent: LinearLayout

    //adapter
    private val adapter: TaskListAdapter = TaskListAdapter(::openTaskDetailView)


   private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
       if (result.resultCode == Activity.RESULT_OK) {
           //pegando o resultado
           val data = result.data
           val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction
           val task: Task = taskAction.task

           //removendo item da lista kotlin
           taskList.remove(task)

           if (taskList.size == 0){
               ctncontent.visibility= View.VISIBLE
           }


       adapter.submit(taskList)
     }
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ctncontent = findViewById(R.id.ctn_content)

//recyclerview
        val rvTasks: RecyclerView = findViewById(R.id.rv_task_list)
        rvTasks.adapter = adapter
    }

    private fun openTaskDetailView(task:Task) {
        val intent = TaskDetailActivit.start(this, task)
        startForResult.launch(intent)


    }
}
       sealed class ActionType:Serializable{
           object DELETE: ActionType()
           object UPDATE: ActionType()
           object CREATE: ActionType()
       }

data class TaskAction(
    val task:Task,
    val actiontype: ActionType
):Serializable

const val TASK_ACTION_RESULT =  "TASK_ACTION_RESULT"