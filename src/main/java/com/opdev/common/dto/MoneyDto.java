package com.opdev.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.joda.money.Money;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true)
public class MoneyDto {

    @NotBlank
    @NonNull
    private String currencyCode;

    @NotBlank
    @NonNull
    private BigDecimal amount;

    public MoneyDto(Money money) {
        this.currencyCode = money.getCurrencyUnit().toString();
        this.amount = money.getAmount();
    }

    public Money asMoney() {
        return Money
                .parse(String.format("%s %s", currencyCode, amount));
    }
}
