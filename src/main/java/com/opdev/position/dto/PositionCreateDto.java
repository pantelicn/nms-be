package com.opdev.position.dto;

import com.opdev.model.talent.Position;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PositionCreateDto {

    @NonNull
    @NotEmpty
    private String name;

    @NonNull
    @NotEmpty
    private String description;

    @NonNull
    @NotEmpty
    private String code;

    public Position asPosition() {
        return Position.builder().code(code).name(name).description(description).build();
    }

}
