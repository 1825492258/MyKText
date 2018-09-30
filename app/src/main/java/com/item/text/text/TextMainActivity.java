package com.item.text.text;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.fujianlian.klinechart.DataHelper;
import com.github.fujianlian.klinechart.KLineEntity;
import com.google.gson.Gson;
import com.item.text.R;
import com.item.text.data.KLineBean;
import com.item.text.data.KSocketBean;
import com.item.text.data.Utils;
import com.item.text.socket.ISocket;
import com.item.text.socket.SocketMessage;
import com.item.text.socket.SocketResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TextMainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, KlineContract.View {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, TextMainActivity.class);
        activity.startActivity(intent);
    }

    @BindView(R.id.ivBack)
    ImageButton ivBack;
    @BindView(R.id.tvMore)
    TextView tvMore;
    @BindView(R.id.tvIndex)
    TextView tvIndex;
    @BindView(R.id.llAllTab)
    LinearLayout llAllTab;
    @BindView(R.id.fl_content)
    FrameLayout flContent;
    @BindView(R.id.tv30M)
    TextView tv30M;
    @BindView(R.id.tvWeek)
    TextView tvWeek;
    @BindView(R.id.tvJan)
    TextView tvJan;
    @BindView(R.id.tabPop)
    LinearLayout tabPop;
    @BindView(R.id.tvMA)
    TextView tvMA;
    @BindView(R.id.tvBOLL)
    TextView tvBOLL;
    @BindView(R.id.tvMainHide)
    TextView tvMainHide;
    @BindView(R.id.tvMACD)
    TextView tvMACD;
    @BindView(R.id.tvKDJ)
    TextView tvKDJ;
    @BindView(R.id.tvRSI)
    TextView tvRSI;
    @BindView(R.id.tvChildHide)
    TextView tvChildHide;
    @BindView(R.id.llIndex)
    LinearLayout llIndex;
    @BindView(R.id.mRadioGroup)
    RadioGroup mRadioGroup;
    @BindView(R.id.llState)
    LinearLayout llState;
    @BindView(R.id.llLandText)
    LinearLayout llLandText;
    private List<Fragment> fragments = new ArrayList<>(); // 这个是K线的Fragment
    private ProgressDialog mDialog;

    private int childType = -1;
    private int mainType = 1;
    private String resolution = "";
    private KlineContract.Presenter mPresenter;

    private void show() {
        if (mDialog == null) mDialog = new ProgressDialog(this);
        mDialog.setMessage("数据加载中...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void hide() {
        if (mDialog != null && mDialog.isShowing()) mDialog.hide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new SocketMessage(0, ISocket.CMD.SUBSCRIBE_EXCHANGE_TRADE,
                Utils.buildGetBodyJson("BTC/USDT").toString().getBytes()));
        mPresenter = new KlinePresenterImpl(this);
        isVertical = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        fragments.add(KLineFragment.newInstance(true, 2)); // 这个是分时的
        fragments.add(KLineFragment.newInstance(2)); // 1分钟
        fragments.add(KLineFragment.newInstance(2)); // 5分钟
        fragments.add(KLineFragment.newInstance(2)); // 1小时
        fragments.add(KLineFragment.newInstance(2)); // 1天
        fragments.add(KLineFragment.newInstance(2)); // 1周或一个月
        //默认显示并加载1分钟的
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content, fragments.get(1)).commit();
        mRadioGroup.setOnCheckedChangeListener(this);
        tvMA.setSelected(true);
        tvChildHide.setSelected(true);
        // 默认加载1分钟的数据
        loadKineData(1);
        // 这里模拟推送，在1分钟时添加一条数据
    }

    private ArrayList<KLineEntity> kMoreEntity = new ArrayList<>(); // 推送来的数据

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketMessage(SocketResponse response){
        if(response.getCmd() == ISocket.CMD.PUSH_EXCHANGE_KLINE)   {
            KSocketBean kBean = new Gson().fromJson(response.getResponse(), KSocketBean.class);
            Log.d("jiejie", "K--" + response.getResponse());
            if (kBean == null) return;
            if ("1min".equals(kBean.getPeriod())) { // 表示推送下来的是 1min的 并且要判断推送来的信息和当前的币是不是一样
                KLineEntity lineEntity = new KLineEntity();
                lineEntity.Date = kBean.getTime();
                lineEntity.Open = kBean.getOpenPrice();
                lineEntity.Close = kBean.getClosePrice();
                lineEntity.High = kBean.getHighestPrice();
                lineEntity.Low = kBean.getLowestPrice();
                lineEntity.Volume = kBean.getVolume();
                kMoreEntity.add(lineEntity);
                DataHelper.calculate(kMoreEntity);
                KLineFragment fragment = (KLineFragment) fragments.get(1);
                //fragment.setKHeaderData(kMoreEntity,false); // 按理说应该用这个方法，但是会出现线划不来的感觉，所以采用下面的方法
                fragment.setKFooterData(kMoreEntity,false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private int index;
    private int currentTabIndex = 1;

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rb_fen: // 分时线
                currentSymType = 0;
                index = 0;
                break;
            case R.id.rb_oneM: // 1分钟
                currentSymType = 1;
                index = 1;
                break;
            case R.id.rb_fiveM: // 5分钟
                currentSymType = 2;
                index = 2;
                break;
            case R.id.rb_oneH: // 1小时
                currentSymType = 3;
                index = 3;
                break;
            case R.id.rb_oneD: // 1天
                currentSymType = 4;
                index = 4;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragments.get(currentTabIndex));
            if (!fragments.get(index).isAdded()) {
                transaction.add(R.id.fl_content, fragments.get(index));
                // 第一次展示，进行数据的加载（如果想每次切换都请求数据就放在下面吧）
                //  initData(index);
                loadKineData(currentSymType);
            }
            transaction.show(fragments.get(index)).commit();
        }
        currentTabIndex = index;
        // 隐藏更多和指示的布局
        tvMore.setText(R.string.more);
        tabPop.setVisibility(View.GONE);
        llIndex.setVisibility(View.GONE);
        tvMore.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_normal));
        tvIndex.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_normal));
    }

    private boolean isVertical;

    @OnClick({R.id.ivBack, R.id.ivFullScreen, R.id.tvMore, R.id.tvIndex, R.id.tv30M, R.id.tvWeek, R.id.tvJan, R.id.tvMA, R.id.tvBOLL, R.id.tvMainHide, R.id.tvMACD, R.id.tvKDJ, R.id.tvRSI, R.id.tvChildHide})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivBack: // 返回键
                finish();
                break;
            case R.id.ivFullScreen: // 切屏
                if (isVertical) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }
                break;
            case R.id.tvMore: // 点击更多
                if (tabPop.getVisibility() == View.VISIBLE) {
                    tabPop.setVisibility(View.GONE);
                    tvMore.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_normal));
                } else {
                    tvMore.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_hover));
                    tvIndex.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_normal));
                    tabPop.setVisibility(View.VISIBLE);
                    llIndex.setVisibility(View.GONE);
                }

                break;
            case R.id.tvIndex: // 点击指标
                if (llIndex.getVisibility() == View.VISIBLE) {
                    llIndex.setVisibility(View.GONE);
                    tvIndex.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_normal));
                } else {
                    tvMore.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_normal));
                    tvIndex.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_hover));
                    llIndex.setVisibility(View.VISIBLE);
                    tabPop.setVisibility(View.GONE);
                }

                break;
            case R.id.tv30M: // 30 分钟
            case R.id.tvWeek: // 1周
            case R.id.tvJan: // 1月
                mRadioGroup.clearCheck(); // 清除上次的点击的状态
                if (view.getId() == R.id.tv30M) {
                    currentSymType = 5;
                    tvMore.setText(R.string.ts_min);
                } else if (view.getId() == R.id.tvWeek) {
                    tvMore.setText(R.string.weekly);
                    currentSymType = 6;
                } else {
                    tvMore.setText(R.string.jan);
                    currentSymType = 7;
                }
                tabPop.setVisibility(View.GONE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(fragments.get(currentTabIndex));
                if (!fragments.get(5).isAdded()) {
                    transaction.add(R.id.fl_content, fragments.get(5));
                }
                transaction.show(fragments.get(5)).commit();
                currentTabIndex = 5; // 这里需要设置为5，不然无法隐藏
                loadKineData(currentSymType);
                break;
            case R.id.tvMA: // MA
            case R.id.tvBOLL: // BOLL
            case R.id.tvMainHide: // 隐藏
                tvMA.setSelected(false);
                tvBOLL.setSelected(false);
                tvMainHide.setSelected(false);
                if (view.getId() == R.id.tvMA) {
                    tvMA.setSelected(true);
                    mainType = 1;
                } else if (view.getId() == R.id.tvBOLL) {
                    tvBOLL.setSelected(true);
                    mainType = 2;
                } else {
                    mainType = 0;
                    tvMainHide.setSelected(true);
                }
                setMaChild(1, mainType);
                tvIndex.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_normal));
                llIndex.setVisibility(View.GONE);
                break;
            case R.id.tvMACD: // MACD
            case R.id.tvKDJ: // KDJ
            case R.id.tvRSI: // RSI
            case R.id.tvChildHide: // 隐藏
                tvMACD.setSelected(false);
                tvKDJ.setSelected(false);
                tvRSI.setSelected(false);
                tvChildHide.setSelected(false);
                if (view.getId() == R.id.tvMACD) {
                    tvMACD.setSelected(true);
                    childType = 0;
                } else if (view.getId() == R.id.tvKDJ) {
                    tvKDJ.setSelected(true);
                    childType = 1;
                } else if (view.getId() == R.id.tvRSI) {
                    tvRSI.setSelected(true);
                    childType = 2;
                } else {
                    childType = -1;
                    tvChildHide.setSelected(true);
                }
                setMaChild(2, childType);
                tvIndex.setBackground(getResources().getDrawable(R.drawable.shape_bg_kline_tab_normal));
                llIndex.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 设置主图 和 副图的一些设置
     */
    private void setMaChild(int type, int childType) {
        for (int i = 0; i < fragments.size(); i++) {
            if (fragments.get(i).isAdded()) {
                KLineFragment fragment = (KLineFragment) fragments.get(i);
                if (type == 1) { // 设置主图
                    fragment.setMainDrawType(childType);
                } else if (type == 2) { // 设置副图
                    fragment.setChildDrawType(childType);
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            isVertical = false;
            llState.setVisibility(View.GONE);
            llLandText.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.GONE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isVertical = true;
            llState.setVisibility(View.VISIBLE);
            llLandText.setVisibility(View.INVISIBLE);
            ivBack.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 返回键时
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isVertical) {
                finish();
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private int currentSymType = 1; // 当前所选币种

    private void loadKineData(int type) {
        Long to = System.currentTimeMillis();
        Long from = to;
        switch (type) {
            case 0: // 分时
//                Calendar c = Calendar.getInstance();
//                int hour = c.get(Calendar.HOUR_OF_DAY) - 6;
//                c.set(Calendar.HOUR_OF_DAY, hour);
//                String strDate = Utils.getFormatTime("HH:mm", c.getTime());
//                String str = Utils.getFormatTime(null, c.getTime());
//                from = Utils.getTimeMillis(null, str);
                from = to - 24L * 60 * 60 * 1000;
                resolution = 1 + "";
                break;
            case 1: // 1分钟
                from = to - 24L * 60 * 60 * 1000;
                resolution = 1 + "";
                break;
            case 2: // 5分钟
                from = to - 2 * 24L * 60 * 60 * 1000;//前两天数据
                resolution = 5 + "";
                break;
            case 3: // 1小时
                from = to - 24 * 24L * 60 * 60 * 1000;
                resolution = 1 + "H";
                break;
            case 4: // 1天
                from = to - 60 * 24L * 60 * 60 * 1000;
                resolution = 1 + "D";
                break;
            case 5: // 30分钟
                from = to - 12 * 24L * 60 * 60 * 1000;
                resolution = 30 + "";
                break;
            case 6: // 1周
                from = to - 730 * 24L * 60 * 60 * 1000;
                resolution = 1 + "W";
                break;
            case 7: // 1月
                from = to - 1095 * 24L * 60 * 60 * 1000;
                resolution = 1 + "M";
                break;
            default: // 默认
                break;
        }
        getKLineData("BTC/USDT", from, to, resolution);
    }

    /**
     * 获取网络数据
     *
     * @param symbol     币种
     * @param from       开始时间
     * @param to         结束时间
     * @param resolution 描述
     */
    private void getKLineData(String symbol, Long from, Long to, String resolution) {
        HashMap<String, String> map = new HashMap<>();
        map.put("symbol", symbol);
        map.put("from", String.valueOf(from));
        map.put("to", String.valueOf(to));
        map.put("resolution", resolution);
        mPresenter.KData(map); // 发起网络请求
       mPresenter.text(symbol,String.valueOf(from),String.valueOf(to),resolution);
    }

    /**
     * 获取的数据
     */
    @Override
    public void KDataSuccess(JSONArray obj) {
        final ArrayList<KLineEntity> data = getAll(obj);
        if (data != null)
            Log.d("jiejie", "da--" + data.size() + "---" + currentTabIndex);
        if(currentTabIndex == 1) kMoreEntity = data; // 这里新加一条数据有问题，所以尝试这么写的
        // 下面的设置一定需要在主线程中设置 否则会有问题
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        KLineFragment fragment = (KLineFragment) fragments.get(currentTabIndex); // 找到对应的Fragment，再在对应的Fragment上改变数据
                        fragment.setMainDrawType(mainType);
                        fragment.setChildDrawType(childType);
                        fragment.setKFooterData(data, true);
                    }
                });
            }
        }).start();

    }

    @Override
    public void showDialog() {
        show();
    }

    @Override
    public void hideDialog() {
        hide();
    }

    private ArrayList<KLineEntity> getAll(JSONArray obj) {
        ArrayList<KLineBean> kLineBeans = Utils.parseKLine(obj, currentSymType);
        if (kLineBeans != null && kLineBeans.size() > 0) {
            ArrayList<KLineEntity> datas = new ArrayList<>();
            for (int i = 0; i < kLineBeans.size(); i++) {
                KLineEntity lineEntity = new KLineEntity();
                KLineBean kLineBean = kLineBeans.get(i);
                lineEntity.Date = kLineBean.getDate();
                lineEntity.Open = kLineBean.getOpen();
                lineEntity.Close = kLineBean.getClose();
                lineEntity.High = kLineBean.getHigh();
                lineEntity.Low = kLineBean.getLow();
                lineEntity.Volume = kLineBean.getVol();
                datas.add(lineEntity);
            }
            DataHelper.calculate(datas);
            return datas;
        } else return null;
    }

    /**
     *
     */
    @Override
    public void dpPostFail(int code, String toastMessage) {
        Toast.makeText(this, "请求失败", Toast.LENGTH_SHORT).show();
    }
}
