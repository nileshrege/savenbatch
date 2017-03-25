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

    public String getXColumn() {
        return xColumn;
    }

    public void setXColumn(String xColumn) {
        this.xColumn = xColumn;
    }

    public String getYColumn() {
        return yColumn;
    }

    public void setYColumn(String yColumn) {
        this.yColumn = yColumn;
    }

    public ColumnValueMapper getXColumnValueMapper() {
        return xColumnValueMapper;
    }

    public void setXColumnValueMapper(ColumnValueMapper xColumnValueMapper) {
        this.xColumnValueMapper = xColumnValueMapper;
    }

    public ColumnValueMapper getYColumnValueMapper() {
        return yColumnValueMapper;
    }

    public void setYColumnValueMapper(ColumnValueMapper yColumnValueMapper) {
        this.yColumnValueMapper = yColumnValueMapper;
    }
}
