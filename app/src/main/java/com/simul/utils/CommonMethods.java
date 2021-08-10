package com.simul.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

public class CommonMethods {

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Bitmap exifCheck(Bitmap bitmap, String path) {

        ExifInterface ei = null;

        int orientation = 0;

        try {

            ei = new ExifInterface(path);

            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

        } catch (IOException e) {

            e.printStackTrace();

        }

        Bitmap rotatedBitmap = null;

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotatedBitmap = rotateImage(bitmap, 90);


            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotatedBitmap = rotateImage(bitmap, 180);


            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotatedBitmap = rotateImage(bitmap, 270);


            case ExifInterface.ORIENTATION_NORMAL:
            default:
                return rotatedBitmap = bitmap;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {

        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
