package com.alemam.virtuallibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class NoInternet extends AppCompatActivity {

    LottieAnimationView animationView;
    SwipeRefreshLayout Refresh;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        animationView = findViewById(R.id.animationView);
        Refresh = findViewById(R.id.Refresh);


        Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                Toast.makeText(getApplicationContext(), "Refresh", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                        if(networkInfo!=null && networkInfo.isConnected()) {
                            String username = sharedPreferences.getString("username", "guest");
                            if (username.equals("guest")) {
                                startActivity(new Intent(getApplicationContext(), LoginSignup.class));
                                finish();
                            } else {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }
                        Refresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }
}