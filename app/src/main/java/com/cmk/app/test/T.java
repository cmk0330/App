package com.cmk.app.test;

import android.graphics.Paint;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

/**
 * @Author: romens
 * @Date: 2019-11-6 14:35
 * @Desc:
 */
public class T extends LambdaTest {

    public String a;
    public int b;

    public void test() {
        LambdaTest lambdaTest = new LambdaTest();
        T t = new T(a, b);

        lambdaTest.setCallBack(new LaCallBack() {

            @Override
            public String sCallBack(String a, String b) {
                return null;
            }
        });

        List<?extends TextView> textViews = new ArrayList<Button>();
        TextView textView = textViews.get(0);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FormBody build = new FormBody.Builder().build();
    }

    public T(String a, int b) {
        this.a = a;
        this.b = b;
    }

    public static String imageToBase64(File path) {
        InputStream is = null;
        byte[] data;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        return result;
    }


}
