package com.example.a19360.secondproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static Context baseContext;
    public static double Latitude ;
    public static double Longtitude ;
    public static ArrayList<Friends> friendsCollection;
    public static ArrayList<Enemies> enemiesCollection;
    //public static MainActivity mainActivity;
    MapView mMapView = null;

    private MediaPlayer mediaPlayer;
    private ToggleButton toggleButton;
    private Button btn_locate, btn_friends, btn_enemies;
    private ImageView refresh;
    private int flag = 1;

    private boolean isFirstLocation = true;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private BaiduMap mBaiduMap;

    private double lat;
    private double lon;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private FriendsCollectionOperator friendsCollectionOperator;

    private EnemiesCollectionOperator enemiesCollectionOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.main);
        mediaPlayer = new MediaPlayer();
        mMapView = (MapView) findViewById(R.id.mapView);
        toggleButton = (ToggleButton) findViewById(R.id.btn_refresh);
        btn_locate = (Button) findViewById(R.id.btn_locate);
        btn_friends = (Button) findViewById(R.id.btn_friends);
        btn_enemies = (Button) findViewById(R.id.btn_enemies);
        refresh = (ImageView) findViewById(R.id.imageview_sweep);
        //修改百度地图的初始位置
        friendsCollectionOperator = new FriendsCollectionOperator();
        friendsCollection=friendsCollectionOperator.load(getBaseContext());
        if(friendsCollection==null){
            friendsCollection = new ArrayList<Friends>();
        }
        enemiesCollectionOperator = new EnemiesCollectionOperator();
        enemiesCollection=enemiesCollectionOperator.load(getBaseContext());
        if(enemiesCollection==null){
            enemiesCollection = new ArrayList<Enemies>();
        }

        mBaiduMap = mMapView.getMap();

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        /*
        * 初始化界面定位在暨南大学珠海校区
        * */
        LatLng latLng = new LatLng(22.2559, 113.541112);//设定中心点坐标

        MapStatus mMapStatus = new MapStatus.Builder()//定义地图状态
                .target(latLng).zoom(15).build();//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);//改变地图状态

        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.clear();
                //配置定位参数
                initLocation();
                //开始定位
                mLocationClient.start();
                setUserMapCenter();
                setMarker();
                friendsCollectionOperator.save(getBaseContext(),friendsCollection);
                enemiesCollectionOperator.save(getBaseContext(),enemiesCollection);
            }
        });

        toggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 1) {
                    refresh.setVisibility(View.VISIBLE);
                    Animation rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_anim);
                    LinearInterpolator lin = new LinearInterpolator();
                    rotate.setInterpolator(lin);
                    if (rotate != null) {
                        refresh.startAnimation(rotate);
                    } else {
                        refresh.setAnimation(rotate);
                        refresh.startAnimation(rotate);
                    }
                    String path = "http://gddx.sc.chinaz.com/Files/DownLoad/sound1/201501/5354.mp3";
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(path);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(friendsCollection!=null&&friendsCollection.size()!=0) {
                        for (int i = 0; i < friendsCollection.size(); i++) {
                            if(friendsCollection.get(i).getPhoneNumber().length()==11){
                                String phone = friendsCollection.get(i).getPhoneNumber().toString();
                                String context = "where are you ?";
                                SmsManager manager = SmsManager.getDefault();
                                ArrayList<String> list = manager.divideMessage(context);  //因为一条短信有字数限制，因此要将长短信拆分
                                for (String text : list) {
                                    manager.sendTextMessage(phone, null, text, null, null);
                                }
                            }
                            Toast.makeText(getApplication(),"发送失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(enemiesCollection!=null&&enemiesCollection.size()!=0) {
                        for (int i = 0; i < enemiesCollection.size(); i++) {
                            if(enemiesCollection.get(i).getPhoneNumber().length()==11){
                                String phone = enemiesCollection.get(i).getPhoneNumber().toString();
                                String context = "where are you ?";
                                SmsManager manager = SmsManager.getDefault();
                                ArrayList<String> list = manager.divideMessage(context);  //因为一条短信有字数限制，因此要将长短信拆分
                                for (String text : list) {
                                    manager.sendTextMessage(phone, null, text, null, null);
                                }
                            }
                            Toast.makeText(getApplication(),"发送失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                    //Toast.makeText(getApplicationContext(), "发送完毕", Toast.LENGTH_SHORT).show();
                    flag = 0;
                } else if (flag == 0) {
                    refresh.clearAnimation();
                    refresh.setVisibility(View.INVISIBLE);
                    flag = 1;
                }
            }
        });
        btn_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, FriendsActivity.class);
               // Toast.makeText(getApplicationContext(), "Friends", Toast.LENGTH_SHORT).show();
                //startActivityForResult(intent, 1);
                startActivity(intent);
            }
        });
        btn_enemies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, EnemiesActivity.class);
                //Toast.makeText(getApplicationContext(), "Enemies", Toast.LENGTH_SHORT).show();
                //startActivityForResult(intent, 2);
                startActivity(intent);
            }
        });
    }


    private void setMarker() {
        Log.v("pcw", "setMarker : lat : " + lat + " lon : " + lon);
        //定义Maker坐标点
        LatLng point = new LatLng(lat, lon);
        Latitude = lat;
        Longtitude = lon;
        //Toast.makeText(MainActivity.this, ""+friendsCollection.get(0).getLongitude(), Toast.LENGTH_SHORT).show();
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.mymark);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

        if(friendsCollection!=null&&friendsCollection.size()!=0&&Latitude!=0&&Longtitude!=0) {
            for (int i = 0; i < friendsCollection.size(); i++) {
                if (friendsCollection.get(i).getLatitude() != null && friendsCollection.get(i).getLongitude() != null) {
                    double Lat = Double.parseDouble(friendsCollection.get(i).getLatitude());
                    double Lon = Double.parseDouble(friendsCollection.get(i).getLongitude());
                    LatLng latLng = new LatLng(Lat, Lon);
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.friend_marker);
                    option = new MarkerOptions()
                            .position(latLng)
                            .icon(bitmap);
                    //在地图上添加Marker，并显示
                    final Marker marker = (Marker) mBaiduMap.addOverlay(option);
                    //Toast.makeText(baseContext, friendsCollection.get(i).getName(), Toast.LENGTH_SHORT).show();
                    OverlayOptions nameOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(50)
                            .fontColor(0xFF02FF3D).text(friendsCollection.get(i).getName()+":"
                                    + friendsCollection.get(i).getPhoneNumber()).rotate(0).position(latLng);
                    mBaiduMap.addOverlay(nameOption);

                    List<LatLng> points = new ArrayList<LatLng>();
                    points.add(new LatLng(Latitude, Longtitude));
                    points.add(new LatLng(Lat, Lon));

                    //构建分段颜色索引数组
                    LatLng start = new LatLng(Latitude, Longtitude);
                    LatLng end = new LatLng(Lat, Lon);
                    String distance = getDistance(start,end);

                    List<Integer> colors = new ArrayList<>();
                    colors.add(Integer.valueOf(Color.GREEN));
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
                            .colorsValues(colors).points(points);
                    Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                    LatLng latLng2 = new LatLng((Lat+Latitude)/2, (Lon+Longtitude)/2);
                    OverlayOptions Distance = new TextOptions().bgColor(0xAAFFFF00).fontSize(50)
                            .fontColor(0xFF02FF3D).text(distance).rotate(0).position(latLng2);
                    mBaiduMap.addOverlay(Distance);
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("id", i);
                    marker.setExtraInfo(mBundle);
                    BaiduMap.OnMarkerClickListener onMarkerClickListener = new BaiduMap.OnMarkerClickListener(){

                        @Override
                        public boolean onMarkerClick(Marker arg0) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent();
                            Bundle bundle = marker.getExtraInfo();
                            int id = bundle.getInt("id");
                            intent.setClass(MainActivity.this,FriendsDetailActivity.class);
                            intent.putExtra("name",""+friendsCollection.get(id).getName());
                            intent.putExtra("phoneNumber",""+friendsCollection.get(id).getPhoneNumber());
                            intent.putExtra("lantitude",""+friendsCollection.get(id).getLatitude());
                            intent.putExtra("longtitude",""+friendsCollection.get(id).getLongitude());
                            intent.putExtra("position",""+id);
                            intent.putExtra("activity","1");
                            startActivityForResult(intent,1);
                            return false;
                        }
                    };
                }
            }
        }
        if(enemiesCollection!=null&&enemiesCollection.size()!=0&&Latitude!=0&&Longtitude!=0) {
            for (int i = 0; i < enemiesCollection.size(); i++) {
                if (enemiesCollection.get(i).getLatitude() != null && enemiesCollection.get(i).getLongitude() != null) {
                    double Lat = Double.parseDouble(enemiesCollection.get(i).getLatitude());
                    double Lon = Double.parseDouble(enemiesCollection.get(i).getLongitude());
                    LatLng latLng = new LatLng(Lat, Lon);
                    bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.enemy_marker);
                    option = new MarkerOptions()
                            .position(latLng)
                            .icon(bitmap);
                    //在地图上添加Marker，并显示
                    final Marker marker = (Marker)mBaiduMap.addOverlay(option);
                    //Toast.makeText(baseContext, friendsCollection.get(i).getName(), Toast.LENGTH_SHORT).show();
                    OverlayOptions nameOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(50)
                            .fontColor(0xFFFF020A).text(enemiesCollection.get(i).getName()+":"
                                    + enemiesCollection.get(i).getPhoneNumber()).rotate(0).position(latLng);
                    mBaiduMap.addOverlay(nameOption);

                    /*OverlayOptions phoneOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(50)
                            .fontColor(0xFFFEFDFD).text(friendsCollection.get(i).getPhoneNumber()).rotate(0).position(latLng);
                    mBaiduMap.addOverlay(phoneOption);*/

                    List<LatLng> points = new ArrayList<LatLng>();
                    points.add(new LatLng(Latitude, Longtitude));
                    points.add(new LatLng(Lat, Lon));

                    LatLng start = new LatLng(Latitude, Longtitude);
                    LatLng end = new LatLng(Lat, Lon);
                    String distance = getDistance(start,end);
                    //构建分段颜色索引数组

                    List<Integer> colors = new ArrayList<>();
                    colors.add(Integer.valueOf(Color.RED));
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
                            .colorsValues(colors).points(points);
                    Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                    LatLng latLng2 = new LatLng((Lat+Latitude)/2, (Lon+Longtitude)/2);
                    OverlayOptions Distance = new TextOptions().bgColor(0xAAFFFF00).fontSize(50)
                            .fontColor(0xFFFF020A).text(distance).rotate(0).position(latLng2);
                    mBaiduMap.addOverlay(Distance);
                   /* Bundle mBundle = new Bundle();
                    mBundle.putInt("id", i);
                    marker.setExtraInfo(mBundle);
                    mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker arg0) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent();
                            Bundle bundle = marker.getExtraInfo();
                            int id = bundle.getInt("id");
                            intent.setClass(MainActivity.this,EnemiesDetailActivity.class);
                            intent.putExtra("name",""+enemiesCollection.get(id).getName());
                            intent.putExtra("phoneNumber",""+enemiesCollection.get(id).getPhoneNumber());
                            intent.putExtra("lantitude",""+enemiesCollection.get(id).getLatitude());
                            intent.putExtra("longtitude",""+enemiesCollection.get(id).getLongitude());
                            intent.putExtra("position",""+id);
                            intent.putExtra("activity","1");
                            startActivityForResult(intent,2);
                            return false;
                        }
                    });*/
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Toast.makeText(getApplicationContext(), ""+temp, Toast.LENGTH_SHORT).show();
        if (requestCode==1)
        {
            if (resultCode==FriendsDetailActivity.RESULT_OK)
            {
                mBaiduMap.clear();
                //配置定位参数
                initLocation();
                //开始定位
                mLocationClient.start();
                setUserMapCenter();
                setMarker();
            }
        }
        if (requestCode==2)
        {
            if (resultCode==FriendsDetailActivity.RESULT_OK)
            {
                mBaiduMap.clear();
                //配置定位参数
                initLocation();
                //开始定位
                mLocationClient.start();
                setUserMapCenter();
                setMarker();
            }
        }
    }
    /*
    * 计算两点间的距离
    * */
    public String getDistance(LatLng start,LatLng end){
        double lat1 = (Math.PI/180)*start.latitude;
        double lat2 = (Math.PI/180)*end.latitude;

        double lon1 = (Math.PI/180)*start.longitude;
        double lon2 = (Math.PI/180)*end.longitude;

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;
        //Toast.makeText(getApplicationContext(),""+d,Toast.LENGTH_SHORT).show();
        if(d<1)
            return String.format("%.2f",d*1000)+"m";
        else
            return String.format("%.2f",d)+"km";
    }
    /**
     * 设置中心点
     */
    private void setUserMapCenter() {
        Log.v("pcw", "setUserMapCenter : lat : " + lat + " lon : " + lon);
        LatLng cenpt = new LatLng(lat, lon);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

    }

    /**
     * 配置定位参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    /**
     * 实现定位监听 位置一旦有所改变就会调用这个方法
     * 可以在这个方法里面获取到定位之后获取到的一系列数据
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());

            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            lat = location.getLatitude();
            lon = location.getLongitude();

            //这个判断是为了防止每次定位都重新设置中心点和marker
            if (isFirstLocation) {
                isFirstLocation = false;
                setMarker();
                setUserMapCenter();
            }
            Log.v("pcw", "lat : " + lat + " lon : " + lon);
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    public void MDestroy() {
        if (mMapView != null) mMapView.onDestroy();
    }

    public void MResume() {
        if (mMapView != null) mMapView.onResume();
    }

    public void MPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        if (mMapView != null) mMapView.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
}
