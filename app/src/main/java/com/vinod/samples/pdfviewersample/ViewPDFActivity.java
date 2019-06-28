package com.vinod.samples.pdfviewersample;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewPDFActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener, DownloadCallback<String> {
    
    private PDFView pdfView;
    public static final int PERMISSION_CODE = 42042;
    private final static int REQUEST_CODE = 42;
    private Uri uri;
    Integer pageNumber = 0;
    private NetworkFragment networkFragment;
    private boolean downloading = false;
    private static final String TAG = "ViewPDFActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        pdfView = findViewById(R.id.pdfView);
        Log.d(TAG, "onCreate: read file from assets");
        String source = getIntent().getStringExtra("source");
        networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "http://www.scbam.com/medias/fund-doc/fund-summary-aimc/SCBTMF_FUNDSUM_En.pdf");
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
        if (!downloading && networkFragment != null) {
            // Execute the async download.
            networkFragment.startDownload("https://docs.kony.com/8_x_PDFs/visualizer/vizrelnotes.pdf");
            downloading = true;
            Log.d(TAG, "loadFromNet: started download");

        }
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


    @Override
    public void updateFromDownload(String result) {
        Log.d(TAG, "updateFromDownload: ");

        //writeToFile("sample.pdf",result);
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"Sample.pdf");
            pdfView.fromFile(file)
                    .defaultPage(pageNumber)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .onPageError(this)
                    .load();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                Log.d(TAG, "onProgressUpdate: ERROR");
                break;
            case Progress.CONNECT_SUCCESS:
                Log.d(TAG, "onProgressUpdate: CONNECT_SUCCESS");
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                Log.d(TAG, "onProgressUpdate: GET_INPUT_STREAM_SUCCESS");
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                Log.d(TAG, "onProgressUpdate: PROCESS_INPUT_STREAM_IN_PROGRESS");
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                Log.d(TAG, "onProgressUpdate: PROCESS_INPUT_STREAM_SUCCESS");
                break;
        }

    }

    @Override
    public void finishDownloading() {

        downloading = false;
        if (networkFragment != null) {
            networkFragment.cancelDownload();
        }

    }

    private File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
            Log.d(TAG, "getTempFile: "+file.getAbsolutePath());
        } catch (IOException e) {
            Log.d(TAG, "getTempFile: IOException");
        }
        return file;
    }

    private void writeToFile(String fileName,String fileContents){
        //String filename = "myfile";
       // String fileContents = "Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            Log.d(TAG, "writeToFile: Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
