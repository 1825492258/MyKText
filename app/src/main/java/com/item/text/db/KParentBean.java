package com.item.text.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by wuzongjie on 2018/9/29
 * 1.在类名上面加入@Table标签，标签里面的属性name的值就是以后生成的数据库的表的名字
 * <p>
 * 2.实体bean里面的属性需要加上@Column标签，这样这个标签的name属性的值会对应数据库里面的表的字段。
 * <p>
 * 3.实体bean里面的普通属性，如果没有加上@Column标签就不会在生成表的时候在表里面加入字段。
 * <p>
 * 4.实体bean中必须有一个主键，如果没有主键，表以后不会创建成功，@Column(name=”id”,isId=true,autoGen=true)这个属性name的值代表的是表的主键的标识，isId这个属性代表的是该属性是不是表的主键，autoGen代表的是主键是否是自增长，如果不写autoGen这个属性，默认是自增长的属性。
 */
@Table(name = "KParentBean")
public class KParentBean {
    @Column(name = "id", isId = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 本来想建立2个主键但是发现存贮都有问题，所以建立一个主键
     */
    @Column(name = "symbol")
    private String symbol; // 表示币种
    @Column(name = "type")
    private int type; // 表示类型
    @Column(name = "response")
    private String response; // 对应的返回数据

    public KParentBean() {
    }

    public KParentBean(String symbol, int type, String response) {
        this.symbol = symbol;
        this.type = type;
        this.response = response;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "KParentBean{" +
                "symbol='" + symbol + '\'' +
                ", type=" + type +
                ", response='" + response + '\'' +
                '}';
    }
}
