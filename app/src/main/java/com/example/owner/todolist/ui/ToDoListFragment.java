package com.example.owner.todolist.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.owner.todolist.Adapters.TaskAdapter;
import com.example.owner.todolist.AddTask;
import com.example.owner.todolist.DatabaseHelper;
import com.example.owner.todolist.EditTask;
import com.example.owner.todolist.Model.Task;
import com.example.owner.todolist.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ToDoListFragment extends Fragment implements TaskAdapter.CheckboxCheckedListener{
    ListView listView;
    Bundle bundle;
    String title;
    String list_id;  //To do list ID
    Button addTask;
    EditText taskName;
    ArrayList<String> list;
    ArrayList<Task> taskList = new ArrayList<Task>();
    TaskAdapter taskAdapter;
    boolean allowRefresh;
    Bundle task_bundle; //bundle object to transfer task data to EditTask activity
    String selection;

    public ToDoListFragment()
    {

    }


    private ToDoListViewModel mViewModel;
    DatabaseHelper myDb;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.to_do_list_fragment, container, false);
        listView = view.findViewById(R.id.task_list);
        addTask = view.findViewById(R.id.btn_add_task);
        taskName = view.findViewById(R.id.edit_task_name);
        task_bundle = new Bundle();
        taskAdapter = new TaskAdapter(getActivity(), R.layout.task_list_element, new ArrayList<Task>());
        listView.setAdapter(taskAdapter);


        setHasOptionsMenu(true);

        list = new ArrayList<String>();


        myDb = DatabaseHelper.getInstance(getActivity());

        bundle = getArguments();

        if (bundle != null)
        {
            title = bundle.getString("fragment_title"); //gets to do list title from bundle object
        }

        //gets the to do list id from the database
        int get_id = 0;
        final Cursor list_result = myDb.getAllListData();
        while (list_result.moveToNext())
        {
            String list_title = list_result.getString(1);

            if (list_title.equals(title))
                get_id = list_result.getInt(0);

            list_id = Integer.toString(get_id);
        }

        AddData();

        viewData();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent I =  new Intent(getActivity(), EditTask.class);
                String task_name = taskAdapter.getItem(i).getTaskName();
                int get_task_id = 0;
                String task_id;

                task_bundle.putString("intent_name", "To Do List Fragment");


                Cursor task_result = myDb.getAllTaskData(list_id);

                while (task_result.moveToNext())
                {
                    if (task_name.equals(task_result.getString(1)))
                    {
                        get_task_id = task_result.getInt(0);
                        task_id = Integer.toString(get_task_id);
                        task_bundle.putString("task_name", task_name);
                        task_bundle.putString("list_title", title);
                        task_bundle.putInt("id", get_task_id);

                        I.putExtra("task", task_bundle);
                        I.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(I);
                    }

                } //end while

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String task_name = taskAdapter.getItem(position).getTaskName();
                createDialog(view, task_name);


                return true;
            }
        });


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I =  new Intent(getActivity(), AddTask.class);
                Bundle b = new Bundle();
                b.putString("list_title", title);
                I.putExtra("to_do_list",b);
                startActivity(I);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return view;
    }

    public void AddData() {
        addTask.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!taskName.getText().toString().equals("Quick Task"))
                        {
                            boolean isInserted = myDb.insertTaskData(taskName.getText().toString(), list_id, "", false, "");
                            if (isInserted) {
                                Toast.makeText(getActivity(), "Data Inserted",
                                        Toast.LENGTH_LONG).show();
                                bundle.putString("fragment_title", title);
                                ToDoListFragment fragment = new ToDoListFragment();

                                fragment.setArguments(bundle);

                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction transaction = fm.beginTransaction();
                                transaction.replace(R.id.content_frame, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();


                                try {
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }

                            } else
                                Toast.makeText(getActivity(), "Data not Inserted",
                                        Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }


    //dialog box to delete task
    public void createDialog(View view, final String task){

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        //adb.setView(Main.this);
        adb.setTitle("Delete task?");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                task_bundle.putString("intent_name", "To Do List Fragment");

                Cursor task_result = myDb.getAllTaskData(list_id);

                while (task_result.moveToNext())
                {
                    if (task.equals(task_result.getString(1)))
                    {
                        myDb.deleteTask(task_result.getString(0));
                    }
                    getActivity().recreate();
                } //end while
            } });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            } });

        AlertDialog alertDialog = adb.create();
        alertDialog.show();

    }

    private void viewData()
    {
        Cursor cursor = myDb.getAllTaskData(list_id);


        if (cursor.getCount() == 0)
            Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
        else
            {
                while (cursor.moveToNext())
                {
                    Task task = new Task(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4) > 0, cursor.getString(6));
                    taskAdapter.add(task);
                }

            taskAdapter.setCheckedListener(ToDoListFragment.this);
            taskAdapter.notifyDataSetChanged();
        }

    }



    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(title);
        if (allowRefresh)
        {
            allowRefresh = false;
            getActivity().recreate();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (!allowRefresh)
            allowRefresh = true;
    }


    private void showRadioButtonDialog() {

        final String[] choices = getActivity().getResources().getStringArray(R.array.sort_tasks);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Sort By: ");
        dialog.setSingleChoiceItems(R.array.sort_tasks, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selection = choices[which];

            }
        });

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                Toast.makeText(getActivity(), "Sorted by "+selection, Toast.LENGTH_SHORT).show();
                Object checkedItem = ((AlertDialog)dialog).getListView().getAdapter().getItem(((AlertDialog)dialog).getListView().getCheckedItemPosition());

                if (selectedPosition == 0)
                {
                    Cursor cursor = myDb.sortTaskByCreatedASC(list_id);

                    taskList.clear();

                    if (cursor.getCount() == 0)
                        Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                    else
                    {
                        while (cursor.moveToNext())
                        {
                            Task task = new Task(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4) > 0, cursor.getString(5));
                            taskList.add(task);
                        }

                        taskAdapter = new TaskAdapter(getActivity(), R.layout.task_list_element, taskList);
                        listView.setAdapter(taskAdapter);
                    }

                }
                else if (selectedPosition == 1)
                {
                    Cursor cursor = myDb.sortTaskByCreatedDESC(list_id);

                    taskList.clear();

                    if (cursor.getCount() == 0)
                        Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                    else
                    {
                        while (cursor.moveToNext())
                        {
                            Task task = new Task(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4) > 0, cursor.getString(5));
                            taskList.add(task);
                        }

                        taskAdapter = new TaskAdapter(getActivity(), R.layout.task_list_element, taskList);
                        listView.setAdapter(taskAdapter);
                    }

                }
                else if (selectedPosition == 2)
                {
                    Cursor cursor = myDb.sortTaskByDueDateASC(list_id);

                    taskList.clear();

                    if (cursor.getCount() == 0)
                        Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                    else {
                        while (cursor.moveToNext()) {
                            Task task = new Task(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4) > 0, cursor.getString(5));
                            taskList.add(task);
                        }

                        taskAdapter = new TaskAdapter(getActivity(), R.layout.task_list_element, taskList);
                        listView.setAdapter(taskAdapter);
                    }
                }
                else if (selectedPosition == 3)
                {
                    Cursor cursor = myDb.sortTaskByDueDateDESC(list_id);

                    taskList.clear();

                    if (cursor.getCount() == 0)
                        Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                    else {
                        while (cursor.moveToNext()) {
                            Task task = new Task(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4) > 0, cursor.getString(5));
                            taskList.add(task);
                        }

                        taskAdapter = new TaskAdapter(getActivity(), R.layout.task_list_element, taskList);
                        listView.setAdapter(taskAdapter);
                    }
                }
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });



        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_to_do_list, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort:
                //sort tasks
                Toast.makeText(getActivity(), "Sort was clicked",
                        Toast.LENGTH_LONG).show();
                showRadioButtonDialog();

                return true;

            case R.id.action_remove_completed:
                //remove completed tasks
                Toast.makeText(getActivity(), "Remove completed was clicked",
                        Toast.LENGTH_LONG).show();
                Cursor cursor = myDb.getAllTaskData(list_id);

                if (cursor.getCount() == 0)
                    Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                else
                    {
                        while (cursor.moveToNext()) {
                            if (cursor.getInt(4) == 1)
                                myDb.deleteTask(cursor.getString(0));
                    }
                }

                getActivity().recreate();
                return true;

            case R.id.action_completed_all:
                //check all tasks
                Toast.makeText(getActivity(), "Completed all was clicked",
                        Toast.LENGTH_LONG).show();

                Cursor cursor2 = myDb.getAllTaskData(list_id);

                if (cursor2.getCount() == 0)
                    Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                else
                {
                    while (cursor2.moveToNext()) {
                            myDb.updateTaskData(cursor2.getString(0), cursor2.getString(1), list_id, cursor2.getString(3),
                                    true, cursor2.getString(5));
                    }
                }

                getActivity().recreate();

                return true;

            case R.id.action_unchecked:
                //uncheck all tasks
                Toast.makeText(getActivity(), "Uncheck all was clicked",
                        Toast.LENGTH_LONG).show();

                Cursor cursor3 = myDb.getAllTaskData(list_id);

                if (cursor3.getCount() == 0)
                    Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                else
                {
                    while (cursor3.moveToNext()) {
                        myDb.updateTaskData(cursor3.getString(0), cursor3.getString(1), list_id, cursor3.getString(3),
                                false, cursor3.getString(5));
                    }
                }

                getActivity().recreate();
                
                return true;
        }

        return false;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ToDoListViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void getCheckboxCheckedListener(int position) {
//        Toast.makeText(getActivity(), "Task: "+taskAdapter.getItem(position).getTaskName(), Toast.LENGTH_SHORT).show();
    }
}
