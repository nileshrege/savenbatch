package com.saven.dailyalert.batch;

import static com.saven.dailyalert.domain.Column.labelForIndex;
import static com.saven.dailyalert.domain.Column.nextLabelFor;

import com.saven.dailyalert.domain.ApplicableFormulas;
import com.saven.dailyalert.domain.Column;
import com.saven.dailyalert.domain.FormulaColumn;
import com.saven.dailyalert.domain.Row;

import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.Map;

public class FieldsSetMapper implements IndexedFieldSetMapper<Row> {

    boolean printRow;

    String rowIdColumn;

    Map<Integer, ApplicableFormulas> applicableFormulaMap;

    Map<String, Row> lastRowMap = new HashMap<>();

    @Override
    public Row map(FieldSet fieldSet, int index) throws BindException {

        Row row = new Row(rowIdColumn, index);

        for (int columnIndex = 0; columnIndex < fieldSet.getValues().length; columnIndex++) {
            String name = nextLabelFor(labelForIndex(columnIndex));
            String value = fieldSet.getValues()[columnIndex];
            row.add(new Column(name, value));

        }
        Column identityColumn = row.getColumn(row.getRowIdColumn()).get();
        if (!lastRowMap.containsKey(identityColumn.getValue())) {
            lastRowMap.put(identityColumn.getValue(), row);
        }

        ApplicableFormulas applicableFormulas = applicableFormulaMap.get(Integer.valueOf(identityColumn.getValue()));
        for (Map.Entry<String, String> entry : applicableFormulas.getColumns().entrySet()) {
            row.add(new FormulaColumn(entry.getKey(), entry.getValue(), row, lastRowMap.get(identityColumn.getValue())));
        }

        lastRowMap.put(identityColumn.getValue(), row);

        if (printRow) {
            StringBuilder builder = new StringBuilder();
            for (Column c : row.getColumns()) {
                builder.append(c.getName()).append(":").append(c.getValue()).append(" ");
            }
            System.out.println(builder);
        }

        return row;
    }

    public String getRowIdColumn() {
        return rowIdColumn;
    }

    public void setRowIdColumn(String rowIdColumn) {
        this.rowIdColumn = rowIdColumn;
    }

    public boolean isPrintRow() {
        return printRow;
    }

    public void setPrintRow(boolean printRow) {
        this.printRow = printRow;
    }

    public Map<Integer, ApplicableFormulas> getApplicableFormulaMap() {
        return applicableFormulaMap;
    }

    public void setApplicableFormulaMap(Map<Integer, ApplicableFormulas> applicableFormulaMap) {
        this.applicableFormulaMap = applicableFormulaMap;
    }
}

