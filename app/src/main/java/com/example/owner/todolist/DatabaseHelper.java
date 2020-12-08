package com.example.owner.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.example.owner.todolist.Model.ToDoList;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;

    private static String DATABASE_NAME = "ToDoList.db";
    private static String TABLE_1 = "Lists";
    private static String TABLE_2 = "Tasks";

    //To Do List Table
    private static String COL_1_TABLE_1 = "Name";

    //Task Table
    private static String COL_1_TABLE_2 = "TASK_NAME";
    private static String COL_2_TABLE_2 = "LIST_ID";
    private static String COL_3_TABLE_2 = "Notes";
    private static String COL_4_TABLE_2 = "Completed";
    private static String COL_5_TABLE_2 = "Due_Date";


    public static DatabaseHelper getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + TABLE_1 +"(LIST_ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT NOT NULL, DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("PRAGMA foreign_keys=ON");
        db.execSQL("create table " + TABLE_2 +"(TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK_NAME TEXT NOT NULL, "+
                "LIST_ID INTEGER, NOTES TEXT, COMPLETED BOOLEAN, DATE_CREATED DATETIME DEFAULT CURRENT_TIMESTAMP, DUE_DATE DATETIME, " +
                "FOREIGN KEY(LIST_ID) REFERENCES LISTS(LIST_ID))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_1);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_2);
        onCreate(db);
    }

    public boolean insertListData(String listName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues listValues = new ContentValues();

        listValues.put(COL_1_TABLE_1,listName);
        long result = db.insert(TABLE_1, null, listValues);

        if (result == -1)
            return false;
        else
            return true;
    }


    public boolean insertTaskData(String taskName, String id, String notes, boolean completed, String dueDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues taskValues = new ContentValues();

        taskValues.put(COL_1_TABLE_2,taskName);
        taskValues.put(COL_2_TABLE_2, id);
        taskValues.put(COL_3_TABLE_2, notes);
        taskValues.put(COL_4_TABLE_2, completed);
        taskValues.put(COL_5_TABLE_2, dueDate);
        long result = db.insert(TABLE_2, null, taskValues);

        if (result == -1)
            return false;
        else
            return true;
    }



    public boolean insertQuickTaskData(String taskName, String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues taskValues = new ContentValues();

        taskValues.put(COL_1_TABLE_2,taskName);
        taskValues.put(COL_2_TABLE_2, id);

        long result = db.insert(TABLE_2, null, taskValues);

        if (result == -1)
            return false;
        else
            return true;
    }


    public Cursor getAllTaskData(String list_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.rawQuery("select * from " + TABLE_2 + " where Tasks.LIST_ID=" + list_id, null);

        return result;
    }

    public Cursor sortTaskByCreatedASC(String list_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.rawQuery("select * from " + TABLE_2 + " where Tasks.LIST_ID=" + list_id + " order by Tasks.DATE_CREATED ASC", null);

        return result;
    }

    public Cursor sortTaskByCreatedDESC(String list_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.rawQuery("select * from " + TABLE_2 + " where Tasks.LIST_ID=" + list_id + " order by Tasks.DATE_CREATED DESC", null);

        return result;
    }

    public Cursor sortTaskByDueDateASC(String list_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.rawQuery("select * from " + TABLE_2 + " where Tasks.LIST_ID=" + list_id + " order by Tasks.DUE_DATE ASC", null);

        return result;
    }

    public Cursor sortTaskByDueDateDESC(String list_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor result = db.rawQuery("select * from " + TABLE_2 + " where Tasks.LIST_ID=" + list_id + " order by Tasks.DUE_DATE DESC", null);

        return result;
    }

    public Cursor getAllListData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_1, null);

        return result;
    }

    public Cursor sortListByNameASC()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_1 + " order by Lists.NAME ASC", null);

        return result;
    }

    public Cursor sortListByNameDESC()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_1 + " order by Lists.NAME DESC", null);

        return result;
    }

    public Cursor sortListByCreatedASC()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_1 + " order by Lists.DATE_CREATED ASC", null);

        return result;
    }

    public Cursor sortListByCreatedDESC()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_1 + " order by Lists.DATE_CREATED DESC", null);

        return result;
    }

    public ArrayList<ToDoList> getAllListDataArray()
    {
        ArrayList<ToDoList> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_1, null);

        while(cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            ToDoList toDoList = new ToDoList(id, name);

            arrayList.add(toDoList);
        }

        return arrayList;
    }

    public boolean updateListData(String listID, String listName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues listValues = new ContentValues();

        listValues.put("LIST_ID", listID);
        listValues.put(COL_1_TABLE_1, listName);
        db.update(TABLE_1, listValues, "LIST_ID = ?", new String[] {listID});
        return true;
    }

    public boolean updateTaskData(String taskID, String taskName, String listID, String notes, boolean completed, String dueDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues taskValues = new ContentValues();

        taskValues.put("TASK_ID", taskID);
        taskValues.put(COL_1_TABLE_2,taskName);
        taskValues.put("LIST_ID", listID);
        taskValues.put(COL_3_TABLE_2, notes);
        taskValues.put(COL_4_TABLE_2, completed);
        taskValues.put(COL_5_TABLE_2, dueDate);
        db.update(TABLE_2, taskValues, "TASK_ID = ?", new String[] {taskID});
        return true;
    }

    public boolean updateTaskCompleted(String taskID, boolean completed)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues taskValues = new ContentValues();

        taskValues.put(COL_4_TABLE_2, completed);
        db.update(TABLE_2, taskValues, "TASK_ID = ?", new String[] {taskID});
        return true;
    }

    public Integer deleteTask(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_2, "TASK_ID = ?", new String[] {id});
    }

    public Integer deleteToDoList(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_1, "LIST_ID = ?", new String[] {id});
    }
}
