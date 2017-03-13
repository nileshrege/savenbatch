package com.saven.dailyalert.domain;

import java.io.Serializable;

public class Column implements Serializable {

    protected String name;

    protected String value;

    public Column() {}

    public Column(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static Column zeroValueColumn() {
        return new Column("","0");
    }

    public static String nextLabelFor(String current){
        double total = indexForLabel(current);
        int n = (int)total;
        return labelForIndex(n+1);
    }

    public static double indexForLabel(String current) {
        char []chr = current.toCharArray();
        double total=0;
        int pos = 0;
        for (int i=chr.length-1; i >= 0; i--){
            int num = chr[i] - 'A'+1;
            double val = Math.pow(26,pos)*num;
            total = val+total;
            pos++;
        }
        return total;
    }

    public static String labelForIndex(int n) {
        int str[] = new int[27];  // To store result (Excel column name)
        int i = 0;  // To store current index in str which is result
        while (n > 0) {
            // Find remainder
            int rem = n % 26;

            // If remainder is 0, then a 'Z' must be there in output
            if (rem == 0) {
                str[i++] = 'Z';
                n = (n / 26) - 1;
            }
            else // If remainder is non-zero
            {
                str[i++] = (rem - 1) + 'A';
                n = n / 26;
            }
        }
        str[i] = '\0';
        StringBuffer result = new StringBuffer();
        for (int j = 0; j < str.length; j++) {
            if (str[j] != 0) {
                result.append(String.valueOf((char) (str[j])));
            }
        }
        return result.reverse().toString();
    }
}
