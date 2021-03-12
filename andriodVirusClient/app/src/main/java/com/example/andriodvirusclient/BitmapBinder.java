package com.example.andriodvirusclient;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;

public class BitmapBinder extends Binder {
    private Bitmap bitmap;

    BitmapBinder(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    Bitmap getBitmap() {
        return bitmap;
    }
}
