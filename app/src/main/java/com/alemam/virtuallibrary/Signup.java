package com.alemam.virtuallibrary;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

public class Signup extends Fragment {

    LinearLayout signupLayout;
    EditText name,userName,email,passWord,conPassword;
    Button signupButton;
    CheckBox showPassword;

    TextView waitText;
    LottieAnimationView animationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_signup, container, false);


        name = myView.findViewById(R.id.name);
        userName = myView.findViewById(R.id.userName);
        email = myView.findViewById(R.id.email);
        showPassword = myView.findViewById(R.id.showPassword);
        passWord = myView.findViewById(R.id.passWord);
        conPassword = myView.findViewById(R.id.conPassword);
        signupButton = myView.findViewById(R.id.signupButton);
        animationView = myView.findViewById(R.id.animationView);
        waitText = myView.findViewById(R.id.waitText);
        signupLayout = myView.findViewById(R.id.signupLayout);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked)
                {
                    //Show Password
                    passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    conPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    //Hide Password
                    passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    conPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name= name.getText().toString();
                String username= userName.getText().toString();
                String Email= email.getText().toString();
                String password= passWord.getText().toString();
                String conPass= conPassword.getText().toString();

                if(Name.isEmpty())
                {
                    name.setError("Enter Your Name");
                } else if (username.isEmpty()) {
                    userName.setError("Enter Your Username");
                }else if (Email.isEmpty()) {
                    email.setError("Enter Your Email");
                }else if (password.isEmpty()) {
                    passWord.setError("Set Your Password");
                }else if (conPass.isEmpty()) {
                    conPassword.setError("Confirm Your Password");
                }else if (password.length()<5) {
                    passWord.setError("Password must contain 5 characters");
                }else if (conPass.length()<5) {
                    conPassword.setError("Password must contain 5 characters");
                }
                else
                {
                    username = username.toLowerCase();
                    if(!password.equals(conPass))
                    {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Invalid Password")
                                .setMessage("Check your password")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }
                    else
                    {
                        String url="https://appservicebysuvo.000webhostapp.com/app/signup.php?nm="+Name+"&un="+username+"&em="+Email
                                +"&ps="+password;

                        animationView.setAnimation(R.raw.progress);
                        animationView.loop(true);
                        animationView.playAnimation();

                        waitText.setVisibility(View.VISIBLE);

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                if(response.contains("Success")) {
                                    animationView.setAnimation(R.raw.signup);
                                    animationView.loop(true);
                                    animationView.playAnimation();
                                    waitText.setVisibility(View.GONE);

                                    AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                                    LayoutInflater factory = LayoutInflater.from(getActivity());
                                    final View view = factory.inflate(R.layout.after_signup, null);
                                    alertadd.setView(view);
                                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            LoginSignup.tabLayout.getTabAt(0).select();
                                        }
                                    });
                                    alertadd.show();
                                }
                                else if(response.contains("Failed"))
                                {
                                    animationView.setAnimation(R.raw.signup);
                                    animationView.loop(true);
                                    animationView.playAnimation();
                                    waitText.setVisibility(View.GONE);

                                    AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                                    LayoutInflater factory = LayoutInflater.from(getActivity());
                                    final View view = factory.inflate(R.layout.login_failed, null);
                                    alertadd.setView(view);
                                    alertadd.setTitle("Registration Failed");
                                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertadd.show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                animationView.setAnimation(R.raw.signup);
                                animationView.loop(true);
                                animationView.playAnimation();
                                waitText.setVisibility(View.GONE);

                                Log.d("ResponseError",""+error);
                                AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                                LayoutInflater factory = LayoutInflater.from(getActivity());
                                final View view = factory.inflate(R.layout.login_failed, null);
                                alertadd.setView(view);
                                alertadd.setTitle("Registration Failed");
                                alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                alertadd.show();
                            }
                        });

                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        queue.add(stringRequest);
                    }
                }
            }
        });

//        Animation RightToLeft = AnimationUtils.loadAnimation(getContext(),R.anim.right_to_left);
//        signupLayout.startAnimation(RightToLeft);

        return myView;
    }


}