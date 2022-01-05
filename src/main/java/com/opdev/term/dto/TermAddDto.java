package com.opdev.term.dto;

import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import com.opdev.util.CodeGenerator;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TermAddDto {

    @NonNull
    @NotEmpty
    private String name;

    @NonNull
    @NotEmpty
    private String description;

    @NonNull
    private TermType type;

    public Term asTerm() {
        return Term.builder()
                .name(name)
                .description(description)
                .type(type)
                .code(CodeGenerator.generate(name))
                .build();
    }

}
