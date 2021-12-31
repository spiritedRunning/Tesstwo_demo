package com.example.tess_two_demo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zach on 2021/12/31 13:53
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    public static void saveBitmap(Context mContext, String path, Bitmap bm) {
//        String path = mContext.getFilesDir() + "/images/";
        Log.d(TAG, "Save Path=" + path);

        if (!fileIsExist(path)) {
            Log.d(TAG, "TargetPath isn't exist");
        } else {
            File saveFile = new File(path);

            try {
                FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                saveImgOut.flush();
                saveImgOut.close();
                Log.d(TAG, "The picture is save to your phone!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean fileIsExist(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        } else {
            return file.mkdirs();
        }
    }

    public static boolean copyAssetFile(Context context, String assetPath, String path) {
        boolean flag = false;
        int BUFFER = 10240;
        InputStream inputStream = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        byte b[] = null;
        try {
            inputStream = context.getAssets().open(assetPath);
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                if (inputStream != null && inputStream.available() == file.length()) {
                    flag = true;
                } else
                    file.delete();
            }
            if (!flag) {
                in = new BufferedInputStream(inputStream, BUFFER);
                boolean isOK = file.createNewFile();
                if (in != null && isOK) {
                    out = new BufferedOutputStream(new FileOutputStream(file), BUFFER);
                    b = new byte[BUFFER];
                    int read = 0;
                    while ((read = in.read(b)) > 0) {
                        out.write(b, 0, read);
                    }
                    flag = true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

}
