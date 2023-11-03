package com.example.mangoleafdoctor1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mangoleafdoctor1.ml.Imagenet21kMangoDisease;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity2 extends AppCompatActivity {

    Button detect, change;
    ImageView imageView;
    TextView result;
    int imageSize = 300;
    Bitmap image; // Declare the Bitmap image as a class-level variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        detect = findViewById(R.id.button);
        change = findViewById(R.id.button2);

        imageView = findViewById(R.id.imageView);

        //When bitmap image is passed:
        //byte[] byteArray = getIntent().getByteArrayExtra("image");
        //Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //imageView.setImageBitmap(image);

        // When the image URI from the intent is passed
        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            // Load and display the image from the URI
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(image);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classifyImage(image);
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void classifyImage(Bitmap image){
        try {
            Imagenet21kMangoDisease model = Imagenet21kMangoDisease.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 300, 300, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Imagenet21kMangoDisease.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Anthracnose", "Bacterial Canker", "Cutting Weevil", "Die Back", "Gall Midge", "Healthy", "Powdery Mildew", "Sooty Mould"};
            String classResult = classes[maxPos];

            // Convert the Bitmap to a byte array
            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            //byte[] byteArray = stream.toByteArray();

            // After capturing or selecting the image:
            Uri imageUri = saveImageToFile(image);

            Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
            intent.putExtra("textData", classResult);
            intent.putExtra("imageUri", imageUri.toString());
            //intent.putExtra("image", byteArray);
            startActivity(intent);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
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
