package com.cycliq.ble;

import android.bluetooth.BluetoothDevice;

/**
 * <br />
 * created by CxiaoX at 2017/1/10 19:33.
 */

public class BLEDevice {
    //蓝牙设备
    private BluetoothDevice device;
    //信号
    private int rssi;

    public BLEDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BLEDevice bleDevice = (BLEDevice) o;

        return device != null ? device.equals(bleDevice.device) : bleDevice.device == null;

    }

    @Override
    public int hashCode() {
        return device != null ? device.hashCode() : 0;
    }
}
