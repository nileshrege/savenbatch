package com.saven.dailyalert.domain;

import java.util.HashMap;
import java.util.Map;

public class ApplicableFormulas {

    private String rowIdColumnValue;

    private Map<String, String> columns;

    public Map<String, String> getColumns() {
        if (columns == null) {
            columns = new HashMap<>();
        }
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    public String getRowIdColumnValue() {
        return rowIdColumnValue;
    }

    public void setRowIdColumnValue(String rowIdColumnValue) {
        this.rowIdColumnValue = rowIdColumnValue;
    }
}
