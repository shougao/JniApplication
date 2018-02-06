package com.mycompany.computetools.jniapplication;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class MainActivity extends Activity implements Handler.Callback {


    private static final String TAG = MainActivity.class.getSimpleName() + "zqc";

    private EditText firstEditText;
    private EditText secondEditText;
    private TextView resultView;

    // ====================network part.
    private static final String ENDPOINT = "https://kylewbanks.com/rest/posts.json";
    private RequestQueue requestQueue;
    private Gson mGson;

    // ====================jni part
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public native String dynamicRegFromJni(String json);     //动态方法注册


    private static final int CHECK_UPDATE_MESSAGE = 100;
    private static final long CHECK_UPDATE_INTERVAL_MS = 1000 * 1000 * 10;

    private Handler mUpdater;
    private HandlerThread mUpdaterThread;
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            if (mUpdater != null) {
                mUpdater.sendEmptyMessage(CHECK_UPDATE_MESSAGE);
            }
        }

        @Override
        public void onLost(Network network) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstEditText = findViewById(R.id.firseditText);
        secondEditText = findViewById(R.id.secondeditText);
        resultView = findViewById(R.id.result_text);

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        mGson = gsonBuilder.create();
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.result_text);
//        tv.setText(dynamicRegFromJni("json"));
//        Toast.makeText(this, dynamicRegFromJni("json"), Toast.LENGTH_SHORT).show();
    }

    private void fetchPosts() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onPostLoaded, onPostError);
        requestQueue.add(request);
    }

    private Response.Listener<String> onPostLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<Post> posts = Arrays.asList(mGson.fromJson(response, Post[].class));

            Iterator<Post> iterator = posts.iterator();
            int itemNumbers = 0;
            while (iterator.hasNext()) {
                Post post = iterator.next();
//                Log.d(TAG, "ID:" + post.ID + ", TITLE:" + post.title);
                itemNumbers++;
            }
            Toast.makeText(getApplication(), "on line Data Parse successful.number= " + itemNumbers, Toast.LENGTH_LONG).show();
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

//        mConnectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
        mUpdaterThread = new HandlerThread("check update version in background");
        mUpdaterThread.start();
        mUpdater = new Handler(mUpdaterThread.getLooper(), this);
        mUpdater.sendEmptyMessage(CHECK_UPDATE_MESSAGE);

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


        //=======parse online json data, using volley and gson.
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        fetchPosts();
    }

    @Override
    public boolean handleMessage(Message message) {
        //网络检查行为。

        synchronized (this) {
            if (mUpdater != null) {
                mUpdater.removeMessages(CHECK_UPDATE_MESSAGE);
                mUpdater.sendEmptyMessageDelayed(CHECK_UPDATE_MESSAGE, CHECK_UPDATE_INTERVAL_MS);//周期检查
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop monitoring
        mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);

        mUpdater.removeCallbacksAndMessages(null);
        mUpdater = null;
        mUpdaterThread.quit();
        mUpdaterThread = null;
    }

    public void addEvent(View view) {
        int firstNumber = Integer.parseInt(firstEditText.getEditableText().toString());
        int secondNumber = Integer.parseInt(secondEditText.getEditableText().toString());
        NativeMethod method = new NativeMethod("add", firstNumber, secondNumber);
        String jsongObj = mGson.toJson(method);
        String result = dynamicRegFromJni(jsongObj);
        Log.d(TAG, "result string = " + result);

        resultView.setText("result = " + result);
        NativeMethod m = mGson.fromJson(result, NativeMethod.class);
        Log.d(TAG, "command = " + m.command + ", result=" + m.result);
    }

    public void multiEvent(View view) {
        int firstNumber = Integer.parseInt(firstEditText.getEditableText().toString());
        int secondNumber = Integer.parseInt(secondEditText.getEditableText().toString());
        NativeMethod method = new NativeMethod("multi", firstNumber, secondNumber);
        String jsongObj = mGson.toJson(method);
        String result = dynamicRegFromJni(jsongObj);
        Log.d(TAG, "result string = " + result);
        resultView.setText("result = " + result);

        NativeMethod m = mGson.fromJson(result, NativeMethod.class);
        Log.d(TAG, "command = " + m.command + ", result=" + m.result);
    }

    public void subEvent(View view) {
        int firstNumber = Integer.parseInt(firstEditText.getEditableText().toString());
        int secondNumber = Integer.parseInt(secondEditText.getEditableText().toString());
        NativeMethod method = new NativeMethod("sub", firstNumber, secondNumber);
        String jsongObj = mGson.toJson(method);
        String result = dynamicRegFromJni(jsongObj);
        Log.d(TAG, "result string = " + result);
        resultView.setText("result = " + result);

        NativeMethod m = mGson.fromJson(result, NativeMethod.class);
        Log.d(TAG, "command = " + m.command + ", result=" + m.result);
    }

    public void runOnThread(View view) {

    }
}
