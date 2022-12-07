package com.shodh.lms.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shodh.lms.request.CacheRequest;
import com.shodh.lms.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookLiveViewModel extends ViewModel {

    private MutableLiveData<JSONArray> books;
    private String URL;

    public BookLiveViewModel(String type) {
        this.books = new MutableLiveData<>();
        if(type.equals("BOOKS")){
            URL = Constants.GET_ALL_BOOK;
        }else if(type.equals("EBOOKS")){
            URL = Constants.GET_ALL_EBOOK;
        }
    }

    public MutableLiveData<JSONArray> getBooks(){
        return books;
    }

    public void makeApiCall(Context context,SharedPreferences user){

        CacheRequest cacheRequest = new CacheRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("LMS_TEST", "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONObject(response).getJSONArray("books");
                            books.postValue(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        books.postValue(null);
                        Log.i("LMS_TEST", "onErrorResponse: "+error.getMessage());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String bearer = user.getString("token_type","token_type")+" "+user.getString("access_token","access_token");
                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headersSys.remove("Authorization");
                headers.put("Authorization", bearer);
                headers.putAll(headersSys);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(cacheRequest);

    }
}
