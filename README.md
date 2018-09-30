## K线测试

## K线和分时线，需要用到KLineFragment，通过Fragment来控制K线的显示

     {
         mKChartView.setMainDrawLine(getArguments().getBoolean("isFen", false)); // 设置默认是否为分时图
         mKChartView.setBaseCoinScale(getArguments().getInt("scale", 2)); // 设置右侧的保留的小数位数
     }
    /**
     * 尾部添加数据
     *
     * @param data        数据
     * @param isAnimation 是否展示动画
     */
    public void setKFooterData(ArrayList<KLineEntity> data, boolean isAnimation) {
        if (mKChartView == null) return;
        if (data != null) {
            mKChartView.justShowLoading();
            DataHelper.calculate(data);
            mAdapter.addFooterData(data);
            if (isAnimation) mKChartView.startAnimation();
            mKChartView.refreshEnd();
        } else {
            mAdapter.clearData();
            mAdapter.notifyDataSetChanged();
            mKChartView.refreshEnd();
        }
    }
    /**
     * 设置主图是否为MA 和BOll
     */
    public void setMainDrawType(int type) {
        if (mKChartView == null) return;
        if (type == 1) {
            mKChartView.changeMainDrawType(Status.MA);
        } else if (type == 2) {
            mKChartView.changeMainDrawType(Status.BOLL);
        } else {
            mKChartView.changeMainDrawType(Status.NONE);
        }
    }
    /**
     * 设置副图的样式
     *
     * @param type macd--0  kdj--1  rsi--2  wr --3 隐藏---1
     */
    public void setChildDrawType(int type) {
        if (mKChartView == null) return;
        if (type == -1) {
            mKChartView.hideChildDraw();
        } else {
            mKChartView.setChildDraw(type);
        }
    }

### 在Activity获取数据后，直接对Fragment进行赋值
    /**
     * 获取的数据
     */
    @Override
    public void KDataSuccess(JSONArray obj) {
        final ArrayList<KLineEntity> data = getAll(obj);
        if (data != null){
            Log.d("jiejie", "da--" + data.size() + "---" + currentTabIndex);
            if(currentTabIndex == 1) kMoreEntity = data; // 这里新加一条数据有问题，所以尝试这么写的
        }
        // 下面的设置一定需要在主线程中设置 否则会有问题
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        KLineFragment fragment = (KLineFragment) fragments.get(currentTabIndex); // 找到对应的Fragment，再在对应的Fragment上改变数据
                        fragment.setMainDrawType(mainType); // 设置主图
                        fragment.setChildDrawType(childType); // 设置幅图
                        fragment.setKFooterData(data, true); // 添加数据
                    }
                });
            }
        }).start();
    }

### 缓存这里我用的是网络数据缓存，自己对应自己的框架修改吧
### 我用的是其他的框架，缓存需要设置先缓存获取数据然后再网络获取数据，缓存要根据币种和选择币种的时间来存取对应的缓存，当然也可以自己建数据库
