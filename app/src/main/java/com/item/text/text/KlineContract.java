package com.item.text.text;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * Created by wuzongjie on 2018/9/27
 */
public interface KlineContract {

    interface View {

        void showDialog();

        void hideDialog();

        void KDataSuccess(JSONArray obj);

        void dpPostFail(int code, String toastMessage);
    }

    interface Presenter {

        void KData(HashMap<String, String> map);

        void onDestroy();
    }
}
