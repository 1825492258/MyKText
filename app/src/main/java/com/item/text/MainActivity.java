package com.item.text;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.item.text.db.ThreeActivity;
import com.item.text.demo.TextOneActivity;
import com.item.text.text.TextMainActivity;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnOne).setOnClickListener(this);
        findViewById(R.id.btnTwo).setOnClickListener(this);
        findViewById(R.id.btnThree).setOnClickListener(this);
        NiceSpinner niceSpinner = findViewById(R.id.nice_spinner);
        List<String> dataSet = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
        niceSpinner.attachDataSource(dataSet);
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
                ThreeActivity.show(this);
                break;
        }
    }
}
