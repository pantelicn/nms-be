package com.opdev.position;

import com.opdev.config.security.Roles;
import com.opdev.model.talent.Position;
import com.opdev.position.dto.PositionUpdateDto;
import com.opdev.position.dto.PositionCreateDto;
import com.opdev.position.dto.PositionViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    @ResponseStatus(HttpStatus.CREATED)
    public PositionViewDto add(@Valid @RequestBody final PositionCreateDto newPositionDto) {
        final Position created = positionService.add(newPositionDto.asPosition());
        return new PositionViewDto(created);
    }

    @PutMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<PositionViewDto> edit(@Valid @RequestBody final PositionUpdateDto modifiedPositionDto) {
        final Position modified = positionService.edit(modifiedPositionDto.asPosition());
        return ResponseEntity.ok(new PositionViewDto(modified));
    }

    @GetMapping("/{code}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PositionViewDto> get(@PathVariable final String code) {
        final Position found = positionService.get(code);
        return ResponseEntity.ok(new PositionViewDto(found));
    }

    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<Void> remove(@PathVariable final String code) {
        positionService.remove(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public Page<PositionViewDto> find(@PageableDefault final Pageable pageable) {
        final Page<Position> found = positionService.find(pageable);
        return found.map(PositionViewDto::new);
    }

}
