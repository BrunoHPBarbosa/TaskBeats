package com.comunidadedevspace.taskbeats.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.data.AppDataBase
import com.comunidadedevspace.taskbeats.data.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.Serializable


class MainActivity : AppCompatActivity() {

    private lateinit var ctncontent: LinearLayout

    //adapter
    private val adapter: TaskListAdapter by lazy {
        TaskListAdapter(::onListItemCliked)
    }
    private val dataBase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "taskbeats-database"
        ).build()
    }

    private val dao by lazy {
        dataBase.taskDao()
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //pegando o resultado
            val data = result.data
            val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction
            val task: Task = taskAction.task

            when (taskAction.actiontype) {
                ActionType.DELETE.name -> deleteById(task.id)
                ActionType.CREATE.name -> insertIntoDataBase(task)
                ActionType.UPDATE.name -> updateIntoDataBase(task)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        listFromDataBase()
        ctncontent = findViewById(R.id.ctn_content)


//recyclerview
        val rvTasks: RecyclerView = findViewById(R.id.rv_task_list)
        rvTasks.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            openTaskListDetail(null)

        }
    }

    private fun deleteAll(){
        CoroutineScope(IO).launch{
            dao.deleteAll()
            listFromDataBase()
        }
    }

    private fun deleteById(id:Int){
           CoroutineScope(IO).launch{
             dao.deleteById(id)
             listFromDataBase()
        }
    }

    private fun insertIntoDataBase(task: Task) {
        CoroutineScope(IO).launch {
            dao.insert(task)
            listFromDataBase()
        }

    }

    private fun updateIntoDataBase(task: Task) {
        CoroutineScope(IO).launch {
            dao.update(task)
            listFromDataBase()

        }
    }

    private fun listFromDataBase() {
        CoroutineScope(IO).launch {
            val myDataBaseList: List<Task> = dao.getAll()
            adapter.submitList(myDataBaseList)
        }
    }

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    private fun onListItemCliked(task: Task) {
        openTaskListDetail(task)
    }

    private fun openTaskListDetail(task: Task?) {
        val intent = TaskDetailActivit.start(this, task)
        startForResult.launch(intent)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.task_list, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_task -> {
                deleteAll()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}



//crud
       enum class ActionType{
           DELETE,
           UPDATE,
           CREATE

       }

data class TaskAction(
    val task: Task,
    val actiontype: String
):Serializable

const val TASK_ACTION_RESULT =  "TASK_ACTION_RESULT"