package com.opdev.util.encoding;

public interface TalentIdEncoder {

    /**
     * Encodes the given 2 Ids and returns an encoded string representation.
     * @param talentId Talent Id
     * @param companyId Company Id
     * @return Encoded string representation of the 2 given parameters.
     */
    String encode(Long talentId, Long companyId);

    /**
     * Decodes the given string which should be the result of the encoding function from this interface.
     * @param encodedString String representing encoded Talent and Company Ids
     * @return Talent Id from the decoded Long number pair
     */
    Long decode(String encodedString);

}
