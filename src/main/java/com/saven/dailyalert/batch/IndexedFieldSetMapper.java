package com.saven.dailyalert.batch;

import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public interface IndexedFieldSetMapper<T> {
    T map(FieldSet fieldSet, int index) throws BindException;
}
