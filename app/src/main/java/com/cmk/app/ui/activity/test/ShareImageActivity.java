package com.cmk.app.ui.activity.test;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cmk.app.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);
        ImageView imageView = findViewById(R.id.imageView);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(ShareImageActivity.this, R.layout.layout_share_image, null);
//                View decorView = ShareImageActivity.this.getWindow().getDecorView();
//                decorView.setDrawingCacheEnabled(true);
//                decorView.buildDrawingCache();
                Bitmap bitmap = getBitmap(view);
                imageView.setImageBitmap(bitmap);
                save(bitmap);
            }
        });
    }

    /**
     * 初始化布局
     */
    private void initLayout() {

    }

    /**
     * 将布局转为bitmap
     *
     * @param view
     * @return
     */
    public Bitmap getBitmap(View view) {

        // wrap_content
        int widthTemp = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
        int heightTemp = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
        // match_parent
//        int widthTemp = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
//        int heightTemp = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
        view.measure(widthTemp, heightTemp);
        //获取view的长宽
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        view.layout(0, 0, width, height);
        //若传入的view长或宽为小于等于0，则返回，不生成图片
        if (width <= 0 || height <= 0) {
            return null;
        }
        //生成一个ARGB8888的bitmap，宽度和高度为传入view的宽高
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        if (bm == null) {
            return null;
        }
        //根据bitmap生成一个画布
        Canvas canvas = new Canvas(bm);
        //注意：这里是解决图片透明度问题，给底色上白色，若存储时保存的为png格式的图，则无需此步骤
        canvas.drawColor(Color.WHITE);
        //手动将这个视图渲染到指定的画布上
        view.draw(canvas);
        return bm;
    }

    /**
     * 将bitmap导出到本地
     *
     * @param bmp
     */
    public void saveCroppedImage(Bitmap bmp) {
        // 判断是否可以对SDcard进行操作
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {      // 获取SDCard指定目录下
            String sdCardDir = Environment.getExternalStorageDirectory() + "/BmpImage/";
            //目录转化成文件夹
            File dirFile = new File(sdCardDir);
            //如果不存在，那就建立这个文件夹
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            // 在SDcard的目录下创建图片文,以当前时间为其命名，注意文件后缀，若生成为JPEG则为.jpg,若为PNG则为.png
            File file = new File(sdCardDir, System.currentTimeMillis() + ".jpg");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                //将bitmap（数值100表示不压缩）存储到out输出流中去，图片格式为JPEG
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                //bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void save(Bitmap bmp) {
        String name = System.currentTimeMillis() + ".jpg";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
