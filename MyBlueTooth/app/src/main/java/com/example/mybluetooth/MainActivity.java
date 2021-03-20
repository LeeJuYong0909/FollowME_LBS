package com.example.mybluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private MinewBeaconManager mMinewBeaconManager;
    public BeaconAdapter mAdapter;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isScanning;
    UserRssi comp = new UserRssi();
    BeaconList BeaconList;
    private int state;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private void initListener() {
        if (mMinewBeaconManager != null) {
            BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
            switch (bluetoothState) {
                case BluetoothStateNotSupported:
                    Toast.makeText(MainActivity.this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                    case BluetoothStatePowerOff:
                        showBLEDialog();
                        return;
                        case BluetoothStatePowerOn:
                            break;
            }
        }
        if (isScanning) {
            isScanning = false;

            if (mMinewBeaconManager != null) {
                mMinewBeaconManager.stopScan();
            }
        } else {
            isScanning = true;

            try {
                mMinewBeaconManager.startScan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {
            /**
             *   if the manager find some new beacon, it will call back this method.
             *
             *  @param minewBeacons  new beacons the manager scanned
             */
            @Override
            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {

            }

            /**
             *  if a beacon didn't update data in 10 seconds, we think this beacon is out of rang, the manager will call back this method.
             *
             *  @param minewBeacons beacons out of range
             */
            @Override
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                /*for (MinewBeacon minewBeacon : minewBeacons) {
                    String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    Toast.makeText(getApplicationContext(), deviceName + "  out range", Toast.LENGTH_SHORT).show();
                }*/
            }

            /**
             *  the manager calls back this method every 1 seconds, you can get all scanned beacons.
             *
             *  @param minewBeacons all scanned beacons
             */

            @Override
            public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Collections.sort(minewBeacons, comp);
                        Log.e("tag", state + "");
                        if (state == 1 || state == 2) {
                        } else {
                            mAdapter.setItems(minewBeacons);
                        }

                    }
                });
            }

            /**
             *  the manager calls back this method when BluetoothStateChanged.
             *
             *  @param state BluetoothState
             */
            @Override
            public void onUpdateState(BluetoothState state) {
                switch (state) {
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
    }
    // 블루투스 연결확인 알고리즘
    private void checkBluetooth() {
        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                break;
        }
    }
    private void showBLEDialog() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                break;
        }
    }

    private void initView(BeaconList BeaconList, GoogleMap GoogleMap) {
        mAdapter = new BeaconAdapter(BeaconList, GoogleMap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

//        //실시간 위치 측위 알고리즘
//        // 1. 서버로 부터 비콘 목록 받아서 저장 ----
//        //  1) 비콘리스트, 어댑터 생성

        BeaconList  = new BeaconList();
        //  2) 서버로부터 받은 비콘 정보를 비콘 리스트에 추가
        // 1층 왼쪽
        BeaconList.add(new BeaconData("2","1", "15001", 35.896671, 128.620354));
        BeaconList.add(new BeaconData("2","1", "15002", 35.896698, 128.620431));
        BeaconList.add(new BeaconData("2","1", "15003", 35.896716, 128.620517));

        // 1~2 계단
        BeaconList.add(new BeaconData("2","2", "15016", 35.896662, 128.620551));

        // 2층 왼쪽
        BeaconList.add(new BeaconData("2","2", "15004", 35.896671, 128.620354));
        BeaconList.add(new BeaconData("2","2", "15005", 35.896698, 128.620431));
        BeaconList.add(new BeaconData("2","2", "15006", 35.896716, 128.620517));

        // 2층 오른쪽
        BeaconList.add(new BeaconData("2","2", "15008", 35.896738, 128.620571));
        BeaconList.add(new BeaconData("2","2", "15009", 35.896748, 128.620621));

        // 2층 방
        BeaconList.add(new BeaconData("3","2", "15010", 35.896786, 128.620605));
        BeaconList.add(new BeaconData("3","2", "15011", 35.896814, 128.620574));
        BeaconList.add(new BeaconData("3","2", "15012", 35.896825, 128.620606));

        // 1층 방
        BeaconList.add(new BeaconData("3","1", "15013", 35.896585, 128.620223));
        BeaconList.add(new BeaconData("3","1", "15014", 35.896605, 128.620289));
        BeaconList.add(new BeaconData("3","1", "15015", 35.896664, 128.620210));

        // 1층 복도 추가 비콘
        mMap = googleMap;
        LatLng SEOUL = new LatLng(35.896671, 128.620354);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(20));

        // 기존에 사용하던 다음 2줄은 문제가 있습니다.
        // CameraUpdateFactory.zoomTo가 오동작하네요.
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        // 2. 어뎁터 생성
        initView(BeaconList, mMap);
//        // 2. 블루투스 연결 확인
////        checkBluetooth();
//        // 3. 싱글턴 패턴
        initManager();
//        // 4. 비콘 신호 수신
        initListener();
    }
}