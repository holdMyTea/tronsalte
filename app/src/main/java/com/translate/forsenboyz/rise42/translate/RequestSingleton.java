package com.translate.forsenboyz.rise42.translate;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by rise42 on 11/03/17.
 */

public class RequestSingleton {

    private static RequestSingleton instance;
    private RequestQueue queue;

    public static RequestSingleton getInstance(Context context){
        if(instance == null){
            instance = new RequestSingleton(context);
        }
        return instance;
    }

    private RequestSingleton(Context context){
        queue = Volley.newRequestQueue(context);
    }

    public RequestQueue getQueue() {
        return queue;
    }
}
