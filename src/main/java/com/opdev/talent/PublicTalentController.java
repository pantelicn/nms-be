package com.opdev.talent;

import com.opdev.talent.dto.PublicTalentViewDto;
import com.opdev.talent.search.TalentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/public/talents")
@RequiredArgsConstructor
public class PublicTalentController {

    private final TalentService service;

    @GetMapping
    public List<PublicTalentViewDto> findLatest10() {
        return service.find(new TalentSpecification(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdOn")))
                .getContent().stream()
                .map(PublicTalentViewDto::new)
                .collect(Collectors.toList());
    }

}
