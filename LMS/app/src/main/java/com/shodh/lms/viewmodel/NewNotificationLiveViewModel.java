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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shodh.lms.Constants;
import com.shodh.lms.DashboardActivity;
import com.shodh.lms.adapter.NewNotificationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewNotificationLiveViewModel extends ViewModel {

    private MutableLiveData<JSONArray> notification;

    public NewNotificationLiveViewModel() {
        this.notification = new MutableLiveData<>();
    }

    public MutableLiveData<JSONArray> getNotification(){
        return notification;
    }

    public void makeApiCall(Context context,SharedPreferences user){

        StringRequest cacheRequest = new StringRequest(
                Request.Method.GET,
                Constants.GET_NEW_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("LMS_TEST", "onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONObject(response).getJSONArray("new_notifications");
                            notification.postValue(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        notification.postValue(null);
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
