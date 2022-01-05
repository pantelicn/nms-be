package com.opdev.talent;

import com.opdev.model.talent.Position;

import java.util.List;

public interface TalentPositionService {

    void addPositionsToTalent(final String username, final List<Position> positions);

    List<Position> getByTalent(final String username);

}
