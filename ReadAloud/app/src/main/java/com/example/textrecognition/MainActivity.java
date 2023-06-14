package com.example.textrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button capture,copy,speak;
    TextView textView;
    TextToSpeech t1;
    LottieAnimationView lottieAnimationView;


    private static final int REQUEST_CAMERA_CODE = 100;
    Bitmap bitmap;
    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView2);
        capture = findViewById(R.id.Capture);
        copy = findViewById(R.id.CopyText);
        speak = findViewById(R.id.speak);

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }


        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(MainActivity.this);

            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String scanned_text = textView.getText().toString();
                copyToClipBoard(scanned_text);
            }
        });
//        YoYo.with(Techniques.SlideInLeft).duration(1000).repeat(1).playOn(textView3);
        LinearLayout layout = findViewById(R.id.linearLayout);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(
                layout,
                PropertyValuesHolder.ofFloat("translationY", 0, -100, 0, -50, 0)
        );
        animator.setDuration(1000);
        animator.start();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK){
                    Uri resultUri = result.getUri();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
                        getTextFromImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if ( !recognizer.isOperational() ){
            Toast.makeText(MainActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
        }
        else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray =   recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0; i<textBlockSparseArray.size(); i++){
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }

            String newstr = stringBuilder.toString();
            textView.setText(newstr);
            capture.setText("Retake");
            copy.setVisibility(View.VISIBLE);
            speak.setVisibility(View.VISIBLE);
//            lottieAnimationView.setVisibility(View.GONE);
//            int duration = 1000;
            speak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String toSpeak = newstr;
//                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    t1.setSpeechRate(0.7f);
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    YoYo.with(Techniques.Bounce).duration(1000).repeat(1).playOn(textView);
                    if (t1.isSpeaking()){
                        Toast.makeText(MainActivity.this, "Speaking..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }

            }
        });

//        YoYo.with(Techniques.SlideInRight).duration(1000).repeat(1).playOn(speak);
//        YoYo.with(Techniques.TakingOff).duration(1000).repeat(1).playOn(capture);

    }


    private void copyToClipBoard(String text){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied data",text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }


}