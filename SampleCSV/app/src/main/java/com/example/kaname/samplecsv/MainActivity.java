package com.example.kaname.samplecsv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] csv; //1行分のデータ格納用

        try {
            InputStream is = getAssets().open("area_days1.csv");
            InputStreamReader ireder = new InputStreamReader(is, "UTF-8");
            CSVReader reader = new CSVReader(ireder,',','"',0);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
