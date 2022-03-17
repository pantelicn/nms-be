package com.opdev.term;

import com.opdev.config.security.Roles;
import com.opdev.model.term.Term;
import com.opdev.term.dto.TermAddDto;
import com.opdev.term.dto.TermEditDto;
import com.opdev.term.dto.TermViewDto;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/terms")
public class TermController {

    private final TermService service;

    @PostMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<TermViewDto> add(@Valid @RequestBody TermAddDto newTerm) {
        final Term created = service.add(newTerm.asTerm());
        return ResponseEntity.status(HttpStatus.CREATED).body(new TermViewDto(created));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TermViewDto>> findAll(@Spec(path = "availableForSearch", spec = Equal.class) Specification<Term> termSpec) {
        final List<Term> found = service.findAll(termSpec);
        final List<TermViewDto> response = found.stream().map(TermViewDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
     }

     @GetMapping("{code}")
     @PreAuthorize("isAuthenticated()")
     public ResponseEntity<TermViewDto> get(@PathVariable String code) {
        final Term found = service.get(code);
        return ResponseEntity.ok(new TermViewDto(found));
     }

     @PutMapping
     @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
     public ResponseEntity<TermViewDto> edit(@Valid @RequestBody TermEditDto modified) {
        final Term updated = service.edit(modified.asTerm());
        return ResponseEntity.ok(new TermViewDto(updated));
     }

     @DeleteMapping("{code}")
     @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
     public ResponseEntity<Void> remove(@PathVariable String code) {
        service.remove(code);
        return ResponseEntity.noContent().build();
     }

}
