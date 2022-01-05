package com.opdev.position;

import com.opdev.model.talent.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PositionService {

    Position add(final Position newPosition);

    Position get(final String code);

    Page<Position> find(final Pageable pageable);

    Position edit(final Position modified);

    void remove(final String code);

}
