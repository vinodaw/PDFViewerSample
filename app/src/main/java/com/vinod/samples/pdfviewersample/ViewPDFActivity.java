package com.vinod.samples.pdfviewersample;

import android.os.Bundle;
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

    private void loadFromDevice(){}

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
