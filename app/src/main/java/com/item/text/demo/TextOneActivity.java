package com.item.text.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.fujianlian.klinechart.DataHelper;
import com.github.fujianlian.klinechart.KLineChartAdapter;
import com.github.fujianlian.klinechart.KLineChartView;
import com.github.fujianlian.klinechart.KLineEntity;
import com.github.fujianlian.klinechart.draw.Status;
import com.github.fujianlian.klinechart.formatter.DateFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.item.text.R;
import com.item.text.data.KSocketBean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 这里是写的基本的操作
 */
public class TextOneActivity extends AppCompatActivity {

    @BindView(R.id.maText)
    TextView maText;
    @BindView(R.id.bollText)
    TextView bollText;
    @BindView(R.id.mainHide)
    TextView mainHide;
    @BindView(R.id.macdText)
    TextView macdText;
    @BindView(R.id.kdjText)
    TextView kdjText;
    @BindView(R.id.rsiText)
    TextView rsiText;
    @BindView(R.id.wrText)
    TextView wrText;
    @BindView(R.id.subHide)
    TextView subHide;
    @BindView(R.id.kLineChartView)
    KLineChartView mKChartView;
    @BindView(R.id.fenText)
    Button fenText;
    @BindView(R.id.kText)
    Button kText;
    private KLineChartAdapter mAdapter;

    private static String text = " {\n" +
            "    \"Close\": \"153.350006\",\n" +
            "    \"Date\": \"2016/10/27\",\n" +
            "    \"High\": \"154.059998\",\n" +
            "    \"Low\": \"152.020004\",\n" +
            "    \"Open\": \"152.820007\",\n" +
            "    \"Volume\": \"4126800\"\n" +
            "  }";

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, TextOneActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_one);
        ButterKnife.bind(this);
        initView();
        initData();
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
setText();
            }
        });
    }
    private List<KLineEntity> kMoreList = new ArrayList<>();
    private void setText(){
        KLineEntity entity = new Gson().fromJson(text,KLineEntity.class);
        kMoreList.add(entity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       mAdapter.addFooterData(kMoreList);
                        mKChartView.refreshEnd();
                    }
                });
            }
        }).start();
    }
    public static ArrayList<KLineEntity> getAll() {
        ArrayList<KLineEntity> datas = new ArrayList<>();
        final KSocketBean kBean = new Gson().fromJson(text, KSocketBean.class);
        KLineEntity lineEntity = new KLineEntity();
        lineEntity.Date = kBean.getTime();
        lineEntity.Open = kBean.getOpenPrice();
        lineEntity.Close = kBean.getClosePrice();
        lineEntity.High = kBean.getHighestPrice();
        lineEntity.Low = kBean.getLowestPrice();
       // lineEntity.Volume = kBean.getVolume();
//        datas = new Gson().fromJson(getStringFromAssert(context, name),
//                new TypeToken<List<KLineEntity>>() {
//                }.getType());
        for (int i =0 ;i<600;i++){
            datas.add(lineEntity);
        }
       DataHelper.calculate(datas);
        return datas;
    }
    private void initView() {
        mAdapter = new KLineChartAdapter();
        mKChartView.setAdapter(mAdapter);
        mKChartView.setDateTimeFormatter(new DateFormatter());
        mKChartView.setGridRows(4);
        mKChartView.setGridColumns(4);
        maText.setSelected(true);
        kText.setSelected(true);
        subHide.setSelected(true);
        mKChartView.setBaseCoinScale(4);
    }

    private void initData() {
        mKChartView.justShowLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<KLineEntity> datas = getAll(TextOneActivity.this,"ibm.json");
              //  final ArrayList<KLineEntity> datas = getAll();
                kMoreList = datas;
                Log.d("jiejie","---" + datas.size()+ "---"+ datas.get(0).getOpenPrice() + " --" + datas.get(0).getRsi()+"  " + datas.get(0).getK());
                DataHelper.calculate(datas);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addFooterData(datas);
                        mAdapter.notifyDataSetChanged();
                        mKChartView.startAnimation();
                        mKChartView.refreshEnd();
                    }
                });
            }
        }).start();
    }

    public static ArrayList<KLineEntity> getAll(Context context,String name) {
        ArrayList<KLineEntity> datas = null;
        datas = new Gson().fromJson(getStringFromAssert(context, name),
                new TypeToken<List<KLineEntity>>() {
                }.getType());
       // DataHelper.calculate(datas);
        return datas;
    }

    public static String getStringFromAssert(Context context, String fileName) {
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            return new String(buffer, 0, buffer.length, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @OnClick({R.id.maText, R.id.bollText, R.id.mainHide, R.id.macdText, R.id.kdjText, R.id.rsiText, R.id.wrText, R.id.subHide, R.id.fenText, R.id.kText})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.maText:
            case R.id.bollText:
            case R.id.mainHide:
                maText.setSelected(false);
                bollText.setSelected(false);
                mainHide.setSelected(false);
                if (view.getId() == R.id.maText) {
                    maText.setSelected(true);
                    mKChartView.changeMainDrawType(Status.MA);
                } else if (view.getId() == R.id.bollText) {
                    bollText.setSelected(true);
                    mKChartView.changeMainDrawType(Status.BOLL);
                } else {
                    mKChartView.changeMainDrawType(Status.NONE);
                    mainHide.setSelected(true);
                }
                break;
            case R.id.macdText:
            case R.id.kdjText:
            case R.id.rsiText:
            case R.id.wrText:
            case R.id.subHide:
                macdText.setSelected(false);
                kdjText.setSelected(false);
                rsiText.setSelected(false);
                wrText.setSelected(false);
                subHide.setSelected(false);
                if (view.getId() == R.id.macdText) {
                    macdText.setSelected(true);
                    mKChartView.setChildDraw(0);
                } else if (view.getId() == R.id.kdjText) {
                    kdjText.setSelected(true);
                    mKChartView.setChildDraw(1);
                } else if (view.getId() == R.id.rsiText) {
                    rsiText.setSelected(true);
                    mKChartView.setChildDraw(2);
                } else if (view.getId() == R.id.wrText) {
                    wrText.setSelected(true);
                    mKChartView.setChildDraw(3);
                } else if (view.getId() == R.id.subHide) {
                    subHide.setSelected(true);
                    mKChartView.hideChildDraw();
                }
                break;
            case R.id.fenText:
                fenText.setSelected(true);
                kText.setSelected(false);
                mKChartView.setMainDrawLine(true);
                break;
            case R.id.kText:
                mKChartView.setMainDrawLine(false);
                fenText.setSelected(false);
                kText.setSelected(true);
                break;
        }
    }
}
