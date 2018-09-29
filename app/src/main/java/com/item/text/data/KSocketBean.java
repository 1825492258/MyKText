package com.item.text.data;

import java.util.Date;

/**
 * Created by wuzongjie on 2018/9/29
 * 推送下拉的数据K线数据
 */
public class KSocketBean {

    private String symbol; // 币种
    private String period; // 1min
    private long time;
    private float closePrice;
    private float openPrice;
    private float highestPrice;
    private float lowestPrice;
    private float volume;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getTime() {
        return Utils.getFormatTime("MM-dd HH:mm", new Date(time));
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public float getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(float highestPrice) {
        this.highestPrice = highestPrice;
    }

    public float getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(float lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
