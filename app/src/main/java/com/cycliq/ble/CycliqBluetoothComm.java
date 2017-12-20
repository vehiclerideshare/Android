package com.cycliq.ble;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cycliq.MapsActivity;
import com.cycliq.QRScanActivity;
import com.cycliq.R;
import com.cycliq.TripRunningActivity;
import com.cycliq.utils.CRCUtil;
import com.cycliq.utils.CommandUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by mohaitheen on 02/12/17.
 */

public class CycliqBluetoothComm {


    public Boolean isOGBDevice = false;

    QRScanActivity currentActivity = null;
    MapsActivity mapsActivity = null;
    TripRunningActivity tripRunningActivity = null;

    private static  final String TAG="CycliqBluetoothComm";
    private Handler mHandler=new Handler();
    private boolean mScanning ;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBLEGatt;
    // Stops scanning after 10 seconds
    private static final long SCAN_PERIOD=10000; // 10s for scanning

    private BluetoothGattCharacteristic mBLEGCWrite;
    private BluetoothGattCharacteristic mBLEGCRead;
    private static final String ACTION_CONNECT_BLE="com.cycliq.ACTION_CONNECT_BLE";
    private static final String ACTION_DISCONNECT="com.cycliq.ACTION_DISCONNECT";
    private static final String ACTION_GET_LOCK_KEY="com.cycliq.ACTION_GET_LOCK_KEY";
    private static final String ACTION_LOCK_CLOSE="com.cycliq.ACTION_LOCK_CLOSE";
    private static final String ACTION_BLE_LOCK_OPEN_STATUS="com.cycliq.ACTION_BLE_LOCK_OPEN_STATUS";

    BLEDevice selectedBleDevice = null;

    BluetoothAdapter btAdapter;

    private static final CycliqBluetoothComm ourInstance = new CycliqBluetoothComm();

    public static CycliqBluetoothComm getInstance() {
        return ourInstance;
    }

    private CycliqBluetoothComm() {
    }

    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION_CONNECT_BLE.equals(intent.getAction())){

            }else if(ACTION_LOCK_CLOSE.equals(intent.getAction())){
                handler.sendEmptyMessage(HANDLER_CLOSE);
            }else if(ACTION_GET_LOCK_KEY.equals(intent.getAction())){
                handler.sendEmptyMessage(HANDLER_GETKEY);

            }else  if(ACTION_DISCONNECT.equals(intent.getAction())){

            }else if(ACTION_BLE_LOCK_OPEN_STATUS.equals(intent.getAction())){



                int status = intent.getIntExtra("status",0);
                long timestamp = intent.getLongExtra("timestamp",0L);
                Log.i(TAG, "onReceive: status="+status);
                Log.i(TAG, "onReceive: timestamp="+timestamp);
                handler.sendEmptyMessage(HANDLER_OPEN);

            }
        }
    };

    public static final int HANDLER_CLOSE=2;
    public static final int HANDLER_OPEN=3;
    public static final int HANDLER_GETKEY=1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_CLOSE:
                    sendLockResp();
                    break;
                case HANDLER_OPEN:
                    sendOpenResponse();
                    break;
                case HANDLER_GETKEY:

                    break;
            }
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if(newState== BluetoothProfile.STATE_CONNECTED){

                sendLocalBroadcast(ACTION_CONNECT_BLE);
                gatt.discoverServices();
            }
            else{
                Log.i(TAG, "onConnectionStateChange: ble disconnection");

                sendLocalBroadcast(ACTION_DISCONNECT);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());

            BluetoothGattService bleGattService= gatt.getService(GattAttributes.UUID_SERVICE);
            mBLEGCWrite = bleGattService.getCharacteristic(GattAttributes.UUID_CHARACTERISTIC_WRITE);

            //1.0783b03e-8535-b5a0-7140-a304d2495cb8
            //1. get read characteristic
            mBLEGCRead = bleGattService.getCharacteristic(GattAttributes.UUID_CHARACTERISTIC_READ);


            //2. descriptor 00002902-0000-1000-8000-00805f9b34fb
            //2. set descriptor notify
            BluetoothGattDescriptor descriptor=mBLEGCRead.getDescriptor(GattAttributes.UUID_NOTIFICATION_DESCRIPTOR);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            mBLEGatt.writeDescriptor(descriptor);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            //3. set characteristic notify
            gatt.setCharacteristicNotification(mBLEGCRead,true);

            Log.i(TAG, "onDescriptorWrite: request the ope key");
            int uid =1001; // user login id

            byte[] crcOrder= CommandUtil.getCRCKeyCommand2();
            Log.i(TAG, "onDescriptorWrite: GET KEY COMM="+getCommForHex(crcOrder));
            mBLEGCWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mBLEGCWrite.setValue(crcOrder);
            mBLEGatt.writeCharacteristic(mBLEGCWrite);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] values=characteristic.getValue();
            Log.i(TAG, "onCharacteristicChanged: values="+ getCommForHex(values));
            int start=0;
            int copyLen = 0;
            for(int i=0;i<values.length;i++){
                if((values[i]&0xFF)==0xFE){
                    start=i;
                    int randNum = (values[i+1]-0x32)&0xFF; //BF
                    int len =((values[i+4])&0xFF)^randNum;
                    copyLen= len+7; //16+
                    break;
                }
            }
            if(copyLen==0)  return ;
            byte[] real=new byte[copyLen];
            System.arraycopy(values,start,real,0,copyLen);


            byte[] command = new byte[values.length-2];
            command[0]= values[0]; // 包头
            // if(CRCUtil.CheckCRC(real)){
            // crc校验成功
            byte head = (byte) (real[1]-0x32);
            command[1]=head;
            for(int i=2;i<real.length-2;i++){
                command[i] = (byte) (real[i] ^ head);
            }
            handCommand(command);
