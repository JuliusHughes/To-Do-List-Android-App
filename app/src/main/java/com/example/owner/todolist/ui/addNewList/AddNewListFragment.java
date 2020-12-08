package com.example.owner.todolist.ui.addNewList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.owner.todolist.DatabaseHelper;
import com.example.owner.todolist.R;
import com.example.owner.todolist.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;

public class AddNewListFragment extends Fragment {

    private AddNewListViewModel addNewListViewModel;
    DatabaseHelper myDb;
    Menu menu;
    boolean allowRefresh;

    public static AddNewListFragment newInstance() {
        return new AddNewListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_new_list_fragment, container, false);

        myDb = DatabaseHelper.getInstance(getActivity());
        addNewListViewModel =
                ViewModelProviders.of(this).get(AddNewListViewModel.class);
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        final Menu submenu = menu.addSubMenu("To Do Lists");
        final Cursor list_result = myDb.getAllListData();

        Button saveBtn = root.findViewById(R.id.button_save);
        final EditText addEdit = root.findViewById(R.id.edit_add_new_list);

        if (saveBtn != null)
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    boolean isInserted = myDb.insertListData(addEdit.getText().toString());

                    if (isInserted) {
                        getActivity().recreate();
                    }
                }
            });

        return root;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        //call close() of the helper class
        if (myDb != null)
            myDb.close();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addNewListViewModel = ViewModelProviders.of(this).get(AddNewListViewModel.class);
        // TODO: Use the ViewModel
    }

}
