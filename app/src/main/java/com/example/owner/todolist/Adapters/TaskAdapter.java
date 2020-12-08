package com.example.owner.todolist.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.owner.todolist.DatabaseHelper;
import com.example.owner.todolist.MainActivity;
import com.example.owner.todolist.Model.Task;
import com.example.owner.todolist.R;
import com.example.owner.todolist.ui.ToDoListFragment;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    CheckBox checkBox_completed;
    boolean complete;
    boolean checked;
    private ArrayList<Task> taskList;
    DatabaseHelper myDb;
    String task_id;
    private CheckboxCheckedListener checkedListener;

    /*
     * Default constructor for the TaskAdapter
     * @param context
     * @param resource
     * @param objects
     */

    public TaskAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Task> objects) {
        super(context, R.layout.task_list_element, objects);
        this.taskList = objects;
        this.mContext = context;
        this.mResource = resource;
    }

    private static class ViewHolder {
        public TextView taskName;
        public TextView notes;
        public CheckBox completed;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get the task information
        final String taskName = getItem(position).getTaskName();
        final String list_id = getItem(position).getList_id();
        final String notes = getItem(position).getNotes();
        complete = getItem(position).isCompleted();
        final String dueDate = getItem(position).getDueDate();

        myDb = DatabaseHelper.getInstance(mContext);

        View result = convertView;

        //Create the task object with the information
        Task task;


        final ViewHolder holder;


        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(R.layout.task_list_element, null);
            holder = new ViewHolder();

            holder.taskName = (TextView) result.findViewById(R.id.txt_task_name);
            holder.notes = (TextView) result.findViewById(R.id.txt_task_notes);
            holder.completed = (CheckBox) result.findViewById(R.id.checkbox_completed);


            result.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) result.getTag();
        }


        Task t = taskList.get(position);
        holder.taskName.setText(t.getTaskName());
        holder.notes.setText(t.getNotes());
        holder.completed.setChecked(t.isCompleted());
        holder.completed.setTag(t);


        holder.completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkedListener != null)
                {
                    checkedListener.getCheckboxCheckedListener(position);
                }

                Cursor task_result = myDb.getAllTaskData(list_id);
                int get_task_id = 0;
                while (task_result.moveToNext())
                {
                    if (taskName.equals(task_result.getString(1)))
                    {
                        get_task_id = task_result.getInt(0);
                    }

                    task_id = Integer.toString(get_task_id);
                } //end while

                if (isChecked) {
                    holder.completed.setChecked(true);
                }
                else
                    {
                        holder.completed.setChecked(false);
                    }

                myDb.updateTaskCompleted(task_id, isChecked);

            }
        });

        myDb.close();

        return result;
    }

    public interface CheckboxCheckedListener
    {
        void getCheckboxCheckedListener(int position);
    }

    public void setCheckedListener(CheckboxCheckedListener checkedListener)
    {
        this.checkedListener = checkedListener;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

}
