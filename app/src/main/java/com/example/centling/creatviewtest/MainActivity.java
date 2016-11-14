package com.example.centling.creatviewtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyCustomView lv_loading;
    private int progress;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    progress += 5;
                    if (progress <= 100) {
                        lv_loading.setProgress(progress);
                        myHandler.sendEmptyMessageDelayed(1, 200);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_loading = (MyCustomView) findViewById(R.id.clv_loading);
        lv_loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "测试啊", Toast.LENGTH_SHORT).show();
            }
        });
        View btn = findViewById(R.id.test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = 0;
                myHandler.removeCallbacksAndMessages(null);
                myHandler.sendEmptyMessage(1);
            }
        });

     //   myHandler.sendEmptyMessage(1);
        lv_loading.setOnProgressListener(new MyCustomView.OnProgressListener() {
            @Override
            public String OnProgress(int max, int progress) {
                return (progress * 100f / max) + "%";
            }
        });
    }

}
