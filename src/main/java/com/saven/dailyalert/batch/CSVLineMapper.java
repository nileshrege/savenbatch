package com.saven.dailyalert.batch;

import com.saven.dailyalert.domain.Row;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;

public class CSVLineMapper implements LineMapper<Row> {

    IndexedFieldSetMapper fieldSetMapper;

    LineTokenizer lineTokenizer;

    @Override
    public Row mapLine(String s, int i) throws Exception {
        FieldSet fieldSet = lineTokenizer.tokenize(s);
        return (Row) fieldSetMapper.map(fieldSet, i);
    }

    public IndexedFieldSetMapper getFieldSetMapper() {
        return fieldSetMapper;
    }

    public void setFieldSetMapper(IndexedFieldSetMapper fieldSetMapper) {
        this.fieldSetMapper = fieldSetMapper;
    }

    public LineTokenizer getLineTokenizer() {
        return lineTokenizer;
    }

    public void setLineTokenizer(LineTokenizer lineTokenizer) {
        this.lineTokenizer = lineTokenizer;
    }
}
