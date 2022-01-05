package com.opdev.position.dto;

import com.opdev.model.talent.Position;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PositionUpdateDto {

    @NonNull
    @NotNull
    private Long id;
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
        return Position.builder().id(id).name(name).description(description).code(code).build();
    }

}
