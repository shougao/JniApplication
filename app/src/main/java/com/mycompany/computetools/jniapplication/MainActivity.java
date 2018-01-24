package com.mycompany.computetools.jniapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // ====================network part.
    private static final String ENDPOINT = "https://kylewbanks.com/rest/posts.json";
    private RequestQueue requestQueue;
    private Gson gson;

    // ====================jni part
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public native String dynamicRegFromJni(String json);     //动态方法注册

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(dynamicRegFromJni("json"));
        Toast.makeText(this, dynamicRegFromJni("json"), Toast.LENGTH_SHORT).show();

        //=======for volley and gson.
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        fetchPosts();
    }

    private void fetchPosts() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onPostLoaded, onPostError);
        requestQueue.add(request);
    }

    private Response.Listener<String> onPostLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<Post> posts = Arrays.asList(gson.fromJson(response, Post[].class));

            Iterator<Post> iterator = posts.iterator();
            while (iterator.hasNext()) {
                Post post = iterator.next();
                Log.d(TAG, "ID:" + post.ID + ", TITLE:" + post.title);
            }
        }
    };

    private Response.ErrorListener onPostError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, error.toString());
        }
    };
}
