package com.example.andriodvirusclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bm.library.Info;
import com.bm.library.PhotoView;

public class Picture extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        PhotoView photoView = (PhotoView) findViewById(R.id.img);

        Bundle bundle = getIntent().getExtras();
        ObjectBinder objbinder = (ObjectBinder) bundle.getBinder("obj");
        Object obj = objbinder.getObj();

        //StringBuffer stringBuffer = (StringBuffer) obj;

          //System.out.println("debug :"+ bytes);
     // Bitmap bitmap =BitmapFactory.decodeResource(getResources(),R.drawable.bitmap );
//
        Bitmap bitmap=Base64Util.base64ToBitmap(obj.toString());
//        byte[] bytes= Base64Util.decode(((StringBuffer)obj).toString());
////
//        BitmapFactory.Options opts = new BitmapFactory.Options();
//        opts.inJustDecodeBounds = false;//为true时，返回的bitmap为null
//          Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length, opts);

        photoView.setImageBitmap(bitmap);
// 启用图片缩放功能
        photoView.enable();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
