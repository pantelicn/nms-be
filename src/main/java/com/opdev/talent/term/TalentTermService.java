package com.opdev.talent.term;

import com.opdev.model.term.TalentTerm;

import java.util.List;

public interface TalentTermService {

     List<TalentTerm> addTermsToTalent(final String username, final List<TalentTerm> talentTerms);

     TalentTerm get(final Long id);

     TalentTerm edit(final TalentTerm modified, final String username);

     List<TalentTerm> getByTalent(final String username);

     TalentTerm getByIdAndTalent(final Long id, final String username);

     void remove(final Long id, final String username);

}
