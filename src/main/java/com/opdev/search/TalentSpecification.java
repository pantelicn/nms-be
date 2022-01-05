package com.opdev.search;

import com.opdev.exception.ApiBadRequestException;
import com.opdev.model.search.Facet;
import com.opdev.model.search.SearchTemplate;
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

@RequiredArgsConstructor
public class TalentSpecification implements Specification<Talent> {

    private final transient SearchTemplate searchTemplate;

    private static final String VALUE_ATTRIBUTE = "value";
    private static final String CODE_ATTRIBUTE = "code";
    private static final Map<TableName, String> joinTableNames = Map.of(
            TableName.SKILL, "talentSkills",
            TableName.TERM, "talentTerms",
            TableName.POSITION, "talentPositions"
    );

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
        for (Facet facet : searchTemplate.getFacets()) {
            Join<Talent, ?> joinEntity = root.join(joinTableNames.get(facet.getTableName()), JoinType.INNER);
            Join<?, ?> entity = joinEntity.join(facet.getTableName().toString(), JoinType.INNER);

            predicates.add(criteriaBuilder.equal(entity.get(CODE_ATTRIBUTE), facet.getCode()));
            if (facet.getTableName() == TableName.TERM) {
                predicates.add(getTermPredicate(joinEntity, criteriaBuilder, facet));
            }
        }
        return predicates;
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
