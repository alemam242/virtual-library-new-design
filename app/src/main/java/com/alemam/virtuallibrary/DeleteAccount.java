package com.alemam.virtuallibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class DeleteAccount extends Fragment {

    TextView tvWarning;
    Button deleteButton;

    int count=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_delete_account, container, false);

        String text="Deleting your account is permanent. When you delete your account, all of your data will also be deleted. You won't be able to retrieve your information/data.";

        Log.d("DeleteAccount","It's Okay");
        tvWarning = myView.findViewById(R.id.tvWarning);
        deleteButton = myView.findViewById(R.id.deleteButton);


        tvWarning.setText(text);

        /*
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.warning)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure want to delete your account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteAccount();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });*/

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText passWord;
                Button delete;
                ProgressBar progressBar;
                AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View myView = factory.inflate(R.layout.delete_account, null);
                passWord = myView.findViewById(R.id.passWord);
                delete = myView.findViewById(R.id.delete);
                progressBar = myView.findViewById(R.id.progressBar);

                alertadd.setView(myView);
                alertadd.setIcon(R.drawable.warning);
                alertadd.setCancelable(false);
                alertadd.setTitle("Delete Account");
                alertadd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertadd.show();

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = passWord.getText().toString();
                        if(password.isEmpty() || password.length()<5)
                        {
                            passWord.setError("Invalid password");
                        }
                        else
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            String url="https://appservicebysuvo.000webhostapp.com/app/confirmDeleteAccount.php?un="+MainActivity.Username+"&ps="+password;

                            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressBar.setVisibility(View.GONE);
                                    try {
                                        boolean result = response.getBoolean("result");

                                        if(result)
                                        {
                                            deleteAccount();
                                        }
                                        else
                                        {
                                            passWord.setText("");
                                            passWord.setError("Wrong Password");
                                            tryDelete();
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });

                            RequestQueue queue = Volley.newRequestQueue(getActivity());
                            queue.add(objectRequest);
                        }
                    }
                });
            }
        });

        return myView;
    }

    private void tryDelete()
    {
        count++;
        if(count>=3)
        {
            MainActivity.editor.clear().commit();
//            startActivity(new Intent(getContext(),LoginSignup.class));

            Intent intent = new Intent(getContext(),LoginSignup.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
    private void deleteAccount()
    {
        String userId = MainActivity.Userid;
        String url="https://appservicebysuvo.000webhostapp.com/app/deleteAccount.php?uid="+userId;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String res = response.getString("result");
                    if (res.equals("deleted"))
                    {
                        MainActivity.editor.clear().commit();
                        Intent intent = new Intent(getContext(),LoginSignup.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else
                    {
                        Toast.makeText(getActivity(), "Something Went Wrong! Try Again", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(objectRequest);
    }
}