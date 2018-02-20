package com.buggyarts.instafeedplus.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by mayank on 2/14/18
 */

public class Share {

    public Bitmap bitmap;
    public View view;
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    FileOutputStream outputStream;
    ByteArrayOutputStream bytes;

    public Share(View view) {
        this.view = view;
    }

    public String shareScreenShot() {
        View screenShot = view.getRootView();
        screenShot.setDrawingCacheEnabled(true);
        bitmap = screenShot.getDrawingCache();
        createImage();
        return path + "/capturedScreenShot.jpg";
    }

    public void createImage() {

        bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (!dir.exists())
            dir.mkdirs();

        try {
            outputStream = new FileOutputStream(Constants.file);
            outputStream.write(bytes.toByteArray());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bitmap = null;
        bytes = null;
        outputStream = null;
//        Constants.file = null;
    }
}
