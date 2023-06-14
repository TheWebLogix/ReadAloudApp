package com.example.textrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;

public class welcome_screen extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        lottieAnimationView = findViewById(R.id.lottie);
//        lottieAnimationView.setSpeed(0.5f); // slows down the animation to half speed
        lottieAnimationView.animate().translationY(-1600).setDuration(1000).setStartDelay(5000);
     lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
         @Override
         public void onAnimationStart(@NonNull Animator animator) {

         }

         @Override
         public void onAnimationEnd(@NonNull Animator animator) {
            Intent intent = new Intent(welcome_screen.this,MainActivity.class);
            startActivity(intent);
         }

         @Override
         public void onAnimationCancel(@NonNull Animator animator) {

         }

         @Override
         public void onAnimationRepeat(@NonNull Animator animator) {

         }
     });

    }
}