package com.opdev.talent;

import com.opdev.model.talent.Talent;
import com.opdev.talent.dto.TalentViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/public/talents")
@RequiredArgsConstructor
public class PublicTalentController {

    private final TalentService service;

    @GetMapping
    public List<TalentViewDto> findLatest10ByCountry(@RequestParam final String country) {
        List<Talent> found = service.findLatest10ByCountry(country);
        return found.stream().map(TalentViewDto::new).collect(Collectors.toList());
    }

}
