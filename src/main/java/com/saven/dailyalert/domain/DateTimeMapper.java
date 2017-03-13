package com.saven.dailyalert.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeMapper implements ColumnValueMapper<String> {

    @Override
    public double map(String s) {
        Date date;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

        try {
            date = df.parse(s);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date: ", e);
        }

        return Double.valueOf(date.getMinutes());
    }
}
