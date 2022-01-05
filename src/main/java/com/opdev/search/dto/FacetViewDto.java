package com.opdev.search.dto;

import com.opdev.model.search.Facet;
import com.opdev.model.search.OperatorType;
import com.opdev.model.search.TableName;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class FacetViewDto {

    private Long id;
    private TableName type;
    private String code;
    private String value;
    private OperatorType operatorType;

    public FacetViewDto(Facet model) {
        id = model.getId();
        type = model.getTableName();
        code = model.getCode();
        value = model.getValue();
        operatorType = model.getOperatorType();
    }

}
