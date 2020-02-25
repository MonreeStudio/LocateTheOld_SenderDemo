package com.monree.senderdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.TypeConverter;
import android.content.Intent;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    BaiduMap baiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        Intent intent = getIntent();
        String latitude = intent.getStringExtra("latitude_data");
        String longitude = intent.getStringExtra("longitude_data");
        double lat = Double.valueOf(latitude).doubleValue();
        double lon = Double.valueOf(longitude).doubleValue();
        LatLng ll = new LatLng(lat,lon);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
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
    }
}
