package com.example.ss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.util.Log;

import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private boolean isListening = false;
    private TextView textView; // 声明TextView变量
    private SQLiteDatabase db;
    private HeartRateDatabaseHelper dbHelper;
    private ListView listView;
    private ArrayList<String> heartRateList;
    private ArrayAdapter<String> adapter;
    private double HeartRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取TextView实例
        textView = findViewById(R.id.textView3);
        if (!checkPer(Manifest.permission.BODY_SENSORS)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 100);
        }


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);


        dbHelper = new HeartRateDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        heartRateList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, heartRateList);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = db.query("heart_rate", null, null, null, null, null, null);
                if (cursor != null) {
                    heartRateList.clear();
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        double rate = cursor.getDouble(cursor.getColumnIndex("rate"));
                        heartRateList.add("ID: " + id + ", Rate: " + rate);
                    }
                    adapter.notifyDataSetChanged();
                    cursor.close();
                }
            }
        });

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // 清空数据库
                    db.execSQL("DELETE FROM heart_rate");
                    db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'heart_rate'");
                    if (!isListening) {
                        mSensorManager.registerListener(sensorEventListener, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
                        isListening = true;
                    } else {
                        mSensorManager.unregisterListener(sensorEventListener);
                        isListening = false;
                    }

                }
        });
    }

    private boolean checkPer(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        private static final String TAG = "心率";
        private static final String TAG1 = "测试变量";
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            Log.d(TAG, "sensor event: " + sensorEvent.accuracy + " = " + sensorEvent.values[0]);
            // 在这里获取心率并处理 sensorEvent.values[0];
            //tvHeartRate.setText("心率：" + heartRate);\
            //db.execSQL("INSERT INTO heart_rate (rate) VALUES (" + sensorEvent.values[0] + ")");
            textView.setVisibility(View.VISIBLE);
            textView.setText("心率：" + sensorEvent.values[0]);
            if(sensorEvent.values[0]!=0.0)
            {

                db.execSQL("INSERT INTO heart_rate (rate) VALUES (" + sensorEvent.values[0] + ")");
            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(sensorEventListener);
        db.close();
    }
}
