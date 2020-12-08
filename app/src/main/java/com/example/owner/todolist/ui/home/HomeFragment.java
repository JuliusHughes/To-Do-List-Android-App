package com.example.owner.todolist.ui.home;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.owner.todolist.DatabaseHelper;
import com.example.owner.todolist.Model.ToDoList;
import com.example.owner.todolist.R;
import com.example.owner.todolist.ui.ToDoListFragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    DatabaseHelper myDb;
    ArrayList<ToDoList> arrayList;
    ArrayList<String> list;
    ArrayAdapter adapter;
    ListView listView;
    String selection;
    boolean allowRefresh;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        myDb = DatabaseHelper.getInstance(getActivity());
        list = new ArrayList<String>();
        ToDoList toDoList;

        setHasOptionsMenu(true);

        final Cursor list_result = myDb.getAllListData();


        listView = root.findViewById(R.id.toDoList_list);
        viewData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String text = listView.getItemAtPosition(i).toString();
                ToDoListFragment fragment = new ToDoListFragment();

                while (list_result.moveToNext())
                {
                    String title = list_result.getString(1);

                    if (title.equals(text))
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("fragment_title", title);
                        fragment.setArguments(bundle);

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(R.id.content_frame, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }

                Toast.makeText(getActivity(), ""+text, Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String toDoList = listView.getItemAtPosition(position).toString();
                createDialog(view, toDoList);

                return false;
            }
        });

        return root;
    }

    public void createDialog(View view, final String toDoList){

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        final String[] choices = getActivity().getResources().getStringArray(R.array.edit_list);
        //adb.setView(Main.this);
        adb.setTitle("To Do List Options");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setSingleChoiceItems(R.array.edit_list, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selection = choices[which];

            }
        });
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();

                if (selectedPosition == 0)
                {
                    AlertDialog.Builder editList = new AlertDialog.Builder(getActivity());
                    editList.setTitle("Type To Do List Title");

// Set up the input
                    final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    editList.setView(input);

// Set up the buttons
                    editList.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String list_text = input.getText().toString();

                            Cursor list_result = myDb.getAllListData();

                            while (list_result.moveToNext()) {
                                if (toDoList.equals(list_result.getString(1))) {
                                    boolean isUpdated = myDb.updateListData(list_result.getString(0), list_text);

                                }
                                getActivity().recreate();
                            } //end while
                        }
                    });
                    editList.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    editList.show();

                }
                else if (selectedPosition == 1)
                {
                    AlertDialog.Builder deleteList = new AlertDialog.Builder(getActivity());
                    deleteList.setTitle("Delete List?");
                    deleteList.setIcon(android.R.drawable.ic_dialog_alert);

                    deleteList.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Cursor list_result = myDb.getAllListData();

                            while (list_result.moveToNext()) {
                                if (toDoList.equals(list_result.getString(1))) {
                                    Cursor task_result = myDb.getAllTaskData(list_result.getString(0));
                                    myDb.deleteToDoList(list_result.getString(0));

                                    while (task_result.moveToNext()) {
                                        if (list_result.getString(0).equals(task_result.getString(3)))
                                            myDb.deleteTask(task_result.getString(0));
                                    }
                                }
                                getActivity().recreate();
                            } //end while
                        }});

                    deleteList.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        } });

                    deleteList.show();
                    }
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
        Cursor cursor = myDb.getAllListData();

        if (cursor.getCount() == 0)
            Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
        else
        {
            while (cursor.moveToNext())
            {
                list.add(cursor.getString(1));
            }

            adapter = new ArrayAdapter<>(getActivity(), R.layout.list_element, list);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Home");

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

        final String[] choices = getActivity().getResources().getStringArray(R.array.sort_list);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Sort By: ");
        dialog.setSingleChoiceItems(R.array.sort_list, -1, new DialogInterface.OnClickListener() {
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
                    Cursor cursor = myDb.sortListByNameASC();

                    list.clear();

                    if (cursor.getCount() == 0)
                        Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                    else
                    {
                        while (cursor.moveToNext())
                        {
                            list.add(cursor.getString(1));
                        }

                        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_element, list);
                        listView.setAdapter(adapter);
                    }

                }
                else if (selectedPosition == 1)
                {
                    Cursor cursor = myDb.sortListByNameDESC();

                    list.clear();

                    if (cursor.getCount() == 0)
                        Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                    else
                    {
                        while (cursor.moveToNext())
                        {
                            list.add(cursor.getString(1));
                        }

                        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_element, list);
                        listView.setAdapter(adapter);
                    }

                }
                else if (selectedPosition == 2)
                {
                    Cursor cursor = myDb.sortListByCreatedASC();

                    list.clear();

                    if (cursor.getCount() == 0)
                        Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                    else
                    {
                        while (cursor.moveToNext())
                        {
                            list.add(cursor.getString(1));
                        }

                        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_element, list);
                        listView.setAdapter(adapter);
                    }
                }
                else if (selectedPosition == 3)
                {
                    Cursor cursor = myDb.sortListByCreatedDESC();

                    list.clear();

                    if (cursor.getCount() == 0)
                        Toast.makeText(getActivity(), "No data to show", Toast.LENGTH_SHORT).show();
                    else
                    {
                        while (cursor.moveToNext())
                        {
                            list.add(cursor.getString(1));
                        }

                        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_element, list);
                        listView.setAdapter(adapter);
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
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_home)
        {
            showRadioButtonDialog();
            return true;
        }

        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }
}