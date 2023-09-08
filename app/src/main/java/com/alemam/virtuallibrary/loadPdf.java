package com.alemam.virtuallibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class loadPdf extends AppCompatActivity {

    WebView webView;
    LottieAnimationView animationView;
    TextView bookTitle;
    ImageView heartIcon;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_pdf);
        webView = findViewById(R.id.webView);
        animationView = findViewById(R.id.animationView);
        bookTitle = findViewById(R.id.bookTitle);
        heartIcon = findViewById(R.id.heartIcon);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setBuiltInZoomControls(true);

        Bundle bundle = getIntent().getExtras();
        String pdfName = bundle.getString("pdfName");
        String pdfId = bundle.getString("pdfId");
        String catId = bundle.getString("catId");
        String pdfUrl = bundle.getString("pdfUrl");

        checkWishlist(catId,pdfId);

        if(bundle!=null)
        {
            String url="https://drive.google.com/file/d/"+pdfUrl+"/preview";

            bookTitle.setText(pdfName);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    setProgressBarVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    setProgressBarVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    setProgressBarVisibility(View.GONE);
                }
            });

            webView.loadUrl(url);
        }


        heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    heartIcon.setImageResource(R.drawable.heart_fill);
                if(heartIcon.getTag() == "heart_fill")
                {
                    Toast.makeText(loadPdf.this, "Already exists in your wishlist", Toast.LENGTH_SHORT).show();
                }else {
                    addToWishlist(pdfId,pdfName,catId);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
        {
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    private void setProgressBarVisibility(int visibility) {
        // If a user returns back, a NPE may occur if WebView is still loading a page and then tries to hide a ProgressBar.
        if (animationView != null) {
            animationView.setVisibility(visibility);
        }
    }


    private void checkWishlist(String catId,String bookId)
    {
        String UserId=MainActivity.Userid;
        String url="https://appservicebysuvo.000webhostapp.com/app/checkWishlist.php?uid="+UserId+"&bid="+bookId+"&cid="+catId;

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if(result.equals("exists"))
                    {
                        heartIcon.setTag("heart_fill");
                        heartIcon.setImageResource(R.drawable.heart_fill);
                    }
                    else {
                        heartIcon.setTag("heart");
                        heartIcon.setImageResource(R.drawable.white_heart);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CheckWishlist",""+error);
            }
        });
        queue = Volley.newRequestQueue(loadPdf.this);
        queue.add(objectRequest);
    }
    private void addToWishlist(String bookId,String bookName, String catId)
    {
        String UserId = MainActivity.Userid;
        String url="https://appservicebysuvo.000webhostapp.com/app/addToWishlist.php?uid="+UserId+"&bid="+bookId+"&bn="+bookName+"&cid="+catId;

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if(result.equals("success & notified"))
                    {
                        heartIcon.setTag("heart_fill");
                        heartIcon.setImageResource(R.drawable.heart_fill);
                        Toast.makeText(loadPdf.this, "Book added to your wishlist", Toast.LENGTH_SHORT).show();

//                        MainActivity obj = new MainActivity();
//                        obj.checkNotification(UserId);
                    }
                    else if(result.equals("success"))
                    {
                        heartIcon.setTag("heart_fill");
                        heartIcon.setImageResource(R.drawable.heart_fill);
                        Toast.makeText(loadPdf.this, "Book added to your wishlist", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        heartIcon.setTag("heart");
                        heartIcon.setImageResource(R.drawable.white_heart);
                        Toast.makeText(loadPdf.this, "Something Wrong! Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AddToWishlist",""+error);
            }
        });
        queue = Volley.newRequestQueue(loadPdf.this);
        queue.add(objectRequest);
    }
}