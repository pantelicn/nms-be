package com.opdev.talent;

import com.opdev.authentication.TalentService;
import com.opdev.model.talent.Position;
import com.opdev.model.talent.Talent;
import com.opdev.model.talent.TalentPosition;
import com.opdev.repository.TalentPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public List<Position> getByTalent(final String username) {
        return repository.getByTalentUserUsername(username).stream()
                .map(TalentPosition::getPosition)
                .collect(Collectors.toList());
    }

}
