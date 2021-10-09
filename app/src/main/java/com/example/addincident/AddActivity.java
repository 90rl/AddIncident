package com.example.addincident;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private ImageView mImageView ;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    double posX, posY;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static Spinner spinner;
    private String textSpinner;
    private String textMessageToSend;
    private EditText textAlarm;
    private Uri mImageUrl;
    private Button mButtonChooseImage, mButtonUpload;
    private ProgressBar mProgressBar;
    private StorageReference mStoageRef;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        posX = getIntent().getExtras().getDouble("posX");
        posY = getIntent().getExtras().getDouble("posY");

//        Toast.makeText(this, "FLAGA 1", Toast.LENGTH_SHORT).show();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = findViewById(R.id.spinnerServices);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Services,android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

        mStoageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
                //Toast.makeText(getApplicationContext(),"posX: " + posX + "  " + "posY: " + posY + " textSpinner: "+ textSpinner + "  Treść ALARMU: "+ textMessageToSend + " URL: "+ mImageUrl,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension (Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        textSpinner = parent.getItemAtPosition(position).toString();
    }


    private void uploadFile(){
        if(mImageUrl != null){
            StorageReference fileReference = mStoageRef.child(System.currentTimeMillis()
            + "."+ getFileExtension(mImageUrl));

            textAlarm = findViewById(R.id.editText);
            textMessageToSend = textAlarm.getText().toString();

            SimpleDateFormat DateStr = new SimpleDateFormat("yyyyMMdd_HHmmSS");
            final String currentDateandTime = DateStr.format(new Date());

            fileReference.putFile(mImageUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 2500);

                            Toast.makeText(AddActivity.this, "Upload sucessful !!!", Toast.LENGTH_LONG).show();

                           //Toast.makeText(AddActivity.this, "Flaga 1 (line 135): textMessageToSend : " + currentDateandTime, Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(textSpinner,
                                    taskSnapshot.getStorage().getDownloadUrl().toString(), posX, posY, textMessageToSend, currentDateandTime );
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);

                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== RESULT_OK
                && data != null && data.getData() != null){
            mImageUrl = data.getData();
            Picasso.with(this).load(mImageUrl).into(mImageView);

        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void sendAlarm(View view) {

        Toast.makeText(getApplicationContext(),"posX: " + posX + "  " + "posY: " + posY + " textSpinner: "+ textSpinner + "  Treść ALARMU: "+ textMessageToSend + " URL: "+ mImageUrl,Toast.LENGTH_SHORT).show();

    }


}

