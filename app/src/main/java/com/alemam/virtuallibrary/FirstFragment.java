package com.alemam.virtuallibrary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class FirstFragment extends Fragment {

    GridView gridView;
    ProgressBar progressBar;
    public static String ctg="";
    public static String id="0";
    String pdf_url="";
    String img_url="";
    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList;
    InterstitialAd mInterstitialAd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_first, container, false);
        gridView = myView.findViewById(R.id.gridView);
        progressBar = myView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        getBooks();

        loadInterstitialAd();

        MyAdapter myAdapter = new MyAdapter();
        gridView.setAdapter(myAdapter);


        return myView;
    }

    private void getBooks()
    {
        arrayList = new ArrayList<>();
        String url="https://appservicebysuvo.000webhostapp.com/app/getBook.php?ctg="+ctg+"&cid="+id;
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                for(int i=1;i<response.length();i++) {
                    try {
                        Log.d("Response",""+response);
                        JSONObject object = response.getJSONObject(i);
                        int catId = object.getInt("cat_id");
                        int bookId = object.getInt("book_id");
                        String bookName=object.getString("book_name");
                        String bookAuthor=object.getString("book_author");
                        String bookImage=object.getString("book_image");
                        String bookLink=object.getString("book_link");


                        hashMap = new HashMap<>();
                        hashMap.put("catId",""+catId);
                        hashMap.put("bookId",""+bookId);
                        hashMap.put("bookName",""+bookName);
                        hashMap.put("bookAuthor",""+bookAuthor);
                        hashMap.put("bookImage",""+bookImage);
                        hashMap.put("bookLink",""+bookLink);
                        arrayList.add(hashMap);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                Log.d("ArrayList",""+arrayList);
                if(ctg.contains("সকল বই")) {
                    SearchBook.arrayList = (ArrayList<HashMap<String, String>>) arrayList.clone();
                    Log.d("ArrayList", "Searchbook" + SearchBook.arrayList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ResponseError",""+error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(arrayRequest);
    }


    private class MyAdapter extends BaseAdapter{
        ImageView bookImage;
        TextView bookTitle,bookAuthor;
        Button bookButton;
        LinearLayout linearLayout;
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            View myView = inflater.inflate(R.layout.book_item,viewGroup,false);
            bookImage = myView.findViewById(R.id.bookImage);
            bookTitle = myView.findViewById(R.id.bookTitle);
            bookAuthor = myView.findViewById(R.id.bookAuthor);
            bookButton = myView.findViewById(R.id.bookButton);
            linearLayout = myView.findViewById(R.id.linearLayout);

            hashMap = new HashMap<>();
            hashMap = arrayList.get(i);
            String cId = hashMap.get("catId");
            String Id = hashMap.get("bookId");
            String Name=hashMap.get("bookName");
            String Author=hashMap.get("bookAuthor");
            String Image=hashMap.get("bookImage");
            String pdfLink=hashMap.get("bookLink");

            Picasso.get()
                    .load(Image)
                    .placeholder(R.drawable.book_loading)
                    .error(R.drawable.book_cover)
                    .into(bookImage);
            bookTitle.setText(Name);
            bookAuthor.setText(Author);

            bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(pdfLink.contains("null")) {
                        Toast.makeText(getActivity(), "Sorry! The book is not available right now", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        showInterstitialAd();

                        Intent myIntent = new Intent(getActivity(), loadPdf.class);
                        myIntent.putExtra("pdfUrl", pdfLink);
                        myIntent.putExtra("pdfName", Name);
                        myIntent.putExtra("pdfId", Id);
                        myIntent.putExtra("catId", cId);
                        startActivity(myIntent);
                    }
                }
            });



            return myView;
        }
    }




    private void showInterstitialAd()
    {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(getActivity());
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }
    private void loadInterstitialAd()
    {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getActivity(),"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        interstitialAdCallBack();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
    }

    private void interstitialAdCallBack()
    {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                mInterstitialAd = null;
                loadInterstitialAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                mInterstitialAd = null;
                loadInterstitialAd();
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
            }
        });
    }
}