package com.saven.dailyalert.domain;

public class XYSeriesConfig {

    String name;

    String xColumn;

    String yColumn;

    ColumnValueMapper xColumnValueMapper;

    ColumnValueMapper yColumnValueMapper;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getxColumn() {
        return xColumn;
    }

    public void setxColumn(String xColumn) {
        this.xColumn = xColumn;
    }

    public String getyColumn() {
        return yColumn;
    }

    public void setyColumn(String yColumn) {
        this.yColumn = yColumn;
    }

    public ColumnValueMapper getxColumnValueMapper() {
        return xColumnValueMapper;
    }

    public void setxColumnValueMapper(ColumnValueMapper xColumnValueMapper) {
        this.xColumnValueMapper = xColumnValueMapper;
    }

    public ColumnValueMapper getyColumnValueMapper() {
        return yColumnValueMapper;
    }

    public void setyColumnValueMapper(ColumnValueMapper yColumnValueMapper) {
        this.yColumnValueMapper = yColumnValueMapper;
    }
}
