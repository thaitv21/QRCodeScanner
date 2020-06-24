package com.nullexcom.qrscannerdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.nullexcom.qrscanner.QRScannerView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private QRScannerView qrScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qrScannerView = findViewById(R.id.qrScannerView);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        qrScannerView.startCamera(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrScannerView.startCamera(this);
        qrScannerView.setCallback(1000, result -> {
            Log.d("QRCodeAnalyzer", "onResume: " + result.getText());
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrScannerView.stopCamera();
    }
}
