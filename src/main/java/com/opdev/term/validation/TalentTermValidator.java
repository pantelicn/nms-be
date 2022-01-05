package com.opdev.term.validation;

import com.opdev.exception.ApiValidationException;
import com.opdev.model.term.TalentTerm;
import com.opdev.model.term.TermType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.format.DateTimeParseException;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.time.LocalDate.parse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TalentTermValidator {

    public static void validate(@NonNull TalentTerm talentTerm) {
        validate(talentTerm.getTerm().getType(), talentTerm.getValue());
    }

    public static void validate(@NonNull TermType type, @NonNull String value) {
        try {
            switch (type) {
                case INT:
                    parseInt(value);
                    break;
                case DATE:
                    parse(value);
                    break;
                case BIGINT:
                    parseLong(value);
                    break;
                case BOOLEAN:
                    parseBoolean(value);
                    break;
                default:
                    break;
            }
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new ApiValidationException("Invalid value");
        }
    }

    private static void parseBoolean(String str) {
        if (!str.equalsIgnoreCase("true") && !str.equals("false")) {
            throw new IllegalArgumentException(format("For input string: %s", str));
        }
    }

}
