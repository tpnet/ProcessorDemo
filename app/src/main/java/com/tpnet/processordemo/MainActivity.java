package com.tpnet.processordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.tpnet.apt.BindView;
import com.tpnet.apt.TPButterKnife;

public class MainActivity extends AppCompatActivity implements BaiduMap.OnMapClickListener {


    @BindView(R.id.mapView)
    MapView mMapView;


    private LocationClient mLocClient;//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
    private BaiduMap mBaiduMap;  //定义 BaiduMap 地图对象的操作方法与接口
    private MyLocationConfiguration.LocationMode mCurrentMode;  //定位图层显示方式，三种状态：1COMPASS罗盘状态 2FOLLOWING跟随状态，3NORMAL普通状态
    public MyLocationListenner myListener;   //定位请求回调接口
    private LatLng MyPosition;  //我当前位置的经纬度
    boolean isFirstLoc = true;// 是否首次定位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TPButterKnife.inject(this);
        init();

    }

    private void init() {

        mBaiduMap = mMapView.getMap();


        //带方向模式
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        //跳回当前位置监听事件
        //requestLocButton.setOnClickListener(new LocOnClickListener());
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //设置地图单击事件监听者
        mBaiduMap.setOnMapClickListener(this);


        // 定位初始化
        mLocClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();  //设置定位相关参数
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll");// 打开gps
        option.setIsNeedAddress(true);
        option.setScanSpan(10000);   //定位事件间隔10s

        myListener = new MyLocationListenner();
        //mLocClient.registerLocationListener(myListener);  //注册定位回调事件，
        mLocClient.registerLocationListener(myListener);  //注册定位回调事件，

        mLocClient.setLocOption(option);
        mLocClient.start();  //开始定位


    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }


    /**
     * 每10s定位请求回调接口
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {  //参数为定位结果

            // map view 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null) {
                return;
            }

            MyPosition = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());


            if (isFirstLoc) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        .direction(100).latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);

                isFirstLoc = false;

                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(MyPosition);
                mBaiduMap.animateMapStatus(u);

            }
        }
    }


    /**
     * 定位按钮点击  回到自己的位置
     */
    public class LocOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            //跳回当前的位置
            //mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));


            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        mLocClient.stop();
        mLocClient = null;


        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }
}
