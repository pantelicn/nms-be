package com.opdev.repository;

import com.opdev.model.talent.Position;
import com.opdev.model.talent.TalentPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalentPositionRepository extends JpaRepository<TalentPosition, Long> {

    List<TalentPosition> getByTalentUserUsername(String username);

    void removeByPositionIn(List<Position> positions);

}
