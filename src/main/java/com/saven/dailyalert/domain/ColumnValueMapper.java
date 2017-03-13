package com.saven.dailyalert.domain;

public interface ColumnValueMapper<T> {
    public double map(T i);
}
