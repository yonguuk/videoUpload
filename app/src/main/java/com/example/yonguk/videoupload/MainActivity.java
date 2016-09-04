package com.example.yonguk.videoupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnChoose;
    private Button btnUpload;
    private TextView tvFilePath;
    private TextView tvResponse;

    private static final int SELECT_VIDEO = 3;
    private String selectedPath;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(getApplicationContext(),"용육찡 안녕?? 모찌모찌기모찌*^^*",TOAST_SHORT(?)).show();

        btnChoose = (Button) findViewById(R.id.btn_choose);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        tvFilePath = (TextView) findViewById(R.id.tv_path);
        tvResponse = (TextView) findViewById(R.id.tv_url);

        btnChoose.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
    }

    private void chooseVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video"), SELECT_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_VIDEO){
                Uri selectedVideoUri = data.getData();
                selectedPath = getPath(selectedVideoUri);
                tvFilePath.setText(selectedPath);
            }
        }
    }

    public String getPath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String documentId = cursor.getString(0);
        documentId = documentId.substring(documentId.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media._ID + " = ? ",
                new String[]{documentId},
                null
        );
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();
        return path;
    }

    private void uploadVideo(){
        class UploadVideo extends AsyncTask<Void, Void, String>{
            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(MainActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                tvResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload uv = new Upload();
                String msg = uv.uploadVideo(selectedPath);
                return msg;
            }
        }
        UploadVideo umv = new UploadVideo();
        umv.execute();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choose:
                chooseVideo();
                break;

            case R.id.btn_upload:
                uploadVideo();
                break;
        }
    }
}
