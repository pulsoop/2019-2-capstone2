package com.example.expcheck_camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    /*for camera-------------
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //camera intent 실행
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.expcheck.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    */
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

    private ListView listView;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // Adapter 생성
        final ListViewAdapter adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter 지정
        final ListView listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);
        */

        listView = (ListView) findViewById(R.id.listview1);

        initDatabase();

        adapter = new ListViewAdapter();
        listView.setAdapter(adapter);

        //header
        final View header = getLayoutInflater().inflate(R.layout.listview_header, null, false) ;
        listView.addHeaderView(header) ;

        //--아이템추가--
        //adapter.addItem("Milk", "2020.11.12") ;
        //1. 직접 추가
        Button addButton = (Button)findViewById(R.id.add);
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //클릭했을 때 다음 화면은 정보 입력 창(ItemInfo)
                Intent intent = new Intent(getApplicationContext(), InsertItemInfo.class);
                startActivity(intent);

                //새롭게 입력한 데이터 받아오기
                //Intent get_intent = getIntent();
                //String new_title = get_intent.getExtras().getString("new_title");
                //String new_exp = get_intent.getExtras().getString("new_exp");

                //mReference.child("message").push().setValue(new_title);
            }
        }) ;


        //2. 촬영으로 추가
        Button addButton_camera = (Button)findViewById(R.id.add_camera);
        addButton_camera.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //-> 카메라로 촬영 후 리스트에 추가(tesseract-ocr 액티비티로 이동)
                Intent intent = new Intent(getApplicationContext(), Tesseract_ocr.class);
                startActivity(intent);
            }
        }) ;

        //3. 아이템 수정 - item클릭했을 때
        //위에서 생성한 listview에 클릭 이벤트 핸들러 정의
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                //클릭했을 때 다음 화면은 정보 입력 창(ItemInfo)
                Intent intent = new Intent(getApplicationContext(), ItemInfo.class);

                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle() ;
                String descStr = item.getDesc() ;

                //intent에 넣어서 전송
                intent.putExtra("title", titleStr);
                intent.putExtra("exp", descStr);

                startActivity(intent);
                // listview 갱신
                // adapter.notifyDataSetChanged();
            }
        }) ;

        mReference = mDatabase.getReference("items");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                adapter.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()){
                    //child 내에 있는 데이터만큼 반복
                    DataModel msg2 = messageData.getValue(DataModel.class);
                    adapter.addItem(msg2.title, msg2.date);
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount()-1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mChild);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }


    //-> 이미지 콜백 안됨
    private void callCamera() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            PackageManager pm = getPackageManager();

            final ResolveInfo mInfo = pm.resolveActivity(i, 0);

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            startActivity(intent);
        } catch (Exception e){ Log.i("TAG", "Unable to launch camera: " + e); }
    }

}

/*
//2. 촬영으로 추가
        Button addButton_camera = (Button) findViewById(R.id.add_camera);
        addButton_camera.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //-> 카메라로 촬영 후 리스트에 추가
                Intent intent = new Intent(getApplicationContext(), Tesseract_ocr.class);
                startActivity(intent);
            }
        });
* */
