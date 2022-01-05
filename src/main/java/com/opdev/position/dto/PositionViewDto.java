package com.opdev.position.dto;

import com.opdev.model.talent.Position;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionViewDto {

    private Long id;
    private String name;
    private String description;
    private String code;

    public PositionViewDto(final Position position) {
        id = position.getId();
        name = position.getName();
        description = position.getDescription();
        code = position.getCode();
    }

}
