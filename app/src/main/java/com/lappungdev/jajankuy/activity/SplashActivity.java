package com.lappungdev.jajankuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.lappungdev.jajankuy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.tvJudul)
    ImageView tvJudul;
    @BindView(R.id.transContainer)
    ViewGroup transContainer;
    private boolean visibleJudul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        transContainer = findViewById(R.id.transContainer);
        tvJudul = transContainer.findViewById(R.id.tvJudul);

        new Handler().postDelayed(() -> {
            TransitionManager.beginDelayedTransition(transContainer);
            visibleJudul = !visibleJudul;
            tvJudul.setVisibility(visibleJudul ? View.VISIBLE : View.GONE);
        }, 1000);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, 2000);

    }
}
