package com.example.expcheck_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertItemInfo extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    //for date
    int y=0, m=0, d=0, h=0, mi=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_item_info);

        //1. 캘린더로 추가
        ImageButton button = findViewById(R.id.calendar_insert_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate();
            }
        });
        //2. 문자인식으로 추가
        ImageButton button2 = findViewById(R.id.camera_insert_button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-> 카메라로 촬영 후 리스트에 추가(tesseract-ocr 액티비티로 이동)
                Intent intent = new Intent(getApplicationContext(), Tesseract_ocr.class);
                startActivity(intent);
            }
        });

        //확인버튼 누르면 수정되도록
        Button add_button = (Button)findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //현재 text내용으로 저장
                Intent intent = new Intent(InsertItemInfo.this, MainActivity.class);
                EditText title_temp = (EditText)findViewById(R.id.title_edit_i);
                String name = title_temp.getText().toString();
                EditText exp_temp = (EditText)findViewById(R.id.exp_edit_i);
                String date = exp_temp.getText().toString();

                DataModel new_item = new DataModel(name, date);

                //databaseReference.child("message").push().setValue(title_temp.getText().toString());
                databaseReference.child("items").push().setValue(new_item);

                //intent.putExtra("new_title", title_temp.getText().toString());
                //intent.putExtra("new_exp", exp_temp.getText().toString());

                startActivity(intent);
            }
        });

    }

    //달력
    void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y = year;
                m = month+1;
                d = dayOfMonth;

                EditText exp_temp = (EditText)findViewById(R.id.exp_edit_i);

                String string_y = Integer.toString(y);
                String string_m = Integer.toString(m);
                String string_d = Integer.toString(d);
                String s_exp = string_y + "-" + string_m + "-" + string_d;

                //캘린더 버튼에서 저장한 것으로 바꿈
                exp_temp.setText(s_exp);
            }
        },2019, 11, 20);

        datePickerDialog.setMessage("choice EXP");
        datePickerDialog.show();
    }
}