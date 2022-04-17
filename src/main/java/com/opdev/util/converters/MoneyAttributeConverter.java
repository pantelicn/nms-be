package com.opdev.util.converters;

import org.joda.money.Money;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MoneyAttributeConverter implements AttributeConverter<Money, String> {

    @Override
    public String convertToDatabaseColumn(Money money) {
        return money == null ? null : money.toString();
    }

    @Override
    public Money convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Money.parse(dbData);
    }
}
