package com.alemam.virtuallibrary;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {

    TextView tvName,tvUsername,tvUser,tvEmail;
    LinearLayout editButton,ProfileCard;

    CircleImageView userImage;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList;

    public static String name="",username="",email="",img="";
    RequestQueue queue;
    public static boolean editProfile=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_profile, container, false);



        sharedPreferences = getContext().getSharedPreferences(""+R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userImage = myView.findViewById(R.id.userImage);
        progressBar = myView.findViewById(R.id.progressBar);
        ProfileCard = myView.findViewById(R.id.ProfileCard);
        editButton = myView.findViewById(R.id.editButton);

        tvUsername = myView.findViewById(R.id.tvUsername);
        tvUser = myView.findViewById(R.id.tvUser);
        tvName = myView.findViewById(R.id.tvName);
        tvEmail = myView.findViewById(R.id.tvEmail);

        tvUsername.setText(MainActivity.Username);

        progressBar.setVisibility(View.VISIBLE);
        ProfileCard.setVisibility(View.GONE);

        getProfileDetails();

//        tvName.setText("Name        : ");
//        tvUser.setText("Username : ");
//        tvEmail.setText("Email         : ");
//
//        tvName.append(name);
//        tvUser.append(username);
//        tvEmail.append(email);


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText Name,UserName,Email;
                Button updateButton;
                ProgressBar progressBar;


                AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View anotherView = factory.inflate(R.layout.edit_profile, null);

                Name = anotherView.findViewById(R.id.Name);
                UserName = anotherView.findViewById(R.id.UserName);
                Email = anotherView.findViewById(R.id.Email);
                updateButton = anotherView.findViewById(R.id.updateButton);
                progressBar = anotherView.findViewById(R.id.progressBar);


                Name.setText(name);
                UserName.setText(username);
                Email.setText(email);

                alertadd.setView(anotherView);
                alertadd.setCancelable(true);
                alertadd.show();


                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nm = Name.getText().toString();
                        String un = UserName.getText().toString();
                        String em = Email.getText().toString();

                        if(!nm.equals(MainActivity.FullName) || !un.equals(MainActivity.Username) || !em.equals(MainActivity.Email))
                        {
//                            editProfile = true;
                                progressBar.setVisibility(View.VISIBLE);

                                String url = "https://appservicebysuvo.000webhostapp.com/app/updateProfile.php?uid=" + MainActivity.Userid + "&nm=" + nm + "&un=" + un + "&em=" + em;

                                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        progressBar.setVisibility(View.GONE);
                                        try {
                                            String res = response.getString("result");

                                            if (res.equals("updated")) {
                                                editProfile = true;
                                                editor.clear().commit();

                                                String nm = Name.getText().toString();
                                                String unm = UserName.getText().toString();
                                                String eml = Email.getText().toString();

                                                editor.putString("username", unm);
                                                editor.putString("name", nm);
                                                editor.putString("email", eml);
                                                editor.putString("userid", MainActivity.Userid);
                                                editor.apply();


                                                Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show();

                                                getProfileDetails();

                                                alertadd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialogInterface) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                            } else {
                                                editProfile = false;
                                                Toast.makeText(getActivity(), "Profile can't be updated", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                                queue.add(objectRequest);


                            //end of outter if
                        }
                        else
                        {
//                            editProfile = false;
                            Toast.makeText(getActivity(), "You didn't changed anything!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


        return myView;
    }


    private void getProfileDetails()
    {

        String url = "https://appservicebysuvo.000webhostapp.com/app/getProfileDetails.php?uid="+MainActivity.Userid;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                ProfileCard.setVisibility(View.VISIBLE);

                Animation LeftToRight = AnimationUtils.loadAnimation(getContext(), R.anim.up_from_bottom);
                ProfileCard.startAnimation(LeftToRight);

                Log.d("DResponse","result: "+response);
                try {
                    name=response.getString("name");
                    username=response.getString("username");
                    email=response.getString("email");

                    MainActivity.FullName = name;
                    MainActivity.Username = username;
                    MainActivity.Email = email;

                    tvName.setText(name);
                    tvUser.setText(username);
                    tvEmail.setText(email);

                    Log.d("DResponse","result: "+arrayList);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                ProfileCard.setVisibility(View.VISIBLE);
                Animation LeftToRight = AnimationUtils.loadAnimation(getContext(), R.anim.up_from_bottom);
                ProfileCard.startAnimation(LeftToRight);
                Log.d("DResponse","error: "+error);
            }
        });

        queue = Volley.newRequestQueue(getActivity());
        queue.add(objectRequest);
    }

}