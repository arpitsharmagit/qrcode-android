package com.ninjas.qrcodegenerator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.design.widget.Snackbar;

import com.google.zxing.WriterException;

public class QrGeneratorActivity extends AppCompatActivity {
    final String folderPath =  "barcodes";
    ImageView imageView;
    Button btnGenerate;
    EditText edtProductId;
    private static final  int HANDLE_PERM = 1;
    private boolean writePermission=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);

        imageView = findViewById(R.id.qrCode);
        edtProductId = findViewById(R.id.edtProductId);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                imageView.setImageBitmap(null);
                try {
                    String productId = edtProductId.getText().toString();
                    Bitmap qrBitmap = Utilities.TextToImageEncode(productId,500);
                    imageView.setImageBitmap(qrBitmap);
                    Utilities.saveImage(qrBitmap,folderPath,productId);

                    Snackbar.make(v,"QR code saved in "+folderPath,Snackbar.LENGTH_LONG)
                            .show();
                } catch (WriterException e) {
                    Snackbar.make(v,"Unable to process entered data.",Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        checkPermission();
    }
    @Override
    public void onBackPressed()
    {
        finish();
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != HANDLE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if(requestCode == HANDLE_PERM && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            writePermission =true;
            return;
        }

        if(writePermission !=true) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("QR Code Generator")
                    .setMessage("This application cannot run because it does not have the storage permission.\n" +
                            "The application will now exit.")
                    .setPositiveButton("OK", listener)
                    .show();
        }
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        else{
            writePermission=true;
        }
    }

    private void requestPermissions() {
        final String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, HANDLE_PERM);
        }
    }
}
