package com.vinod.samples.pdfviewersample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

public class ViewPDFActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {
    
    private PDFView pdfView;
    public static final int PERMISSION_CODE = 42042;
    private final static int REQUEST_CODE = 42;
    private Uri uri;
    Integer pageNumber = 0;
    private static final String TAG = "ViewPDFActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        pdfView = findViewById(R.id.pdfView);
        Log.d(TAG, "onCreate: read file from assets");
        String source = getIntent().getStringExtra("source");

        switch(source){
            case "fromAssets":
                   loadFromAssets();
                   break;
            case "fromInternet":
                   loadFromNet();
                   break;
            case "fromStorage":
                  loadFromDevice();
                  break;
            default:
                Log.d(TAG, "onCreate: Do nothing");
        }
    }

    private void loadFromAssets() {
        try {
            pdfView.fromAsset("FundFactSheet.pdf")
                    .defaultPage(0)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .onPageError(this)
                    .pageFitPolicy(FitPolicy.BOTH)
                    .load();
            Log.d(TAG, "onCreate: loading pdfView");
        } catch (Exception e) {
            Log.d(TAG, "onCreate: Exception "+e.getMessage());
        }
    }

    private void loadFromNet(){

    }

    private void loadFromDevice(){
        Log.d(TAG, "loadFromDevice: ");
        //TODO Check storage permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this,"android.permission.READ_EXTERNAL_STORAGE");

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
                    PERMISSION_CODE
            );

            return;
        }

        launchPicker();

    }

    private void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                uri = data.getData();
                displayFromUri(uri);
            }
        }
    }

    private void displayFromUri(Uri uri) {
       //pdfFileName = getFileName(uri);

        pdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                launchPicker();
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageError(int page, Throwable t) {

    }
}
