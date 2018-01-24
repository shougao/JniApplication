package com.mycompany.computetools.jniapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

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
    }

}
