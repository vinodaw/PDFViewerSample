package com.vinod.samples.pdfviewersample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.barteksc.pdfviewer.PDFView;

public class MainActivity extends AppCompatActivity {

    private PDFView pdfView;
    private Button btnAsset;
    private Button btnDevice;
    private Button btnUrl;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAsset = findViewById(R.id.btnAssets);
        btnDevice = findViewById(R.id.btnDevice);
        btnUrl = findViewById(R.id.btnInternet);
        
        btnAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: From Assets");
                Intent intent = new Intent(MainActivity.this,ViewPDFActivity.class);
                intent.putExtra("source","fromAssets");
                startActivity(intent);
            }
        });
        
        btnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: From Device storage");
                Intent intent = new Intent(MainActivity.this,ViewPDFActivity.class);
                intent.putExtra("source","fromStorage");
                startActivity(intent);
            }
        });

        btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: From URL");
                Intent intent = new Intent(MainActivity.this,ViewPDFActivity.class);
                intent.putExtra("source","fromInternet");
                startActivity(intent);
            }
        });
        
      
    }
}
