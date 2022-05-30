package com.opdev.util;

import com.opdev.repository.BenefitRepository;
import com.opdev.repository.CompanyRepository;
import com.opdev.repository.ContactRepository;
import com.opdev.repository.LocationRepository;
import com.opdev.repository.PositionRepository;
import com.opdev.repository.PositionSkillRepository;
import com.opdev.repository.PostRepository;
import com.opdev.repository.RequestRepository;
import com.opdev.repository.SkillRepository;
import com.opdev.repository.TalentPositionRepository;
import com.opdev.repository.TalentRepository;
import com.opdev.repository.TalentSkillRepository;
import com.opdev.repository.TalentTermRepository;
import com.opdev.repository.TermRepository;
import com.opdev.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class RepositoryBundler {

    @Autowired
    protected CompanyRepository companyRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TalentRepository talentRepository;

    @Autowired
    protected ContactRepository contactRepository;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected BenefitRepository benefitRepository;

    @Autowired
    protected LocationRepository locationRepository;

    @Autowired
    protected PositionRepository positionRepository;

    @Autowired
    protected TalentPositionRepository talentPositionRepository;

    @Autowired
    protected SkillRepository skillRepository;

    @Autowired
    protected PositionSkillRepository positionSkillRepository;

    @Autowired
    protected TalentSkillRepository talentSkillRepository;

    @Autowired
    protected TermRepository termRepository;

    @Autowired
    protected TalentTermRepository talentTermRepository;

    @Autowired
    protected RequestRepository requestRepository;

}
