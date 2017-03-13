package com.saven.dailyalert.domain;

import static java.util.regex.Pattern.compile;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class FormulaColumn extends Column{

    public FormulaColumn(String name, String formula, Row row, Row lastRow) {
        String expression = row.rewrite(compile("[A-Z]+"), formula);
        expression = lastRow.rewrite(compile("[a-z]+"), expression);

        String derivedValue = null;
        try {
            derivedValue = eval(expression).toString();
        }
        catch (ScriptException e) {
            throw new IllegalArgumentException(expression);
        }
        super.setName(name);
        super.setValue(derivedValue);
    }

    private Object eval(String expression) throws ScriptException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        return engine.eval(expression);
    }
}
