package com.item.text;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.item.text.demo.TextOneActivity;
import com.item.text.socket.MarketService;
import com.item.text.text.TextMainActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, MarketService.class));
        findViewById(R.id.btnOne).setOnClickListener(this);
        findViewById(R.id.btnTwo).setOnClickListener(this);
        findViewById(R.id.btnThree).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,MarketService.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnOne: // 最简单的使用
                TextOneActivity.show(this);
                break;
            case R.id.btnTwo:
                TextMainActivity.show(this);
                break;
            case R.id.btnThree:
               // ThreeActivity.show(this);
                break;
        }
    }
}
