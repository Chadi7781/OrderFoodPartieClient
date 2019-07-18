package com.example.chadi.orderfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

public class SplashScreen extends AppCompatActivity {
    private TextView textView;
    private ImageView imageView;
    private SpinKitView progressBar;
    private Sprite cubeGrid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);   init();
        Animation yanim= AnimationUtils.loadAnimation(this,R.anim.mytransition);
        imageView.startAnimation(yanim);

        cubeGrid=new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid);

        Thread timmer=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);

                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
                finally {
//                    if(new TinyDB(SplashScreen.this).getString("connected").contains("connected")){
//                        final Intent intent=new Intent(SplashScreen.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }else{
//                        final Intent intent=new Intent(SplashScreen.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
                    final Intent intent = new Intent(SplashScreen.this, SignIn.class);
                    startActivity(intent);
                    finish();

                    // progressBar.setVisibility(View.GONE);

                }
            }
        };
        timmer.start();
    }
    public void init(){
        imageView=findViewById(R.id.iv);
        progressBar = findViewById(R.id.progress);


    }
}

