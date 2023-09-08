package com.alemam.virtuallibrary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePassword extends Fragment {

    LottieAnimationView animationView;
    EditText newPass,conPass,oldPass;
    Button change;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_change_password, container, false);

        newPass = myView.findViewById(R.id.newPass);
        conPass = myView.findViewById(R.id.conPass);
        oldPass = myView.findViewById(R.id.oldPass);
        change = myView.findViewById(R.id.change);
        progressBar = myView.findViewById(R.id.progressBar);


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newpass = newPass.getText().toString();
                String conpass = conPass.getText().toString();
                String oldpass = oldPass.getText().toString();

                if(newpass.length()<5)
                {
                    newPass.setError("Password too short");
                }
                if(conpass.length()<5)
                {
                    conPass.setError("Password too short");
                }
                if(!conpass.equals(newpass))
                {
                    conPass.setError("Password doesn't matched");
                }
                if(conpass==newpass && conpass==oldpass)
                {
                    newPass.setError("set different password");
                    conPass.setError("set different password");
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);

                    String url="https://appservicebysuvo.000webhostapp.com/app/changePassword.php?uid="+MainActivity.Userid+"&nps="+newpass+"&ops="+oldpass;
                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressBar.setVisibility(View.GONE);

                            try {
                                String res = response.getString("result");

                                Log.d("ChangePassword",""+res+" "+newpass+" "+oldpass);

                                if(res.equals("changed")){
                                    newPass.setText("");
                                    conPass.setText("");
                                    oldPass.setText("");
                                    newPass.setError(null);
                                    conPass.setError(null);
                                    oldPass.setError(null);

                                    AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                                    LayoutInflater factory = LayoutInflater.from(getActivity());
                                    final View view = factory.inflate(R.layout.after_change_password, null);
                                    alertadd.setView(view);
                                    alertadd.setTitle("Password Changed");
                                    alertadd.setCancelable(true);
                                    alertadd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertadd.show();

                                    Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                                }
                                else if(res.equals("wrong")){
                                    Toast.makeText(getContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Password can't be changed", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            change.setText(""+error);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    queue.add(objectRequest);
                }
            }
        });

        return myView;
    }
}