package com.example.mangoleafdoctor1;

import android.content.Intent;
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity3 extends AppCompatActivity {

    Button reset, change;
    ImageView imageView;
    TextView result, result2;
    Bitmap image; // Declare the Bitmap image as a class-level variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        reset = findViewById(R.id.button);


        result = findViewById(R.id.result);
        result2 = findViewById(R.id.result2);
        imageView = findViewById(R.id.imageView);


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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String classResult = getIntent().getStringExtra("textData");
        result.setText(classResult);

        if (classResult.equals("Anthracnose")) {
            result2.setText("When Anthracnose is present, dark necrotic patches emerge on both sides of the\n" +
                "mango leaf. Typically, these necrotic patches form along the leaf edges, where the lesions\n" +
                "combine. Leaves that have suffered severe damage start to curl.");
        } else if (classResult.equals("Bacterial Canker")) {
            result2.setText("When infected by Bacterial Canker, the bacterium Pseudomonas mangifera induces\n" +
                "water-soaked spots on mango fruits, leaves, stalks, and branches that eventually develop\n" +
                "into characteristic cankers.");
        } else if (classResult.equals("Cutting Weevil")) {
            result2.setText("Cutting Weevil disease cuts the mango leaf in such a way that it looks like it is cut\n" +
                "with scissors.");
        } else if (classResult.equals("Die Back")) {
            result2.setText("The primary symptom is the slow top-to-bottom drying and wilting of twigs, which is more common in older trees.");
        } else if (classResult.equals("Gall Midge")) {
            result2.setText("Gall Midge disease causes leaves have what look like pimples on\n" +
                "them. Heavy outbreaks of mango Gall Midge disease result in defoliation and reduced fruit\n" +
                "yield.");
        } else if (classResult.equals("Healthy")) {
            result2.setText("Fresh and young mango leaves are pliable, soft, and succulent in their raw state, displaying a combination of purple-red and green colors. As mango leaves mature, they turn deep green and acquire a more resilient and slightly chewy texture.");
        } else if (classResult.equals("Powdery Mildew")) {
            result2.setText("White powdery fungal growth on leaves, panicle stalks, flowers, and early fruits is\n" +
                "the disease’s hallmark. It adversely affects flowers and fruits, resulting in early wilting and\n" +
                "decreased agricultural production");
        } else if (classResult.equals("Sooty Mould")) {
            result2.setText(". Sooty mold, a black velvety layer that covers the\n" +
                "surface of leaves in the field, serves as a telltale sign of the disease. In more extreme cases,\n" +
                "the mold can spread throughout the tree’s twigs and leaves, turning the trees completely\n" +
                "black.");
        }

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
