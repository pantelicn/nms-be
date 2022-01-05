package com.opdev.search.dto;

import javax.validation.constraints.NotEmpty;

import com.opdev.model.search.Facet;
import com.opdev.model.search.OperatorType;
import com.opdev.model.search.SearchTemplate;
import com.opdev.model.search.TableName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FacetAddDto {

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

    public Facet asFacet(SearchTemplate searchTemplate) {
        return Facet.builder()
                .code(code)
                .tableName(type)
                .value(value)
                .operatorType(operatorType)
                .searchTemplate(searchTemplate)
                .build();
    }

}
