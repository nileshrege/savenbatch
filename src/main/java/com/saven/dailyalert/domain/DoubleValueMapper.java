package com.saven.dailyalert.domain;

public class DoubleValueMapper implements ColumnValueMapper<String>{

    @Override
    public double map(String s) {
        return Double.valueOf(s);
    }
}
