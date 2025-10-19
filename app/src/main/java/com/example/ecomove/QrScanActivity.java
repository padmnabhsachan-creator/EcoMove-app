package com.example.ecomove;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecomove.api.RetrofitClient;
import com.example.ecomove.travel.qr.QRDecoder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private PreviewView previewView;
    private boolean scanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr_scan);

        previewView = findViewById(R.id.previewView);

        ViewCompat.setOnApplyWindowInsetsListener(previewView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build();

                BarcodeScanner scanner = BarcodeScanning.getClient(options);

                analysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                    processImageProxy(scanner, imageProxy);
                });

                CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.bindToLifecycle(this, selector, preview, analysis);

            } catch (Exception e) {
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void processImageProxy(BarcodeScanner scanner, ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    handleBarcodes(barcodes);
                    imageProxy.close();
                })
                .addOnFailureListener(e -> imageProxy.close());
    }

    private void handleBarcodes(List<Barcode> barcodes) {
        if (scanned) return;

        for (Barcode barcode : barcodes) {
            if (barcode.getFormat() == Barcode.FORMAT_QR_CODE) {
                String rawValue = barcode.getRawValue();
                if (rawValue != null) {
                    scanned = true;
                    submitRawQr(rawValue);
                    break;
                }
            }
        }
    }

    private void submitRawQr(String qrContent) {
        Call<com.example.ecomove.travel.model.TravelVerification> call =
                RetrofitClient.getApiService().verifyViaQr(qrContent);

        call.enqueue(new Callback<com.example.ecomove.travel.model.TravelVerification>() {
            @Override
            public void onResponse(Call<com.example.ecomove.travel.model.TravelVerification> call,
                                   Response<com.example.ecomove.travel.model.TravelVerification> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(QrScanActivity.this, "Travel verified and credits awarded!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(QrScanActivity.this, "Verification failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.ecomove.travel.model.TravelVerification> call, Throwable t) {
                Toast.makeText(QrScanActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Simulating QR scan in emulator", Toast.LENGTH_SHORT).show();

        if (isEmulator() && !scanned) {
            scanned = true;
            String testQr = "1e644ea5-9bb7-4482-8644-f226b387caa1|Chengalpattu|Guindy|22.5|2025-10-19 08:30:00|bus";
            submitRawQr(testQr);
        }
    }

    private boolean isEmulator() {
        return android.os.Build.FINGERPRINT.contains("generic")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.HARDWARE.contains("goldfish")
                || android.os.Build.HARDWARE.contains("ranchu")
                || android.os.Build.PRODUCT.contains("sdk");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
