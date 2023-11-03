package com.example.mangoleafdoctor1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mangoleafdoctor1.ml.Imagenet21kMangoDisease;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    Button camera, gallery;
    ImageView imageView;
    TextView result;

    int imageSize = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

                // Convert the Bitmap to a byte array
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();

                // After capturing or selecting the image:
                Uri imageUri = saveImageToFile(image);

                // Start the second activity and pass the image URI as an extra
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("imageUri", imageUri.toString());
                startActivity(intent);



            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Convert the Bitmap to a byte array
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();

                // After capturing or selecting the image:
                Uri imageUri = saveImageToFile(image);

                // Start the second activity and pass the image URI as an extra
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("imageUri", imageUri.toString());
                startActivity(intent);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Uri saveImageToFile(Bitmap image) {
        File imageFile = new File(getCacheDir(), "temp_image.png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(imageFile);
    }

}
