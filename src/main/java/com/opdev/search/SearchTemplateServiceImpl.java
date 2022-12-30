package com.opdev.search;

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
import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import com.opdev.repository.FacetRepository;
import com.opdev.repository.SearchTemplateRepository;
import com.opdev.term.TermService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchTemplateServiceImpl implements SearchTemplateService {

    private final SearchTemplateRepository repository;
    private final TermService termService;
    private final CompanyService companyService;
    private final FacetRepository facetRepository;

    @Override
    @Transactional
    public SearchTemplate add(@NonNull final SearchTemplate newSearchTemplate, @NonNull final String companyUsername) {
        Company foundCompany = companyService.getByUsername(companyUsername);
        newSearchTemplate.setCompany(foundCompany);
        newSearchTemplate.getFacets().forEach(facet -> {
            validateFacet(facet);
            facet.setSearchTemplate(newSearchTemplate);
        });
        return repository.save(newSearchTemplate);
    }

    @Override
    @Transactional
    public SearchTemplate edit(@NonNull final SearchTemplate modified, @NonNull final String companyUsername) {
        SearchTemplate found = get(modified.getId(), companyUsername);
        updateFacets(found, modified, companyUsername);
        found.setName(modified.getName());
        found.setExperienceYears(modified.getExperienceYears());
        found.setAvailableLocations(modified.getAvailableLocations());
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
                .message("Entity.not.found").entity("SearchTemplate").id(id + "_" + companyUsername).build());
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
        return facetRepository.save(newFacet);
    }

    @Override
    @Transactional
    public Facet editFacet(@NonNull final Long id, @NonNull final Facet newFacet, @NonNull final String companyUsername) {
        validateFacet(newFacet);
        SearchTemplate foundSearchTemplate = get(id, companyUsername);
        if(facetRepository.findByIdAndSearchTemplate(newFacet.getId(), foundSearchTemplate).isEmpty()) {
            throw ApiEntityNotFoundException.builder()
                    .message("Entity.not.found")
                    .entity("Facet").id(newFacet.getId().toString() + "_" + foundSearchTemplate.getId().toString())
                    .build();
        }
        return facetRepository.save(newFacet);
    }

    @Override
    @Transactional
    public void removeFacet(@NonNull final Long id, @NonNull final Long facetId, @NonNull final String companyUsername) {
        SearchTemplate foundSearchTemplate = get(id, companyUsername);
        Facet foundFacet = facetRepository.findByIdAndSearchTemplate(facetId, foundSearchTemplate).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found").entity("Facet").id(facetId + "_" + foundSearchTemplate.getId().toString()).build());
        facetRepository.delete(foundFacet);
    }

    @Transactional
    public void updateFacets(SearchTemplate found, SearchTemplate modified, String companyUsername) {
        addNewFacets(found, modified, companyUsername);
        updateExistingFacets(found, modified, companyUsername);
        removeRedundantFacets(found, modified);
    }

    private void addNewFacets(SearchTemplate found, SearchTemplate modified, String companyUsername) {
        modified
                .getFacets()
                .stream()
                .filter(facet -> facet.getId() == null)
                .forEach(facet -> addFacet(found.getId(), facet, companyUsername));
    }

    private void updateExistingFacets(SearchTemplate found, SearchTemplate modified, String companyUsername) {
        modified
                .getFacets()
                .stream()
                .filter(facet -> facet.getId() != null)
                .forEach(facet -> editFacet(found.getId(), facet, companyUsername));
    }

    private void removeRedundantFacets(SearchTemplate found, SearchTemplate modified) {
        List<Facet> redundantFacets = found
                .getFacets()
                .stream()
                .filter(facet -> !modified.containsFacet(facet))
                .collect(Collectors.toList());

        found.removeAllFacets(redundantFacets);
    }

    private void validateFacet(Facet facet) {
        if (facet.getTableName() == TableName.POSITION) {
            validatePosition(facet);
        } else if (facet.getTableName() == TableName.SKILL) {
            validateSkill(facet);
        }
        if (facet.getTableName() == TableName.TERM) {
            validateTerm(facet);
        }
    }

    private void validatePosition(Facet facet) {
        if (facet.getOperatorType() != OperatorType.EQ) {
            throw new FacetInvalidPositionException(facet.getCode(), facet.getOperatorType());
        }
    }

    private void validateSkill(Facet facet) {
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
