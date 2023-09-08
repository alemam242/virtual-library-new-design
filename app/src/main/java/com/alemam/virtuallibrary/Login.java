package com.alemam.virtuallibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Login extends Fragment {

    LinearLayout loginLayout;
    EditText userName,passWord;
    CheckBox showPassword;

    TextView tvWait;
    Button loginButton;

    LottieAnimationView animationView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_login, container, false);

        userName = myView.findViewById(R.id.userName);
        passWord = myView.findViewById(R.id.passWord);
        loginButton = myView.findViewById(R.id.loginButton);
        showPassword = myView.findViewById(R.id.showPassword);
        animationView = myView.findViewById(R.id.animationView);
        tvWait = myView.findViewById(R.id.tvWait);
        loginLayout = myView.findViewById(R.id.loginLayout);

        sharedPreferences = getContext().getSharedPreferences(""+R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked)
                {
                    //Show Password
                    passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    //Hide Password
                    passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userName.getText().toString();
                String password = passWord.getText().toString();
//                new AlertDialog.Builder(getActivity())
//                        .setMessage(""+username+"\n"+password)
//                        .show();
                if(username.isEmpty())
                {
                    userName.setError("Enter Your Username");
                }
                else if(password.isEmpty())
                {
                    passWord.setError("Enter Your Password");
                }
                else
                {
                    username=username.toLowerCase();
                    String url="https://appservicebysuvo.000webhostapp.com/app/login.php?un="+username+"&ps="+password;

                    animationView.setAnimation(R.raw.progress);
                    animationView.loop(true);
                    animationView.playAnimation();
                    tvWait.setVisibility(View.VISIBLE);

                    String finalUsername = username;
                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            animationView.setAnimation(R.raw.login);
                            animationView.loop(true);
                            animationView.playAnimation();
                            tvWait.setVisibility(View.GONE);

                            Log.d("Response",""+response);
                            try {
                                boolean status = response.getBoolean("authenticate");


                                if(status)
                                {
                                    String UserId = response.getString("id");
                                    String Name = response.getString("name");
                                    String Username = response.getString("username");
                                    String Email = response.getString("email");

                                    editor.putString("userid",UserId);
                                    editor.putString("username",Username);
                                    editor.putString("name",Name);
                                    editor.putString("email",Email);
                                    editor.apply();

                                    Intent intent = new Intent(getContext(),MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                                else
                                {
                                    AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                                    LayoutInflater factory = LayoutInflater.from(getActivity());
                                    final View view = factory.inflate(R.layout.login_failed, null);
                                    alertadd.setView(view);
                                    alertadd.setTitle("Login Failed");
                                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertadd.show();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            animationView.setAnimation(R.raw.login);
                            animationView.loop(true);
                            animationView.playAnimation();
                            tvWait.setVisibility(View.GONE);

                            Log.d("ErrorResponse",""+error);

                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.drawable.warning)
                                    .setTitle("Error")
                                    .setMessage("Something Went Wrong\nTry Again Later")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });

                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    queue.add(objectRequest);
                }
            }
        });


//        Animation LeftToRight = AnimationUtils.loadAnimation(getContext(),R.anim.left_to_right);
//        loginLayout.startAnimation(LeftToRight);
        return myView;
    }
}