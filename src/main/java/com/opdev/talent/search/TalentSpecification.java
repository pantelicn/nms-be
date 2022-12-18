package com.opdev.talent.search;

import com.opdev.exception.ApiBadRequestException;
import com.opdev.model.location.AvailableLocation;
import com.opdev.model.search.LocationFilter;
import com.opdev.model.search.Facet;
import com.opdev.model.search.TableName;
import com.opdev.model.talent.Talent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@RequiredArgsConstructor
public class TalentSpecification implements Specification<Talent> {

    private static final String VALUE_ATTRIBUTE = "value";
    private static final String CODE_ATTRIBUTE = "code";
    private static final Map<TableName, String> joinTableNames = Map.of(
            TableName.SKILL, "talentSkills",
            TableName.TERM, "talentTerms",
            TableName.POSITION, "talentPositions"
    );
    private static final String AVAILABLE_LOCATIONS_TABLE_NAME = "availableLocations";

    private final transient List<Facet> facets;
    private final transient List<LocationFilter> locationFilters;
    private final transient Integer experienceYears;

    @Override
    public Predicate toPredicate(@NonNull Root<Talent> root,
                                 @NonNull CriteriaQuery<?> criteriaQuery,
                                 @NonNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = getPredicates(root, criteriaBuilder);
        criteriaQuery.distinct(true);
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private List<Predicate> getPredicates(Root<Talent> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        for (Facet facet : facets) {
            Join<Talent, ?> joinEntity = root.join(joinTableNames.get(facet.getTableName()), JoinType.INNER);
            Join<?, ?> entity = joinEntity.join(facet.getTableName().toString(), JoinType.INNER);

            predicates.add(criteriaBuilder.equal(entity.get(CODE_ATTRIBUTE), facet.getCode()));
            if (facet.getTableName() == TableName.TERM) {
                predicates.add(getTermPredicate(joinEntity, criteriaBuilder, facet));
            }
        }

        if (locationFilters != null) {
            Join<Talent, AvailableLocation> joinEntity = root.join(AVAILABLE_LOCATIONS_TABLE_NAME, JoinType.LEFT);
            List<Predicate> locationPredicateList = new ArrayList<>();
            locationFilters.forEach(locationFilter ->
                    locationPredicateList.add(getLocationPredicate(joinEntity, criteriaBuilder, locationFilter))
            );
            Predicate locationPredicate = criteriaBuilder.or(locationPredicateList.toArray(new Predicate[]{}));
            predicates.add(locationPredicate);
        }

        if (experienceYears != null) {
            predicates.add(criteriaBuilder.and(criteriaBuilder.ge(root.get("experienceYears"), experienceYears)));
        }

        return predicates;
    }

    private Predicate getLocationPredicate(Join<Talent, AvailableLocation> joinEntity, CriteriaBuilder criteriaBuilder, LocationFilter locationFilter) {
        Predicate locationPredicate = criteriaBuilder.equal(joinEntity.get("country"), locationFilter.getCountry());
        if (isNotEmpty(locationFilter.getCities())) {
            List<Predicate> cityPredicateList = new ArrayList<>();
            locationFilter.getCities()
                    .forEach(city -> cityPredicateList.add(
                            criteriaBuilder.isMember(city, joinEntity.get("cities"))
                    ));
            Predicate cityPredicate = criteriaBuilder.or(
                    criteriaBuilder.or(cityPredicateList.toArray(new Predicate[]{})),
                    criteriaBuilder.isEmpty(joinEntity.get("cities")));
            locationPredicate = criteriaBuilder.and(locationPredicate, cityPredicate);
        }
        return locationPredicate;
    }


    private Predicate getTermPredicate(Join<Talent, ?> joinEntity, CriteriaBuilder criteriaBuilder, Facet facet) {
        switch (facet.getOperatorType()) {
            case EQ:
                return criteriaBuilder.equal(joinEntity.get(VALUE_ATTRIBUTE), facet.getValue());
            case GT:
                return criteriaBuilder.gt(
                        criteriaBuilder.toLong(joinEntity.get(VALUE_ATTRIBUTE)),
                        Long.valueOf(facet.getValue())
                );
            case LT:
                return criteriaBuilder.lt(
                        criteriaBuilder.toLong(joinEntity.get(VALUE_ATTRIBUTE)),
                        Long.valueOf(facet.getValue())
                );
            case GTE:
                return criteriaBuilder.ge(
                        criteriaBuilder.toLong(joinEntity.get(VALUE_ATTRIBUTE)),
                        Long.valueOf(facet.getValue())
                );
            case LTE:
                return criteriaBuilder.le(
                        criteriaBuilder.toLong(joinEntity.get(VALUE_ATTRIBUTE)),
                        Long.valueOf(facet.getValue())
                );
            default:
                throw ApiBadRequestException.message(facet.getOperatorType() + " not implemented");
        }
    }

}
