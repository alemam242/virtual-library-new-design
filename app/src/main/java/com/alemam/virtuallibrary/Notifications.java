package com.alemam.virtuallibrary;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Notifications extends Fragment {

    ProgressBar progressBar;
    GridView gridView;
    String imgUrl="";
    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList;
    RequestQueue queue;
    int count=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_notifications, container, false);

        progressBar = myView.findViewById(R.id.progressBar);
        gridView = myView.findViewById(R.id.gridView);

        getData();

        MyAdapter myAdapter = new MyAdapter();
        gridView.setAdapter(myAdapter);




        return myView;
    }


    private class MyAdapter extends BaseAdapter{

        ImageView bookImage;
        TextView notification,date,time;
        LinearLayout notificationLayout;
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
            View myView = inflater.inflate(R.layout.notification_item,viewGroup,false);
            bookImage = myView.findViewById(R.id.bookImage);
            notification = myView.findViewById(R.id.notification);
            notificationLayout = myView.findViewById(R.id.notificationLayout);
            date = myView.findViewById(R.id.date);
            time = myView.findViewById(R.id.time);


            hashMap = new HashMap<>();
            hashMap = arrayList.get(i);
            String Name=hashMap.get("bookName");
            String Image=hashMap.get("bookImage");
            String status=hashMap.get("status");
            String dateTime=hashMap.get("date");
            String[] array = dateTime.split(" ");
            String dt = array[0]; // this will contain "Fruit"
            String tm = array[1];

            Picasso.get()
                    .load(Image)
                    .placeholder(R.drawable.book_cover)
                    .error(R.drawable.book_cover)
                    .into(bookImage);

            notification.setText("“"+Name+" ” added in your wishlist");
            date.setText(dt);
            time.setText(tm);

            if(status.equals("new"))
            {
                count++;
                notification.setTypeface(null, Typeface.BOLD_ITALIC);
            }
            else
            {
                notification.setTypeface(null, Typeface.ITALIC);
            }

            if (i%2==0) {
                Animation LeftToRight = AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right);
                notificationLayout.startAnimation(LeftToRight);
            }
            else
            {
                Animation LeftToRight = AnimationUtils.loadAnimation(getContext(), R.anim.right_to_left);
                notificationLayout.startAnimation(LeftToRight);
            }
            return myView;
        }
    }

    private void getData()
    {
        progressBar.setVisibility(View.VISIBLE);
        arrayList = new ArrayList<>();
        String url = "https://appservicebysuvo.000webhostapp.com/app/getNotification.php?uid="+MainActivity.Userid;

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                for (int i=0;i<response.length();i++)
                {
                    try {
                        JSONObject object = response.getJSONObject(i);

                        String bookName = object.getString("book_name");
                        String bookImage = object.getString("book_image");
                        String status = object.getString("status");
                        String date = object.getString("date");


                        hashMap = new HashMap<>();
                        hashMap.put("bookName",bookName);
                        hashMap.put("bookImage",bookImage);
                        hashMap.put("status",status);
                        hashMap.put("date",date);
                        arrayList.add(hashMap);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
        queue = Volley.newRequestQueue(getActivity());
        queue.add(arrayRequest);
    }

    private void updateNotification(){
        String url="https://appservicebysuvo.000webhostapp.com/app/updateNotification.php?uid="+MainActivity.Userid;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String result = response.getString("result");

                    if(result.equals("success"))
                    {
                        Toast.makeText(getActivity(), "Notification updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "Notification update failed", Toast.LENGTH_SHORT).show();
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
    }
}