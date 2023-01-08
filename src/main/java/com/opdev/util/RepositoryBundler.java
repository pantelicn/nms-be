package com.opdev.util;

import com.opdev.repository.*;
import com.opdev.subscription.SubscriptionService;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class RepositoryBundler {

    @Autowired
    protected CompanyRepository companyRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TalentRepository talentRepository;

    @Autowired
    protected VerificationTokenRepository verificationTokenRepository;

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

    @Autowired
    protected AvailableChatRepository availableChatRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected PlanRepository planRepository;

    @Autowired
    protected PlanProductRepository planProductRepository;

    @Autowired
    protected SubscriptionService subscriptionService;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRoleRepository userRoleRepository;

    @Autowired
    protected TalentAvailableLocationRepository availableLocationRepository;

    @Autowired
    protected CountryRepository countryRepository;

    @Autowired
    protected CityRepository cityRepository;

}
