package com.opdev.search.dto;

import com.opdev.model.search.Facet;
import com.opdev.model.search.OperatorType;
import com.opdev.model.search.SearchTemplate;
import com.opdev.model.search.TableName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class FacetEditDto {

    private Long id;
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

    public Facet asFacet(SearchTemplate searchTemplate) {
        return Facet.builder()
                .id(id)
                .code(code)
                .tableName(type)
                .value(value)
                .operatorType(operatorType)
                .searchTemplate(searchTemplate)
                .build();
    }

}
