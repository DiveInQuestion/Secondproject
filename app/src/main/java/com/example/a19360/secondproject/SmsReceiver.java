package com.example.a19360.secondproject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 19360 on 2018/12/9.
 */

public class SmsReceiver extends BroadcastReceiver {
    private FriendsCollectionOperator friendsCollectionOperator = new FriendsCollectionOperator();
    private EnemiesCollectionOperator enemiesCollectionOperator = new EnemiesCollectionOperator();
    String regEx = "-?[0-9]{1,3}.[0-9]{1,6},-?[0-9]{1,3}.[0-9]{1,6}";//经纬度的正则表达式
    Pattern pattern = Pattern.compile(regEx);
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[])intent.getExtras().get("pdus");	//接收数据
        //Toast.makeText(getBaseContext(),"接收成功",Toast.LENGTH_SHORT).show();
        for(Object p: pdus) {
            byte[] pdu = (byte[]) p;
            SmsMessage message = SmsMessage.createFromPdu(pdu);    //根据获得的byte[]封装成SmsMessage
            String body = message.getMessageBody();                //发送内容
            String sender = message.getOriginatingAddress();    //短信发送方
            if (body.equals("where are you ?")) {
                body = getLantitude() + "," + getLongtitude();
                SmsManager manager = SmsManager.getDefault();
                ArrayList<String> list = manager.divideMessage(body);  //因为一条短信有字数限制，因此要将长短信拆分
                for (String text : list) {
                    manager.sendTextMessage(sender, null, text, null, null);
                }
            }
            else{
                Matcher matcher = pattern.matcher(body);
                if(matcher.matches()){
                    String latitude = body.substring(0, body.indexOf(","));
                    String longtitude = body.substring(body.indexOf(",") + 1, body.length());
                    for (int i = 0; i < getFriendsCollection().size(); i++) {
                        //注意，短信返回的号码会在前面增加“+86”
                        String Number = "+86"+getFriendsCollection().get(i).getPhoneNumber();
                        if(Number.equals(sender)) {
                            getFriendsCollection().get(i).setLatitude(latitude);
                            getFriendsCollection().get(i).setLongitude(longtitude);
                        }
                        friendsCollectionOperator.save(getBaseContext(),getFriendsCollection());
                    }
                    for (int i = 0; i <  getEnemiesCollection().size(); i++) {
                        //注意，短信返回的号码会在前面增加“+86”
                        String Number = "+86"+ getEnemiesCollection().get(i).getPhoneNumber();
                        if(Number.equals(sender)) {
                            getEnemiesCollection().get(i).setLatitude(latitude);
                            getEnemiesCollection().get(i).setLongitude(longtitude);
                        }
                        enemiesCollectionOperator.save(getBaseContext(), getEnemiesCollection());
                    }
                }
            }
        }
    }
    public Context getBaseContext(){return MainActivity.baseContext;}
    public double getLantitude() {
        return MainActivity.Latitude;
    }
    public double getLongtitude() {
        return MainActivity.Longtitude;
    }
    public ArrayList<Friends> getFriendsCollection(){
        return MainActivity.friendsCollection;
    }
    public ArrayList<Enemies> getEnemiesCollection() {return  MainActivity.enemiesCollection;}
}