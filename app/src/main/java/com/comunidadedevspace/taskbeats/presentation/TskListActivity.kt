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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.data.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable


class TaskListActivity : AppCompatActivity() {

    private lateinit var ctncontent: LinearLayout

    //adapter
    private val adapter: TaskListAdapter by lazy {
        TaskListAdapter(::onListItemCliked)
    }
    private val viewModel: TaskListViewModel by lazy {
        TaskListViewModel.create(application)
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //pegando o resultado
            val data = result.data
            val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction

            viewModel.execute(taskAction)
            }
        }
        override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_task_list)
             setSupportActionBar(findViewById(R.id.toolbar))
               ctncontent = findViewById(R.id.ctn_content)


//recyclerview
        val rvTasks: RecyclerView = findViewById(R.id.rv_task_list)
        rvTasks.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            openTaskListDetail(null)

        }
    }

    override fun onStart() {
        super.onStart()
        listFromDataBase()
    }

    private fun deleteAll(){
        val taskAction = TaskAction(null,ActionType.DELETE_ALL.name)
        viewModel.execute(taskAction)

    }


    private fun listFromDataBase() {
            //Observer
            val listObserver = Observer<List<Task>>{listTasks ->
                if(listTasks.isEmpty()){
                   ctncontent.visibility = View.VISIBLE
                }else{
                    ctncontent.visibility = View.GONE
                }
                adapter.submitList(listTasks)
            }

            //Live Data
             viewModel.taskListLiveData.observe(this@TaskListActivity,listObserver)
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
           DELETE_ALL,
           UPDATE,
           CREATE

       }

data class TaskAction(
    val task: Task?,
    val actiontype: String
):Serializable

const val TASK_ACTION_RESULT =  "TASK_ACTION_RESULT"