//            }else{
//                // CRC校验失败
//                Log.i(TAG, "onCharacteristicChanged: CRC校验失败");
//            }
        }
    };

    private String getCommForHex(byte[] values){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0;i<values.length;i++){
            sb.append( String.format("%02X,",values[i]));
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }

    private BluetoothAdapter.LeScanCallback mLescanCallback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            final BLEDevice bleDevice = new BLEDevice(device,rssi);
            final String deviceName = device.getName();
            final String macAddress = device.getAddress();
            if (selectedBleDevice == null) {
                if (macAddress.toUpperCase().equalsIgnoreCase("FF:FF:11:00:02:2F")) {
                    isOGBDevice = false;
                    scanLeDevice(false);
                    selectedBleDevice = bleDevice;
                    bgOperation(1003);
                } else if (macAddress.toLowerCase().equalsIgnoreCase("80:ea:ca:01:00:17")) {
                    isOGBDevice = true;
                    scanLeDevice(false);
                    selectedBleDevice = bleDevice;
                    bgOperation(1003);
                }

            }



        }
    };

    private void handCommand(byte[] command){

        if (isOGBDevice) {
            Log.i(TAG, "handCommand: ord= "+String.format("0x%02X",command[3]));
            switch (command[3]){
                case 0x11:
                    // get key
                    handKey(command);
                    bgOperation(1001);
                    break;
                case 0x22:
                    // lock
                    handLockClose(command);
                    bgOperation(1004);

                    break;
                case 0x21:
                    handLockOpen(command);
                    bgOperation(1005);
                    break;
            }
        } else {
            Log.i(TAG, "handCommand: ord= "+String.format("0x%02X",command[7]));
            switch (command[7]){
                case 0x11:
                    // get key
                    handKey(command);
                    bgOperation(1001);
                    break;
                case 0x22:
                    // lock
                    handLockClose(command);
                    bgOperation(1004);

                    break;
                case 0x21:
                    handLockOpen(command);
                    bgOperation(1005);
                    break;
            }
        }


    }

    private  byte bleCKey =0;
    private void handKey(byte[] command){
        if (isOGBDevice) {
            bleCKey=command[2];

        } else {
            bleCKey=command[6];

        }
        Log.i(TAG, "handKey: key=0x"+String.format("%02X",bleCKey));
        sendLocalBroadcast(ACTION_GET_LOCK_KEY);
    }

    private void handLockClose(byte[] command){
        int status;
        if (isOGBDevice) {
            status = command[5];
        } else {
            status = command[7];
        }
        // long  timestamp= ((command[6]&0xFF)<<24) | ((command[7]&0xff)<<16) | ((command[8]&0xFF)<<8) | (command[9]&0xFF);
        // int  runTime= ((command[10]&0xFF)<<24) | ((command[11]&0xff)<<16) | ((command[12]&0xFF)<<8) | (command[13]&0xFF);


        Log.i(TAG, "handLockClose: status="+status);
//        Log.i(TAG, "handLockClose: timestamp="+timestamp);
//        Log.i(TAG, "handLockClose: runTime="+runTime);

        Intent intent = new Intent(ACTION_LOCK_CLOSE);
        //intent.putExtra("runTime",runTime);
        ///intent.putExtra("timestamp",timestamp);
        intent.putExtra("status",status);
        sendLocalBroadcast(intent);

    }

    private void handLockOpen(byte[] command){
        int status = command[5];
        Log.i(TAG, "handLockOpen: status="+status);

        long timestamp = ((command[6]&0xFF)<<24) | ((command[7]&0xff)<<16) | ((command[8]&0xFF)<<8) | (command[9]&0xFF);
        Intent intent = new Intent(ACTION_BLE_LOCK_OPEN_STATUS);
        intent.putExtra("status",status);
        intent.putExtra("timestamp",timestamp);
        sendLocalBroadcast(intent);


    }

    private void sendLockResp(){
        int uid = 1001;
        byte[] crcOrder= CommandUtil.getCRCLockCommand( bleCKey);
        Log.i(TAG, "sendOpenResponse: 上锁回复="+getCommForHex(crcOrder));
        mBLEGCWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBLEGCWrite.setValue(crcOrder);
        mBLEGatt.writeCharacteristic(mBLEGCWrite);

    }

    public void sendOpenResponse(){
        byte[] crcOrder= CommandUtil.getCRCOpenResCommand( bleCKey);
        Log.i(TAG, "sendOpenResponse: 开锁回复="+getCommForHex(crcOrder));
        mBLEGCWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mBLEGCWrite.setValue(crcOrder);
        mBLEGatt.writeCharacteristic(mBLEGCWrite);
    }

    private TextView txInfo;
    private Button btnScan ;
    private Button btnGetKey ;

    public void init() {

        initBLE();

        initReceiver();

        getPermission();
    }

    private void initBLE() {
        final BluetoothManager bluetoothManager= (BluetoothManager) currentActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter==null || !mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            currentActivity.startActivityForResult(enableBTIntent, 1);
        }
    }


    public void cleanupRecever() {
        LocalBroadcastManager.getInstance(currentActivity).unregisterReceiver(broadcastReceiver);
    }

    public void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CONNECT_BLE);
        intentFilter.addAction(ACTION_LOCK_CLOSE);
        intentFilter.addAction(ACTION_GET_LOCK_KEY);
        intentFilter.addAction(ACTION_DISCONNECT);
        intentFilter.addAction(ACTION_BLE_LOCK_OPEN_STATUS);

        LocalBroadcastManager.getInstance(currentActivity).registerReceiver(broadcastReceiver,intentFilter);
    }



    private void sendLocalBroadcast(String action){
        LocalBroadcastManager.getInstance(this.currentActivity).sendBroadcast(new Intent(action));
    }
    private void sendLocalBroadcast(Intent intent){
        LocalBroadcastManager.getInstance(this.currentActivity).sendBroadcast(intent);
    }

    private void  scanLeDevice(final boolean enable){

        if (!enable) {
            mScanning=false;
//            BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
//            scanner.stopScan(null);
             mBluetoothAdapter.stopLeScan(mLescanCallback);
            return;
        }

        int apiVersion = android.os.Build.VERSION.SDK_INT;
        if (apiVersion > android.os.Build.VERSION_CODES.KITKAT){
//            BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
//            // scan for devices
//            scanner.startScan(new ScanCallback() {
//
//                @Override
//                public void onScanResult(int callbackType, ScanResult result) {
//                    super.onScanResult(callbackType, result);
//
//                    BluetoothDevice device = result.getDevice();
//
//                    final BLEDevice bleDevice = new BLEDevice(device,result.getRssi());
//
//                    final String deviceName = device.getName();
////                    if(scanAdapter!=null && ("OmniBleLock".equals(deviceName)    )){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                scanAdapter.addBLEDevice(bleDevice);
//                                scanAdapter.notifyDataSetChanged();
//                            }
//                        });
//                   // }
//
//                }
//
//                @Override
//                public void onBatchScanResults(List<ScanResult> results) {
//                    super.onBatchScanResults(results);
//                    Log.e("failed","failed");
//
//                }
//
//                @Override
//                public void onScanFailed(int errorCode) {
//                    super.onScanFailed(errorCode);
//                    Log.e("failed","failed");
//                }
//
//
//            });
        } else {
            // targetting kitkat or bellow

        }

        mBluetoothAdapter.startLeScan(mLescanCallback);

//        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
//            @Override
//            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//                // get the discovered device as you wish
//
//            }
//        });

        mScanning=true;
//        mBluetoothAdapter.startDiscovery();
//        mBluetoothAdapter.startLeScan(mLescanCallback);


//        if(enable){
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
////                    mScanning=false;
////                    mBluetoothAdapter.stopLeScan(mLescanCallback);
////                    btnScan.setText(R.string.scan);
//                }
//            },SCAN_PERIOD);
//            mScanning=true;
//            mBluetoothAdapter.startDiscovery();
//            mBluetoothAdapter.startLeScan(mLescanCallback);
//            btnScan.setText(R.string.scanning);
//        }else{
//            mScanning=false;
//            mBluetoothAdapter.stopLeScan(mLescanCallback);
//            btnScan.setText(R.string.scan);
//        }
    }




    public void bgOperation(int type) {
        // send  unlock command
        switch (type){
            case 1001: // Unlock
                sendOpenCommand();
                break;
            case 1002: //Start Scann
                if(!mScanning) {
                    scanLeDevice(true);
                }
                break;
            case 1003: // Connect with Gatt
                if (selectedBleDevice != null) {
                    scanLeDevice(false);
                    mBLEGatt = selectedBleDevice.getDevice().connectGatt(currentActivity,false,mGattCallback);
                }

                break;
            case 1004: // DisConnect with Gatt
                if (selectedBleDevice != null) {
                    mScanning = false;
                    if (tripRunningActivity != null) {
                        tripRunningActivity.sendLockClosedStatus();
                    }
                    mBLEGatt.disconnect();
                    try {
                        Method m = selectedBleDevice.getClass().getMethod("removeBond", (Class[]) null);
                        m.invoke(selectedBleDevice, (Object[]) null);
                    } catch (Exception e) { Log.e(TAG, e.getMessage()); }
                }
                break;
            case 1005: // DisConnect with Gatt
                if (selectedBleDevice != null) {
                    if (currentActivity != null) {
                        currentActivity.sendLockOpenStatus();
                    }

                }
                break;
            case 1006: // Bypass with Gatt
//                if (selectedBleDevice != null) {
//                    if (currentActivity != null) {
                        currentActivity.sendLockOpenStatus();
//                    }
//
//                }
                break;
            default:
                break;

        }
    }



    private void sendOpenCommand(){
        if(mBLEGatt!=null) {
            int uid = 1002;
            long timestamp = System.currentTimeMillis()/1000;
            byte[] crcOrder = CommandUtil.getCRCOpenCommand(uid, bleCKey,timestamp);
            Log.i(TAG, "sendOpenCommand: openComm="+getCommForHex(crcOrder) );
            mBLEGCWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mBLEGCWrite.setValue(crcOrder);
            mBLEGatt.writeCharacteristic(mBLEGCWrite);
        }else{
//            Toast.makeText(this, currentActivity.getResources().getString(R.string.connect_tip), Toast.LENGTH_SHORT).show();
        }

    }



    private void getPermission(){


//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (btAdapter == null)
//        {
//            // Device does not support Bluetooth
//            Toast.makeText(currentActivity, "Device does not support bluetooth", Toast.LENGTH_LONG).show();
//        }
//        else {
//            if (!btAdapter.isEnabled()) {
//                btAdapter.enable();
//                Toast.makeText(currentActivity, "Bluetooth switched ON", Toast.LENGTH_LONG).show();
//
//
//            }
//        }

//        if(ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
//
//            // 检测定位权限
//            ActivityCompat.requestPermissions(currentActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
//        }
    }



    private void showDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setMessage(message);
        builder.setTitle(currentActivity.getResources().getString(R.string.main_dialog_tip));
        builder.setPositiveButton(currentActivity.getResources().getString(R.string.main_dialog_submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void setCurrentActivity(QRScanActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public QRScanActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setMapsActivity(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    public MapsActivity getMapsActivity() {
        return mapsActivity;
    }

    public TripRunningActivity getTripRunningActivity() {
        return tripRunningActivity;
    }

    public void setTripRunningActivity(TripRunningActivity tripRunningActivity) {
        this.tripRunningActivity = tripRunningActivity;
    }

    public Boolean getOGBDevice() {
        return isOGBDevice;
    }


}
