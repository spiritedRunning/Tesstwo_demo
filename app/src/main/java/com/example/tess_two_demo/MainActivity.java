package com.example.tess_two_demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tess_two_demo.util.AssetsUtil;
import com.example.tess_two_demo.util.FileUtil;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String LANGUAGE_FILE_NAME = "chi_sim";

    private ProgressBar mProgressbar;
    private TessBaseAPI tessBaseAPI;

    private TextView recogResultTv;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        recogResultTv = findViewById(R.id.recognize_result);
        mProgressbar = findViewById(R.id.progressbar);
        imageView = findViewById(R.id.image);

        copyAssets();
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, 1);

        }
        return false;
    }

    private void copyAssets() {
        new Thread(() -> {
            AssetsUtil.getInstance(MainActivity.this).
                    copyAssetsToSD("traindata",  "tessdata")
                    .setFileOperateCallback(new AssetsUtil.FileOperateCallback() {
                        @Override
                        public void onSuccess() {
                            initTessTwo();
                        }

                        @Override
                        public void onFailed(String error) {
                            Log.e(TAG, "assets copy failed");
                        }
                    });
        }).start();
    }

    private void initTessTwo() {
        Log.i(TAG, "start initTessTwo ");
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(Environment.getExternalStorageDirectory().getAbsolutePath(), LANGUAGE_FILE_NAME);

        Toast.makeText(this, "语音模型初始化完成", Toast.LENGTH_SHORT).show();
    }

    private static final String SCREENSHOT_IMG = "/sdcard/sreenshot.png";

    public void OnRecognize(View view) {
        mProgressbar.setVisibility(View.VISIBLE);
        recogResultTv.setText("");

        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            tessBaseAPI.setImage(new File(SCREENSHOT_IMG));
            String text = tessBaseAPI.getUTF8Text();
            Log.i(TAG, "result: " + text.replace(" ", ""));

            Log.e(TAG, "recognize complete, using " + (System.currentTimeMillis() - startTime) / 1000 + "s");
            runOnUiThread(() -> {
                mProgressbar.setVisibility(View.GONE);
                recogResultTv.setText(text);
            });
        }).start();

    }

    public void OnSnapshot(View view) {
        getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bmp = getWindow().getDecorView().getDrawingCache();
        imageView.setImageBitmap(bmp);

        FileUtil.saveBitmap(this, SCREENSHOT_IMG, bmp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tessBaseAPI != null) {
            tessBaseAPI.recycle();
        }
    }
}