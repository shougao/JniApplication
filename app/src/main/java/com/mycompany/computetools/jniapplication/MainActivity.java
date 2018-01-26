package com.mycompany.computetools.jniapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName() + "zqc";

    private EditText firstEditText;
    private EditText secondEditText;
    private TextView resultView;

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
        firstEditText = (EditText) findViewById(R.id.firseditText);
        secondEditText = (EditText) findViewById(R.id.secondeditText);
        resultView = (TextView) findViewById(R.id.result_text);


        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.result_text);
//        tv.setText(dynamicRegFromJni("json"));
//        Toast.makeText(this, dynamicRegFromJni("json"), Toast.LENGTH_SHORT).show();

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


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void gsonTest(View view) {

        // encode
        Gson gson = new Gson();
        NativeMethod method = new NativeMethod("add", 3, 9);
        String jsongObj = gson.toJson(method);
        Log.d(TAG, jsongObj);

        // decode
        //{"command":"add","parameter1":3,"parameter2":9,"result":0}
        String jsonString = "{\"command\":\"sub\",\"parameter1\":7,\"parameter2\":2,\"result\":5}";
        NativeMethod method2 = gson.fromJson(jsonString, NativeMethod.class);
        Log.d(TAG, "command = " + method2.command);
    }

    public void addEvent(View view) {
        int firstNumber = Integer.parseInt(firstEditText.getEditableText().toString());
        int secondNumber = Integer.parseInt(secondEditText.getEditableText().toString());
        NativeMethod method = new NativeMethod("add", firstNumber, secondNumber);
        String jsongObj = gson.toJson(method);
        String result = dynamicRegFromJni(jsongObj);
        Log.d(TAG, "result string = " + result);

        resultView.setText("result = " + result);
        NativeMethod m = gson.fromJson(result, NativeMethod.class);
        Log.d(TAG, "command = " + m.command + ", result=" + m.result);
    }

    public void multiEvent(View view) {
        int firstNumber = Integer.parseInt(firstEditText.getEditableText().toString());
        int secondNumber = Integer.parseInt(secondEditText.getEditableText().toString());
        NativeMethod method = new NativeMethod("multi", firstNumber, secondNumber);
        String jsongObj = gson.toJson(method);
        String result = dynamicRegFromJni(jsongObj);
        Log.d(TAG, "result string = " + result);
        resultView.setText("result = " + result);

        NativeMethod m = gson.fromJson(result, NativeMethod.class);
        Log.d(TAG, "command = " + m.command + ", result=" + m.result);
    }

    public void subEvent(View view) {
        int firstNumber = Integer.parseInt(firstEditText.getEditableText().toString());
        int secondNumber = Integer.parseInt(secondEditText.getEditableText().toString());
        NativeMethod method = new NativeMethod("sub", firstNumber, secondNumber);
        String jsongObj = gson.toJson(method);
        String result = dynamicRegFromJni(jsongObj);
        Log.d(TAG, "result string = " + result);
        resultView.setText("result = " + result);

        NativeMethod m = gson.fromJson(result, NativeMethod.class);
        Log.d(TAG, "command = " + m.command + ", result=" + m.result);
    }
}
