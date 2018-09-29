package com.item.text.text;

import android.util.Log;

import com.github.fujianlian.klinechart.KLineChartView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by wuzongjie on 2018/9/27
 */
public class KlinePresenterImpl implements KlineContract.Presenter {

    private KlineContract.View view;
    private String url = "https://api.topbullex.one/market/history";
    private boolean isInitCache;
    KlinePresenterImpl(KlineContract.View view) {
        this.view = view;
        isInitCache = false;
    }

    /**
     * 网络请求
     */
    @Override
    public void KData(HashMap<String, String> map) {
        if(view!=null)view.showDialog();
        OkGo.<String>post(url)
                .cacheKey("text" + map.get("symbol") + map.get("resolution"))
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST) // 缓存模式先使用缓存然后使用网络数据
                .params(map).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Log.d("jiejie","----------网络数据");
                if (view == null) return;
                try {
                    view.KDataSuccess(new JSONArray(response.body()));
                } catch (JSONException e) {
                    view.dpPostFail(1, null);
                }
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                Log.d("jiejie","---缓存" +response.body());
                // 一般来说，只需要第一次初始化界面的时候需要使用缓存刷新界面，以后就不需要了

                    // 一般来说，缓存回调成功和网络回调成功的事情是一样的，所以
                    onSuccess(response);


            }

            @Override
            public void onFinish() {
                super.onFinish();
                if(view!=null) view.hideDialog();
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}
