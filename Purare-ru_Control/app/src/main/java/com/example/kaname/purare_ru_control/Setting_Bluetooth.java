package com.example.kaname.purare_ru_control;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Kaname on 2015/11/21.
 */
public class Setting_Bluetooth{
    private AppCompatActivity main;

    //BTの設定
    private BluetoothAdapter mBluetoothAdapter; //BTアダプタ
    private BluetoothDevice mBtDevice; //BTデバイス
    private BluetoothSocket mBtSocket; //BTソケット
    private OutputStream mOutput; //出力ストリーム

    public Setting_Bluetooth(AppCompatActivity activity) {
        main = activity;
    }

    /**
     * Bluetoothの初期化
     */
    public void Initialize_Bluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.getDefaultAdapter() == null){
            main.finish();
        }
        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }

        //Bluetoothへ接続
        mBtDevice = mBluetoothAdapter.getRemoteDevice(main.getResources().getString(R.string.blutooth_ip));

        //接続が確立するまで少し待つ
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //OutputStreamの設定
        try {
            mBtSocket = mBtDevice.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            mBtSocket.connect();
            mOutput = mBtSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Bluetoothの送信用
     * @param text 速度
     */
    public void Send_Bluetooth(String text){
        text = text + "/";
        //Toast.makeText(main,text, Toast.LENGTH_SHORT).show();


        try {
            mOutput.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Bluetoothのソケットを閉じる
     * うまくいかない・・・
     */
    public void Close_Bluetooth(){
        try {
            mOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
