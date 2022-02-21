package com.opdev.term.validation;

import com.opdev.exception.ApiValidationException;
import com.opdev.model.term.TalentTerm;
import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import com.opdev.model.term.UnitOfMeasure;

import org.junit.jupiter.api.Test;

import static com.opdev.term.validation.TalentTermValidator.validate;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TalentTermValidatorTest {

    @Test
    void validate_validBoolean() {
        final Term term = createTerm(TermType.BOOLEAN);
        TalentTerm talentTerm = TalentTerm.builder().value("true").negotiable(true).term(term).build();
        validate(talentTerm);

        talentTerm = TalentTerm.builder().value("false").negotiable(true).term(term).build();
        validate(talentTerm);
    }

    @Test
    void validate_invalidBoolean() {
        final Term term = createTerm(TermType.BOOLEAN);
        TalentTerm talentTerm = TalentTerm.builder().value("123").negotiable(true).term(term).build();

        assertThrows(ApiValidationException.class, () -> validate(talentTerm));
    }

    @Test
    void validate_validInteger() {
        final Term term = createTerm(TermType.INT);
        TalentTerm talentTerm = TalentTerm.builder().value("123").unitOfMeasure(UnitOfMeasure.EURO).negotiable(true).term(term).build();

        validate(talentTerm);
    }

    @Test
    void validate_invalidInteger() {
        final Term term = createTerm(TermType.INT);
        final TalentTerm talentTerm1 = TalentTerm.builder().value("123a").negotiable(true).term(term).build();

        assertThrows(ApiValidationException.class, () -> validate(talentTerm1));

        TalentTerm talentTerm2 = TalentTerm.builder().value("21474836490").negotiable(true).term(term).build();
        assertThrows(ApiValidationException.class, () -> validate(talentTerm2));
    }

    @Test
    void validate_validBigInteger() {
        final Term term = createTerm(TermType.BIGINT);
        TalentTerm talentTerm = TalentTerm.builder().value("21474836490").unitOfMeasure(UnitOfMeasure.DOLLAR).negotiable(true).term(term).build();

        validate(talentTerm);
    }

    @Test
    void validate_invalidBigInteger() {
        final Term term = createTerm(TermType.BIGINT);
        final TalentTerm talentTerm = TalentTerm.builder().value("123a").negotiable(true).term(term).build();

        assertThrows(ApiValidationException.class, () -> validate(talentTerm));
    }

    @Test
    void validate_validDate() {
        final Term term = createTerm(TermType.DATE);
        final TalentTerm talentTerm = TalentTerm.builder().value("2020-01-01").negotiable(true).term(term).build();

        validate(talentTerm);
    }

    @Test
    void validate_invalidDate() {
        final Term term = createTerm(TermType.DATE);
        final TalentTerm talentTerm = TalentTerm.builder().value("2020-1-1").negotiable(true).term(term).build();
        assertThrows(ApiValidationException.class, () -> validate(talentTerm));
    }

    private Term createTerm(TermType type) {
        return Term.builder().code("code").description("description").name("name").type(type).build();
    }
}
