package com.example.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    ImageButton add_btn;
    RecyclerView recyclerView;
    ToDoListHelper toDoListHelper= new ToDoListHelper(this);
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    TaskAdapter taskAdapter;
    ArrayList list= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        add_btn= findViewById(R.id.add_btn);
        recyclerView= findViewById(R.id.task_list);

        taskAdapter= new TaskAdapter(DashboardActivity.this, list);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(DashboardActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(taskAdapter);

        updateList();

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1= getLayoutInflater().inflate(R.layout.input_dialogue, null);
                EditText editText= view1.findViewById(R.id.input_task);
                Button cancelBtn= view1.findViewById(R.id.cancel_btn);
                Button okayBtn= view1.findViewById(R.id.ok_btn);
                AlertDialog alertDialog= new AlertDialog.Builder(DashboardActivity.this)
                        .setView(view1)
                        .create();
                alertDialog.show();
                okayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String input = editText.getText().toString();
                        if(input.equals("")){
                            alertDialog.dismiss();
                        }else{
                            try {
                                sqLiteDatabase= toDoListHelper.getReadableDatabase();
                                ContentValues contentValues= new ContentValues();
                                contentValues.put("task", input);
                                sqLiteDatabase.insert("todo", null, contentValues);
                                sqLiteDatabase.close();
                                updateList();
                                Toast.makeText(DashboardActivity.this, "Task Added!", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }catch (Exception e){
                                Toast.makeText(DashboardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

    }

    public void updateList() {
        list.clear();
        sqLiteDatabase= toDoListHelper.getReadableDatabase();
        cursor= sqLiteDatabase.query("todo", null, null, null, null, null, null);
        while (cursor.moveToNext()){
            int index= cursor.getColumnIndex("task");
            list.add(cursor.getString(index));
        }
        cursor.close();
        sqLiteDatabase.close();

        taskAdapter.notifyDataSetChanged();
    }
}