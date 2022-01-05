package com.opdev.model.search;

public enum TableName {

    POSITION("position"), SKILL("skill"), TERM("term");

    private final String value;

    TableName(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
