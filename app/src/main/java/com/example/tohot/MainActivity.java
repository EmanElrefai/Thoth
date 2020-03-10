package com.example.tohot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
     public VisionServiceClient visionServiceClient =new VisionServiceClient("ac585835001b490a941d07984f938e77");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap mBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.rrr);
        ImageView imageView=(ImageView)findViewById(R.id.imageView);
        Button btnProcess=(Button)findViewById(R.id.btnProcess);

        imageView.setImageBitmap(mBitmap);

        //convert image to stream

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        ByteArrayInputStream inputStream =new ByteArrayInputStream(outputStream.toByteArray());

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             final AsyncTask<InputStream,String,String>visionTask =new AsyncTask<InputStream, String, String>() {
                 ProgressDialog mDialog =new ProgressDialog(MainActivity.this);
                 @Override
                 protected String doInBackground(InputStream... params) {
                     try {
                         publishProgress("Recognizing....");
                         String[] features = {"Description"};
                         String[] details = {};

                         AnalysisResult result = visionServiceClient.analyzeImage(params[0], features, details);
                         String strResult = new Gson().toJson(result);
                         return strResult;
                     } catch (Exception e) {
                         return null;
                     }

                 }
                 @Override
                 protected void onPreExecute(){
                     mDialog.show();

                 }
                 @Override
                 protected  void onPostExecute(String s){
                     mDialog.dismiss();
                     AnalysisResult result=new Gson().fromJson(s,AnalysisResult.class);
                     StringBuilder stringBuilder=new StringBuilder();
                     TextView textView=(TextView)findViewById(R.id.txtDescription);
                     for(Caption caption:result.description.captions){
                         stringBuilder.append(caption.text);
                     }textView.setText(stringBuilder);

                 }
                 @Override
                 protected void onProgressUpdate(String... values){
                     mDialog.setMessage(values[0]);
                 }


             };

            }
        });
    }
}
