package com.saven.dailyalert.domain;

import static com.saven.dailyalert.domain.Column.zeroValueColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Row {

    private int index;

    private String rowIdColumn;

    private List<Column> columns;

    public Row(String rowIdColumn, int index) {
        this.rowIdColumn = rowIdColumn;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getRowIdColumn() {
        return rowIdColumn;
    }

    public void setRowIdColumn(String rowIdColumn) {
        this.rowIdColumn = rowIdColumn;
    }

    public List<Column> getColumns() {
        if (columns == null) columns = new ArrayList<>();
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void add(Column column){
        this.getColumns().add(column);
    }

    public Optional<Column> getColumn(String name) {
        return getColumns().stream().filter(c -> c.getName().equals(name)).findFirst();
    }

    protected String rewrite(Pattern pattern, String expression) {
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String group = matcher.group();
            Optional<Column> column = getColumn(group.toUpperCase());
            String val = column.orElse(zeroValueColumn()).getValue();
            expression = expression.replace(group, val);
        }
        return expression;
    }

}
