package com.example.abs.clixcel;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    Uri picUri;
    private static final int CROP_IMAGE = 2;
    private Uri mCropImagedUri;
    ImageView cam,fol;
    int MY_PERMISSIONS_REQUEST_CAMERA;
    private static final int CAMERA_REQUEST = 1888;
    File f;
    String datap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cam=(ImageView)findViewById(R.id.camic);
        fol=(ImageView)findViewById(R.id.folderic);
        permission();
        crfolder();
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        fol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performCropImage();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(requestCode == CAMERA_REQUEST && resultCode == MainActivity.RESULT_OK) {
             picUri = data.getData();
             performCropImage();
        }
        if(requestCode==CROP_IMAGE)
        {
            Intent intent = new Intent(getBaseContext(),OCR.class);
            intent.putExtra("pic", datap);
            startActivity(intent);
        }
    }
    void crfolder()
    {
        File cacheDir = new File(Environment.getExternalStorageDirectory(), "Clixcel");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
    }
    void permission()
    {
        int camper = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA);
        if(camper!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
        int readper = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(readper!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
        int wrper = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(wrper!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }
    /**Crop the image
     * @return returns <tt>true</tt> if crop supports by the device,otherwise false*/
    private boolean performCropImage(){
        try {
            //if(mFinalImageUri!=null)
            {
                //call the standard crop action intent (the user device may not support it)
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                //indicate image type and Uri
                cropIntent.setType("image/*");
                //set crop properties
                cropIntent.putExtra("crop", "true");
                //indicate aspect of desired crop
                //cropIntent.putExtra("aspectX", 1);
                //cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("scale", true);
                //indicate output X and Y
                //cropIntent.putExtra("outputX", 500);
                //cropIntent.putExtra("outputY", 500);
                //retrieve data on return
                cropIntent.putExtra("return-data", false);
                f = createNewFile("CROP_");
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    //VLLog.e("io", ex.getMessage());
                }

                mCropImagedUri = Uri.fromFile(f);
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImagedUri);
                //start the activity - we handle returning in onActivityResult
                startActivityForResult(cropIntent, CROP_IMAGE);
                return true;
            }
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        //return false;
    }

    private File createNewFile(String prefix){
        if(prefix==null || "".equalsIgnoreCase(prefix)){
            prefix="IMG_";
        }
        File newDirectory = new File(Environment.getExternalStorageDirectory()+"/Clixcel/");
        datap=prefix+System.currentTimeMillis()+".jpg";
        if(!newDirectory.exists()){
            if(newDirectory.mkdir()){
                //VLLog.d(mContext.getClass().getName(), newDirectory.getAbsolutePath()+" directory created");
            }
        }
        File file = new File(newDirectory,(datap));
        if(file.exists()){
            //this wont be executed
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
