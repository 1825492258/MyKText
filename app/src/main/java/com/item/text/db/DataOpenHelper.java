package com.item.text.db;

import android.util.Log;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzongjie on 2018/9/29
 */
public class DataOpenHelper {

    private static DbManager db;

    private DataOpenHelper() {
        int VERSION = 1;
        String DB_NAME = "k_db";
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName(DB_NAME) // 设置数据库名称
                .setDbVersion(VERSION) // 设置数据的版本号
                .setAllowTransaction(true) // 设置是否允许开启事务
                // 设置数据库打开的监听
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        db.getDatabase().enableWriteAheadLogging();
                        //开启WAL支持多线程操作，提升性能, 对写入加速提升巨大(作者原话)
                    }
                })
                // 设置数据库更新的监听
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        //数据库升级操作
                    }
                })
                // 设置表创建的监听
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        Log.d("jiejie", "onTableCreate:" + table.getName());
                    }
                });
        db = x.getDb(daoConfig);
    }

//    public static DbManager getInstance() {
//        if (db == null) {
//            DataOpenHelper databaseUtils = new DataOpenHelper();
//        }
//        return db;
//    }
    private static DataOpenHelper mInstance = null;
    public static DataOpenHelper getInstence(){
        if(mInstance == null || db == null) {
            synchronized (DataOpenHelper.class){
                if(mInstance == null || db==null){
                    mInstance = new DataOpenHelper();
                }
            }
        }
        return mInstance;
    }
    /**
     * 插入数据
     */
    public void saveKBean(KParentBean bean) {
        try {
            db.save(bean);
            Log.d("jiejie","保存成功");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void updateKBean(KParentBean bean) {
        try {
            db.update(bean);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<KParentBean> findKBeanList(String symbol) {
        List<KParentBean> kParentBeans = new ArrayList<>();
        try {
            kParentBeans = db.selector(KParentBean.class).where("symbol", "=", symbol).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return kParentBeans;
    }

    public KParentBean findKBean(String symbol, int type) {
        try {
            KParentBean kParentBean = db.selector(KParentBean.class)
                    .where("symbol", "=", symbol)
                    .where("type", "=", type)
                    .findFirst();
            return kParentBean;
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }
}
