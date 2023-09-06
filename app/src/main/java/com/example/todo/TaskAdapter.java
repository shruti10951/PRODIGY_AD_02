package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{

    Context context;
    ArrayList arrayList;
    SQLiteDatabase database;
    ToDoListHelper toDoListHelper;

    public TaskAdapter(Context context, ArrayList arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.task_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String task= (String) arrayList.get(position);
        holder.textView.setText(task);

        holder.done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDoListHelper= new ToDoListHelper(context);
                database = toDoListHelper.getWritableDatabase();
                database.delete("todo", "task=?", new String[]{task});
                arrayList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                Toast.makeText(context, "Task Completed!", Toast.LENGTH_SHORT).show();
                database.close();
            }
        });

        holder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AlertDialog alertDialog= new AlertDialog.Builder(context)
//                        .setView(holder.view1)
//                        .create();
                holder.alertDialog.show();
                String task= holder.textView.getText().toString();
                holder.editText.setText(task);
                holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.alertDialog.dismiss();
                    }
                });
                holder.okayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String input= holder.editText.getText().toString();
                        ToDoListHelper toDoListHelper1= new ToDoListHelper(context);
                        SQLiteDatabase sqLiteDatabase= toDoListHelper1.getWritableDatabase();
                        ContentValues contentValues= new ContentValues();
                        contentValues.put("task", input);
                        sqLiteDatabase.update("todo", contentValues, "task=?", new String[]{task});
                        Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();
                        sqLiteDatabase.close();
                        holder.textView.setText(input);
                        holder.alertDialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageButton done_btn, edit_btn;

        View view1;
        EditText editText;
        Button cancelBtn;
        Button okayBtn;

        AlertDialog alertDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView= itemView.findViewById(R.id.task);
            done_btn = itemView.findViewById(R.id.completed_btn);
            edit_btn= itemView.findViewById(R.id.edit_btn);

            view1= View.inflate(context, R.layout.input_dialogue, null);
            editText= view1.findViewById(R.id.input_task);
            cancelBtn= view1.findViewById(R.id.cancel_btn);
            okayBtn= view1.findViewById(R.id.ok_btn);

            alertDialog= new AlertDialog.Builder(context)
                    .setView(view1)
                    .create();
        }
    }

}
