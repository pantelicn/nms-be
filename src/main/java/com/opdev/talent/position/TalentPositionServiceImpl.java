package com.opdev.talent.position;

import com.opdev.model.talent.Position;
import com.opdev.model.talent.Talent;
import com.opdev.model.talent.TalentPosition;
import com.opdev.repository.TalentPositionRepository;
import com.opdev.talent.TalentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalentPositionServiceImpl implements TalentPositionService {

    private final TalentPositionRepository repository;
    private final TalentService talentService;

    @Override
    @Transactional
    public void addPositionsToTalent(final String username, final List<Position> positions) {
        Talent found = talentService.getByUsername(username);
        List<TalentPosition> talentPositions = positions.stream()
                .map(position -> TalentPosition.builder().position(position).talent(found).build())
                .collect(Collectors.toList());
        repository.saveAll(talentPositions);
    }

    @Override
    public void removePositionsFromTalent(String username, List<Position> positions) {
        repository.removeByPositionIn(positions);
        LOGGER.info("Removed {} positions from talent {}", positions.size(), username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Position> getByTalent(final String username) {
        return repository.getByTalentUserUsername(username).stream()
                .map(TalentPosition::getPosition)
                .collect(Collectors.toList());
    }

}
