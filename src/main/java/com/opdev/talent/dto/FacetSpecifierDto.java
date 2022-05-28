package com.opdev.talent.dto;

import com.opdev.model.search.Facet;
import com.opdev.model.search.OperatorType;
import com.opdev.model.search.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FacetSpecifierDto {

    @NonNull
    private TableName type;
    @NonNull
    @NotEmpty
    private String code;
    @NonNull
    @NotEmpty
    private String value;
    @NonNull
    private OperatorType operatorType;

    public Facet asFacet() {
        return Facet.builder()
                .code(code)
                .tableName(type)
                .value(value)
                .operatorType(operatorType)
                .build();
    }

}
