package com.ds.carvinocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // creating variables for our
    // image view, text view and two buttons.
    private ImageView img;
    private EditText textview;
    private Button snapBtn;

    // variable for our image bitmap.
    private Bitmap imageBitmap;

    private GraphicOverlay mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // on below line we are initializing our variables.
        img = (ImageView) findViewById(R.id.image);
        textview = (EditText) findViewById(R.id.text);
        snapBtn = (Button) findViewById(R.id.snapbtn);

        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.graphic_overlay);

        snapBtn.setOnClickListener(v -> {
            // calling a method to capture our image.
            dispatchTakePictureIntent();
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        // in the method we are displaying an intent to capture our image.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // on below line we are calling a start activity
        // for result method to get the image captured.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // calling on activity result method.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // on below line we are getting
            // data from our bundles. .
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            // below line is to set the
            // image bitmap to our image.
            img.setImageBitmap(imageBitmap);

            runTextRecognition();
        }
    }

    private void runTextRecognition() {
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        //mTextButton.setEnabled(false);
        recognizer.process(image)
                .addOnSuccessListener(
                        texts -> {
                            //mTextButton.setEnabled(true);
                            processTextRecognitionResult(texts);
                        })
                .addOnFailureListener(
                        e -> {
                            // Task failed with an exception
                            //mTextButton.setEnabled(true);
                            e.printStackTrace();
                        });
    }

    private void processTextRecognitionResult(Text texts) {
        textview.setText("");

        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            //showToast("No text found");
            return;
        }
        mGraphicOverlay.clear();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    //GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    //mGraphicOverlay.add(textGraphic);
                    stringBuilder.append(elements.get(k).getText());
                }
            }
        }
        textview.setText(stringBuilder);

    }
}
