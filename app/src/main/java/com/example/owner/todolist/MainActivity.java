package com.example.owner.todolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.owner.todolist.Model.ToDoList;
import com.example.owner.todolist.ui.ToDoListFragment;
import com.example.owner.todolist.ui.addNewList.AddNewListFragment;
import com.example.owner.todolist.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    DatabaseHelper myDb;
    Menu menu;
    boolean allowRefresh;
    String list_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = DatabaseHelper.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        menu = navigationView.getMenu();
        final SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.main_swipe_refresh);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_add_new_list)
                .setDrawerLayout(drawer)
                .build();

        final Menu submenu = menu.addSubMenu("To Do Lists");
        final Cursor list_result = myDb.getAllListData();

        if (list_result != null)
        {
            while (list_result.moveToNext()) {
                int count = 0;
                submenu.add(count, Menu.FIRST + count, Menu.FIRST, list_result.getString(1)).setIcon(R.drawable.ic_to_do_list);
                ++count;
            }
        }



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                String title = "";
                //creating fragment object
                Fragment fragment = null;

                //initializing the fragment object which is selected
                if (id == R.id.nav_home)
                    fragment = new HomeFragment();
                else if (id == R.id.nav_add_new_list)
                    fragment = new AddNewListFragment();
                else
                {
                    fragment = new ToDoListFragment();

                    title = (String)item.getTitle();

                    Bundle bundle = new Bundle();
                    bundle.putString("fragment_title", title);
                    fragment.setArguments(bundle);
                }



                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                //close navigation drawer
                drawer.closeDrawer(GravityCompat.START);
                return true;

            }
        });


        Intent i = getIntent();
        Bundle b = i.getBundleExtra("list");
        if (b != null)
            list_title = b.getString("fragment_title");
        ToDoListFragment toDoListFragment = new ToDoListFragment();

        if (getSupportFragmentManager().findFragmentById(R.id.content_frame) == null && b == null)
        {
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }
        else if (b != null)
        {
            Bundle bundle = new Bundle();
            bundle.putString("fragment_title", list_title);
            toDoListFragment.setArguments(bundle);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, toDoListFragment);
            transaction.commit();
        }

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        mySwipeRefreshLayout.setRefreshing(true);
                        drawer.invalidate();
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        mySwipeRefreshLayout.setRefreshing(false);

                    }
                }
        );


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        drawer.invalidate();
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        //call close() of the helper class
        if (myDb != null)
            myDb.close();
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();

        if (getFragmentManager().getBackStackEntryCount() == 0) {
           //
        } else
            {
                getFragmentManager().popBackStack();
            }
    }

}
