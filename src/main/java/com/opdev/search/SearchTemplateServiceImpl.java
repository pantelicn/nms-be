package com.opdev.search;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.company.service.CompanyService;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.exception.FacetInvalidPositionException;
import com.opdev.exception.FacetInvalidSkillException;
import com.opdev.exception.FacetInvalidTermException;
import com.opdev.model.company.Company;
import com.opdev.model.search.Facet;
import com.opdev.model.search.OperatorType;
import com.opdev.model.search.SearchTemplate;
import com.opdev.model.search.TableName;
import com.opdev.model.talent.Position;
import com.opdev.model.talent.Skill;
import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import com.opdev.position.PositionService;
import com.opdev.repository.FacetRepository;
import com.opdev.repository.SearchTemplateRepository;
import com.opdev.skill.SkillService;
import com.opdev.term.TermService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchTemplateServiceImpl implements SearchTemplateService {

    private final SearchTemplateRepository repository;
    private final TermService termService;
    private final PositionService positionService;
    private final SkillService skillService;
    private final CompanyService companyService;
    private final FacetRepository facetRepository;

    @Override
    @Transactional
    public SearchTemplate add(@NonNull final String name, @NonNull final List<Facet> facets, @NonNull final String companyUsername) {
        Company foundCompany = companyService.getByUsername(companyUsername);
        SearchTemplate newSearchTemplate = SearchTemplate.builder()
                .name(name)
                .company(foundCompany)
                .build();
        facets.forEach(facet -> {
            validateFacet(facet);
            facet.setSearchTemplate(newSearchTemplate);
        });
        newSearchTemplate.getFacets().addAll(facets);
        return repository.save(newSearchTemplate);
    }

    @Override
    @Transactional
    public SearchTemplate edit(@NonNull final Long id, @NonNull final String newName, @NonNull final String companyUsername) {
        Company foundCompany = companyService.getByUsername(companyUsername);
        SearchTemplate found = repository.findByIdAndCompany(id, foundCompany).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found").entity("SearchTemplate").id(id.toString() + "_" + companyUsername).build());
        found.setName(newName);
        return repository.save(found);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchTemplate> findAllForCompany(@NonNull final String companyUsername) {
        Company foundCompany = companyService.getByUsername(companyUsername);
        return repository.findByCompany(foundCompany);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchTemplate get(@NonNull final Long id, @NonNull final String companyUsername) {
        Company foundCompany = companyService.getByUsername(companyUsername);
        return repository.findByIdAndCompany(id, foundCompany).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found").entity("SearchTemplate").id(id.toString() + "_" + companyUsername).build());
    }

    @Override
    @Transactional
    public void remove(@NonNull final Long id, @NonNull final String companyUsername) {
        SearchTemplate found = get(id, companyUsername);
        repository.delete(found);
    }

    @Override
    @Transactional
    public Facet addFacet(@NonNull final Long id, @NonNull final Facet newFacet, @NonNull final String companyUsername) {
        get(id, companyUsername);
        validateFacet(newFacet);
        Facet created =  facetRepository.save(newFacet);
        return created;
    }

    @Override
    @Transactional
    public Facet editFacet(@NonNull final Long id, @NonNull final Facet newFacet, @NonNull final String companyUsername) {
        validateFacet(newFacet);
        SearchTemplate foundSearchTemplate = get(id, companyUsername);
        facetRepository.findByIdAndSearchTemplate(newFacet.getId(), foundSearchTemplate).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found").entity("Facet").id(newFacet.getId().toString() + "_" + foundSearchTemplate.getId().toString()).build());
        return facetRepository.save(newFacet);
    }

    @Override
    @Transactional
    public void removeFacet(@NonNull final Long id, @NonNull final Long facetId, @NonNull final String companyUsername) {
        SearchTemplate foundSearchTemplate = get(id, companyUsername);
        Facet foundFacet = facetRepository.findByIdAndSearchTemplate(facetId, foundSearchTemplate).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found").entity("Facet").id(facetId.toString() + "_" + foundSearchTemplate.getId().toString()).build());
        facetRepository.delete(foundFacet);
    }

    private void validateFacet(Facet facet) {
        if (facet.getTableName() == TableName.POSITION) {
            validatePosition(facet);
        } else if (facet.getTableName() == TableName.SKILL) {
            validateSkill(facet);
        } if (facet.getTableName() == TableName.TERM) {
            validateTerm(facet);
        }
    }

    private void validatePosition(Facet facet) {
        Position foundPosition = positionService.get(facet.getCode());
        if (facet.getOperatorType() != OperatorType.EQ) {
            throw new FacetInvalidPositionException(facet.getCode(), facet.getOperatorType());
        }
    }

    private void validateSkill(Facet facet) {
        Skill foundSkill = skillService.get(facet.getCode());
        if (facet.getOperatorType() != OperatorType.EQ) {
            throw new FacetInvalidSkillException(facet.getCode(), facet.getOperatorType());
        }
    }

    private void validateTerm(Facet facet) {
        Term foundTerm = termService.get(facet.getCode());
        if ((foundTerm.getType() == TermType.BOOLEAN || foundTerm.getType() == TermType.STRING) && facet.getOperatorType() != OperatorType.EQ) {
            throw new FacetInvalidTermException(facet.getCode(), facet.getOperatorType());
        }
    }
}
