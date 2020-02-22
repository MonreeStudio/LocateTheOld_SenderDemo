package com.monree.senderdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.autofill.OnClickAction;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.List;
import java.lang.String;

public class MainActivity extends AppCompatActivity {

    EditText phoneNum;
    EditText messageCnt;
    Button sendBtn;
    Context context;
    IntentFilter filter;
    SmsReceiver receiver;
    TextView ContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        receiver = new SmsReceiver();
        registerReceiver(receiver,filter);
        phoneNum = findViewById(R.id.phoneNumber);
        messageCnt = findViewById(R.id.messageContent);
        sendBtn = findViewById(R.id.sendButton);
        ContentTv = findViewById(R.id.ContentTextView);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS)
                        !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},1);
                    try{
                        sendSMSS();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,"缺少权限",Toast.LENGTH_SHORT).show();
                    }

                }
                else
                    sendSMSS();
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECEIVE_SMS},2);

                }

                //sendSMSS();

//                if(!isOPen(MainActivity.this)){
//                    openGPS(MainActivity.this);
//                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public class SmsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder content = new StringBuilder();
            String sender = null;
            Bundle bundle = intent.getExtras();
            String format = intent.getStringExtra("format");
            if(bundle!=null){
                Object[] pdus = (Object[])bundle.get("pdus");
                for(Object object : pdus){
                    SmsMessage message = SmsMessage.createFromPdu((byte[])object,format);
                    sender = message.getOriginatingAddress();
                    content.append(message.getMessageBody());
                }
            }
            Toast.makeText(MainActivity.this,"到这一步了吗？",Toast.LENGTH_SHORT).show();
            //if(content.toString().contains("1908"))
            ContentTv.setText("来自："+ sender+"\n" + content.toString());
            //replySMSS(sender);
        }
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps && network) {
            return true;
        }
        return false;
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Toast.makeText(context,"未打开GPS，帮你自动打开",Toast.LENGTH_SHORT).show();
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void sendSMSS() {
        String content = messageCnt.getText().toString().trim();
        String phone = phoneNum.getText().toString().trim();
        if (!content.isEmpty()&&!phone.isEmpty()) {
            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> strings = manager.divideMessage(content);
            for (int i = 0; i < strings.size(); i++) {
                manager.sendTextMessage(phone, null, content, null, null);
            }
            Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "手机号或内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void replySMSS(String phoneNumber) {
        String content = "收到了谢谢！";
        String phone = phoneNumber;
        if (!content.isEmpty()&&!phone.isEmpty()) {
            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> strings = manager.divideMessage(content);
            for (int i = 0; i < strings.size(); i++) {
                manager.sendTextMessage(phone, null, content, null, null);
            }
            Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "手机号或内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    sendSMSS();
                }
                else {
                    Toast.makeText(this,"你取消了授权",Toast.LENGTH_SHORT).show();
                }
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写操作 如send（）； send函数中New SendMsg （号码，内容）；
                } else {
                    Toast.makeText(this, "你取消了授权", Toast.LENGTH_SHORT).show();
                }
                break;
                default:
        }
    }

    private  void toggleGPS(){
        Toast.makeText(this,  "Hi?", Toast.LENGTH_SHORT).show();
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings","com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try{
            PendingIntent.getBroadcast(this,0,gpsIntent,0).send();
        }
        catch (PendingIntent.CanceledException e){
            e.printStackTrace();
        }
    }

}
