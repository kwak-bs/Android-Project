package com.example.startproject2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {
    Animation anim1;
    Animation anim2;
    ImageView imageView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
      imageView = findViewById(R.id.imageView2);
      textView = findViewById(R.id.textView5);

      //에니메이션 구성 후 첫 인트로 화면이 열리자 마자 에니메이션 실행
      anim1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_image);
      anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_text);
      imageView.startAnimation(anim1);
      textView.startAnimation(anim2);

      //인트로 화면 구성 후, 2초 후 MainActivity로 넘어가기
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
          startActivity(intent);

          finish();
        }
      },2000);
    }

    @Override
  protected void onPause() {
      super.onPause();
      finish();
    }
}
