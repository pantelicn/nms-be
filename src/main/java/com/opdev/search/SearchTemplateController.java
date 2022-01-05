package com.opdev.search;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.config.security.Roles;
import com.opdev.model.search.Facet;
import com.opdev.model.search.SearchTemplate;
import com.opdev.search.dto.FacetAddDto;
import com.opdev.search.dto.FacetEditDto;
import com.opdev.search.dto.FacetViewDto;
import com.opdev.search.dto.SearchTemplateAddDto;
import com.opdev.search.dto.SearchTemplateEditDto;
import com.opdev.search.dto.SearchTemplateViewDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies/{username}/search-templates")
public class SearchTemplateController {

    private final SearchTemplateService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public SearchTemplateViewDto add(@RequestBody @Valid SearchTemplateAddDto newSearchTemplate, @PathVariable String username) {
        SearchTemplate created = service.add(newSearchTemplate.getName(), newSearchTemplate.getFacets().stream().map(FacetAddDto::asFacet).collect(Collectors.toList()), username);
        return new SearchTemplateViewDto(created);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public SearchTemplateViewDto edit(@RequestBody @Valid SearchTemplateEditDto searchTemplateEditDto, @PathVariable String username) {
        SearchTemplate updated = service.edit(searchTemplateEditDto.getId(), searchTemplateEditDto.getName(), username);
        return new SearchTemplateViewDto(updated);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public List<SearchTemplateViewDto> findAllForCompany(@PathVariable String username) {
        List<SearchTemplate> found = service.findAllForCompany(username);
        return found.stream().map(SearchTemplateViewDto::new).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public SearchTemplateViewDto get(@PathVariable String username, @PathVariable Long id) {
        SearchTemplate found = service.get(id, username);
        return new SearchTemplateViewDto(found);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public void remove(@PathVariable String username, @PathVariable Long id) {
        service.remove(id, username);
    }

    @PostMapping("{id}/facets")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public FacetViewDto addFacet(@Valid @RequestBody FacetAddDto facetAddDto, @PathVariable String username, @PathVariable Long id) {
        SearchTemplate foundSearchTemplate = service.get(id, username);
        Facet created = service.addFacet(id, facetAddDto.asFacet(foundSearchTemplate), username);
        return new FacetViewDto(created);
    }

    @PutMapping("{id}/facets")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public FacetViewDto editFacet(@Valid @RequestBody FacetEditDto facetEditDto, @PathVariable String username, @PathVariable Long id) {
        SearchTemplate foundSearchTemplate = service.get(id, username);
        Facet updated = service.editFacet(id, facetEditDto.asFacet(foundSearchTemplate), username);
        return new FacetViewDto(updated);
    }

    @DeleteMapping("{id}/facets/{facetId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public void removeFacet(@PathVariable Long id, @PathVariable Long facetId, @PathVariable String username) {
        service.removeFacet(id, facetId, username);
    }

}
