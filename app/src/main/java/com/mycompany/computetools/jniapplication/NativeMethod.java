package com.mycompany.computetools.jniapplication;

/**
 * Created by zhangqc8 on 1/26/2018.
 */

public class NativeMethod {
    //和native定义保持一致
    public String command;
    public int parameter1;
    public int parameter2;
    public int result;


    public NativeMethod(String add, int i, int j) {
        command = add;
        parameter1 = i;
        parameter2 = j;
    }
}
