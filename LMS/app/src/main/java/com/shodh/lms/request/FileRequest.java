package com.shodh.lms.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import java.util.Map;

public class FileRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;
    private final Response.ErrorListener mErrorListener;
    public Map<String, String> responseHeaders;

    public FileRequest(int method, String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        super(method, url,errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers;

        //Pass the response data here
        return Response.success( response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }
}
