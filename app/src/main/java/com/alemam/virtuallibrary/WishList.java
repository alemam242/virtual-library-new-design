package com.alemam.virtuallibrary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WishList extends Fragment {
    ProgressBar progressBar;
    GridView gridView;
    TextView empty;
    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList;
    RequestQueue queue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_wish_list, container, false);
        progressBar = myView.findViewById(R.id.progressBar);
        gridView = myView.findViewById(R.id.gridView);
        empty = myView.findViewById(R.id.empty);
        progressBar.setVisibility(View.VISIBLE);

        getData();

            MyAdapter myAdapter = new MyAdapter();
            gridView.setAdapter(myAdapter);


        return myView;
    }

    private class MyAdapter extends BaseAdapter {
        ImageView bookImage,removeButton,heartIcon;
        TextView bookTitle,bookAuthor;
        Button bookButton;

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
            View myView = inflater.inflate(R.layout.fav_book_item,viewGroup,false);
            bookImage = myView.findViewById(R.id.bookImage);
            bookTitle = myView.findViewById(R.id.bookTitle);
            bookAuthor = myView.findViewById(R.id.bookAuthor);
            bookButton = myView.findViewById(R.id.bookButton);
            removeButton = myView.findViewById(R.id.removeButton);
            heartIcon = myView.findViewById(R.id.heartIcon);

            hashMap = new HashMap<>();
            hashMap = arrayList.get(i);
            String userId = hashMap.get("userId");
            String Id = hashMap.get("bookId");
            String cId = hashMap.get("catId");
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
                    Intent myIntent = new Intent(getActivity(), loadPdf.class);
                    myIntent.putExtra("pdfUrl", pdfLink);
                    myIntent.putExtra("pdfName", Name);
                    myIntent.putExtra("pdfId", Id);
                    myIntent.putExtra("catId", cId);
                    startActivity(myIntent);
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Confirmation")
                            .setMessage("Remove this book from your Favourite List!")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String url="https://appservicebysuvo.000webhostapp.com/app/removeFavBooks.php?uid="+userId+"&bid="+Id+"&cid="+cId;


                                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            progressBar.setVisibility(View.GONE);
                                            try {
                                                String res = response.getString("result");
                                                if (res.equals("removed"))
                                                {
                                                    getData();
                                                    Toast.makeText(getActivity(), "Book Removed from your Favourite List", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
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
                                    queue = Volley.newRequestQueue(getActivity());
                                    queue.add(objectRequest);
//
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setIcon(R.drawable.warning)
                            .show();
                }
            });
            return myView;
        }
    }



    private void getData()
    {
        arrayList = new ArrayList<>();
        String UserId = MainActivity.Userid;
        String url="https://appservicebysuvo.000webhostapp.com/app/getFavBooks.php?uid="+UserId;

        progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                if (response.length()<=0)
                {
                    gridView.setEmptyView(empty);
                }else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String userId = object.getString("user_id");
                            String bookId = object.getString("book_id");
                            String catId = object.getString("cat_id");
                            String bookName = object.getString("book_name");
                            String bookAuthor = object.getString("book_author");
                            String bookImage = object.getString("book_image");
                            String bookLink = object.getString("book_link");

                            hashMap = new HashMap<>();
                            hashMap.put("userId", userId);
                            hashMap.put("bookId", bookId);
                            hashMap.put("catId", catId);
                            hashMap.put("bookName", bookName);
                            hashMap.put("bookAuthor", bookAuthor);
                            hashMap.put("bookImage", bookImage);
                            hashMap.put("bookLink", bookLink);
                            arrayList.add(hashMap);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ErrorResponse",""+error);
            }
        });
        queue = Volley.newRequestQueue(getActivity());
        queue.add(arrayRequest);
    }
}