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