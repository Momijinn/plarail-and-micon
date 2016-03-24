package com.example.kaname.purare_ru_control;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, Runnable {
    //準備段階のスイッチ
    private Switch ReadySwitch;

    //車両速度について
    private TextView SokudoTextView;
    private SeekBar SokudoSeekbar;

    //ProgressDialogの設定 & Bluetooth設定
    private ProgressDialog mProgressDialog;
    private Thread thread;
    private Setting_Bluetooth SB;


    //特別な操作(音声入力)
    private Button OnseiButton;
    static int INPUT_SPEECH = 10;
    private Button EmagenceButton;


    //効果音
    private MediaPlayer mp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //XMLと紐付け
        ReadySwitch = (Switch)findViewById(R.id.Ready_Switch);

        SokudoTextView = (TextView)findViewById(R.id.Sokudo_TextView);
        SokudoSeekbar = (SeekBar)findViewById(R.id.Sokudo_SeekBar);

        OnseiButton = (Button)findViewById(R.id.Onsei_Button);
        EmagenceButton = (Button)findViewById(R.id.Emagence_Button);

       //BluetoothClassの呼び出し
        SB = new Setting_Bluetooth(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Bluetooth有効化");
        mProgressDialog.setMessage("Loading now...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        thread = new Thread(this);
        thread.start();



        //それぞれのClassに投げる
        ReadySwitch.setOnCheckedChangeListener(this);
        SokudoSeekbar.setOnSeekBarChangeListener(this);
        OnseiButton.setOnClickListener(this);
        EmagenceButton.setOnClickListener(this);
        //DebugButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Activityが閉じられたらBluetoothソケットを閉じる
     */
    @Override
    protected void onDestroy() {
        Log.d("FinishTest", "onDestroy");
        super.onDestroy();
        //SB.Close_Bluetooth();
    }

    //Buttonが押された時に呼び出される
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Onsei_Button:
                //Toast.makeText(this, "音声ボタン", Toast.LENGTH_SHORT).show();
                if (ReadySwitch.isChecked() == true){
                    if (SokudoSeekbar.getProgress() <= 0){
                        Speech();
                    }else{
                        Toast.makeText(this, "すぐに出発できます", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "セーフティを解除してください!!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.Emagence_Button:
                Toast.makeText(this, "緊急停止", Toast.LENGTH_SHORT).show();
                SB.Send_Bluetooth(String.valueOf(0));

                SokudoTextView.setText("Emergency!!!");
                SokudoSeekbar.setProgress(0);
                ReadySwitch.setChecked(false);
                break;


            default:
                Toast.makeText(this, "error #1", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /**
     * 音声入力
     */
    private void Speech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,"音声認識実行中");
        startActivityForResult(intent, INPUT_SPEECH);
    }

    /**
     * 音声入力Class
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INPUT_SPEECH && resultCode == RESULT_OK){
            // 結果文字列リスト
            ArrayList<String> ret = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(this, ret.get(0), Toast.LENGTH_SHORT).show();

            if (ret.get(0).equals("出発進行")){
                SB.Send_Bluetooth(String.valueOf(90));
                SokudoSeekbar.setProgress(90);
                SokudoTextView.setText(90 + " km");
            }
        }
    }


    /**
     * Switchが押された時に呼び出される
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.Ready_Switch){
            if (ReadySwitch.isChecked() == true) {
                ReadySwitch.setChecked(true);
                ReadySwitch.setText("Go!");
                mp = MediaPlayer.create(this, R.raw.trainbel1); //出発音
                mp.start();
                //Toast.makeText(this, "操作可能！", Toast.LENGTH_SHORT).show();
            }else{
                ReadySwitch.setChecked(false);
                SB.Send_Bluetooth(String.valueOf(0));
                ReadySwitch.setText("Ready...");

                SokudoSeekbar.setProgress(0);
                SokudoTextView.setText(SokudoSeekbar.getProgress() + " km");

                mp = MediaPlayer.create(this, R.raw.trainsteam1);
                mp.start();
            }
        }

    }


    /**
     * Seekbarのツマミをドラッグしたときに呼び出される
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.Sokudo_SeekBar){

            if (ReadySwitch.isChecked() == true){
                SokudoTextView.setText(seekBar.getProgress() + " km");
            }else{
                seekBar.setProgress(0);
                SokudoTextView.setText(seekBar.getProgress() + " km");
            }
        }

    }


    /**
     * Seekbarのツマミを触れた時に呼び出される
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (ReadySwitch.isChecked() == false){
            Toast.makeText(this, "セーフティを解除してください！", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Seekbarのツマミを離した時に呼び出さる
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.Sokudo_SeekBar){

            if (ReadySwitch.isChecked() == true){
                SokudoTextView.setText(seekBar.getProgress() + " km");
                SB.Send_Bluetooth(String.valueOf(seekBar.getProgress()));

                if (seekBar.getProgress() == 0){
                    mp = MediaPlayer.create(this, R.raw.trainsteam1);
                    mp.start();
                }

            }else{
                seekBar.setProgress(0);
                SokudoTextView.setText(seekBar.getProgress() + " km");
                mp = MediaPlayer.create(this, R.raw.trainsteam1);
                mp.start();
            }
        }
    }


    /**
     * ProgressDialogによる非同期処理
     */
    @Override
    public void run() {
        //接続が確立するまで少し待つ
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SB.Initialize_Bluetooth();
        mProgressDialog.dismiss();

    }

}
