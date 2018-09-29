package com.item.text.db;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.item.text.R;

import java.util.List;

public class ThreeActivity extends AppCompatActivity implements View.OnClickListener {

    public static void show(Activity activity){
        Intent intent = new Intent(activity,ThreeActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnFind).setOnClickListener(this);
        findViewById(R.id.btnChange).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                KParentBean bean1 = new KParentBean();
                bean1.setSymbol("BTC");
                bean1.setType(1);
                bean1.setResponse("1111111111");
                DataOpenHelper.getInstence().saveKBean(bean1);
                KParentBean bean2 = new KParentBean("BTC",2,"2222222222");
                DataOpenHelper.getInstence().saveKBean(bean2);
                KParentBean bean3 = new KParentBean("AAA",1,"3333333333");
                DataOpenHelper.getInstence().saveKBean(bean3);
                KParentBean bean4 = new KParentBean("BTC",4,"4444444444");
                DataOpenHelper.getInstence().saveKBean(bean4);
                break;
            case R.id.btnFind:
                KParentBean bean = DataOpenHelper.getInstence().findKBean("BTC",2);
                List<KParentBean> beans = DataOpenHelper.getInstence().findKBeanList("BTC");
                Log.d("jiejie","---" + bean.toString());
                for (KParentBean b : beans){
                    Log.d("jiejie","----------------" + b.toString());
                }
                break;
            case R.id.btnChange: // 改变
                DataOpenHelper.getInstence().updateKBean(new KParentBean("BTC",1,"11111111111111111111111"));
                DataOpenHelper.getInstence().updateKBean(new KParentBean("BTC",2,"22222222222222222222222"));
                break;
        }
    }
}
