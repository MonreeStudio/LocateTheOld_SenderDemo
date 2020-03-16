package com.monree.senderdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.TypeConverter;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private TextView locationTv;
    private boolean isFirstLocate = true;
    private double lat;
    private double lon;
    BaiduMap baiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.GCJ02);
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.bmapView);
        locationTv = findViewById(R.id.locationTextView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        Intent intent = getIntent();
        String latitude = intent.getStringExtra("latitude_data");
        String longitude = intent.getStringExtra("longitude_data");
        lat = Double.valueOf(latitude).doubleValue();
        lon = Double.valueOf(longitude).doubleValue();
        CoordinateBean cdb = new PositionConvertUtil().wgs84ToGcj02(lat,lon);
        lat = cdb.getLatitude();
        lon = cdb.getLongitude();
        locationTv.setText(lat+"\n"+lon);
        locationTv.setText(SDKInitializer.getCoordType()+"");

        LatLng ll = new LatLng(lat,lon);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(16f);
        baiduMap.animateMapStatus(update);
        baiduMap.setMyLocationEnabled(true);
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(lat);
        locationBuilder.longitude(lon);

        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }




    private void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
//        locationBuilder.latitude(location.getLatitude());
//        locationBuilder.longitude(location.getLongitude());
        locationBuilder.latitude(lat);
        locationBuilder.longitude(lon);
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location.getLocType()==BDLocation.TypeGpsLocation||location.getLocType()==BDLocation.TypeNetWorkLocation)
                navigateTo(location);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
}
