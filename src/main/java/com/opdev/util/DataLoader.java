package com.opdev.util;

import com.opdev.model.chat.AvailableChat;
import com.opdev.model.company.Benefit;
import com.opdev.model.company.Company;
import com.opdev.model.contact.Contact;
import com.opdev.model.contact.ContactType;
import com.opdev.model.location.AvailableLocation;
import com.opdev.model.location.City;
import com.opdev.model.location.CompanyLocation;
import com.opdev.model.location.Country;
import com.opdev.model.location.Location;
import com.opdev.model.post.Post;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.request.TalentTermRequest;
import com.opdev.model.request.TalentTermRequestStatus;
import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.PlanProduct;
import com.opdev.model.subscription.PlanType;
import com.opdev.model.subscription.Product;
import com.opdev.model.subscription.Subscription;
import com.opdev.model.subscription.SubscriptionStatus;
import com.opdev.model.talent.Position;
import com.opdev.model.talent.PositionSkill;
import com.opdev.model.talent.Skill;
import com.opdev.model.talent.SkillStatus;
import com.opdev.model.talent.Talent;
import com.opdev.model.talent.TalentPosition;
import com.opdev.model.talent.TalentSkill;
import com.opdev.model.term.TalentTerm;
import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import com.opdev.model.term.UnitOfMeasure;
import com.opdev.model.user.*;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@ConditionalOnProperty(name="import.local.data", havingValue="true")
public class DataLoader extends RepositoryBundler implements ApplicationRunner {

    private List<TalentTerm> goranTalentTerms;
    private Company companyGoogle;
    private Company companyFacebook;
    private Talent talentGoran;
    private Talent talentNikola;
    private Plan trialPlan;
    private Role companyRole;
    private Role talentRole;
    private Role adminRole;
    private Country serbia;
    private Country croatia;
    private City noviSad;
    private City zagreb;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        initializeCountries();
        initializeCities();
        initializeRoles();
        initializeAdmin();
        initializeSkills();
        initializePositions();
        initializeSkillPositions();
        initializeTerms();
        initializeTalents();
        initializeCompanies();
        initializeAvailableChats();
        initializePlanAndProducts();
        initializeSubscriptionForGoogle();
    }

    private void initializeCities() {
        noviSad = City.builder()
                .country(serbia)
                .latitude(1f)
                .longitude(1f)
                .name("Novi Sad")
                .build();

        noviSad = cityRepository.save(noviSad);

        zagreb = City.builder()
                .country(croatia)
                .name("Zagreb")
                .longitude(2f)
                .latitude(2f)
                .name("Zagreb")
                .build();

        zagreb = cityRepository.save(zagreb);
    }

    private void initializeCountries() {
        serbia = Country.builder()
                .code("SRB")
                .name("Serbia")
                .build();
        croatia = Country.builder()
                .code("CRO")
                .name("Croatia")
                .build();

        serbia = countryRepository.save(serbia);
        croatia = countryRepository.save(croatia);
    }

    private void initializeRoles() {
        Role roleCompany = Role.builder()
                .name("ROLE_COMPANY")
                .build();
        Role roleTalent = Role.builder()
                .name("ROLE_TALENT")
                .build();

        Role roleAdmin = Role.builder()
                .name("ROLE_ADMIN")
                .build();

        companyRole = roleRepository.save(roleCompany);
        talentRole = roleRepository.save(roleTalent);
        adminRole = roleRepository.save(roleAdmin);
    }

    private void initializeSubscriptionForGoogle() {
        Subscription subscription = Subscription.builder()
                .autoRenewal(false)
                .company(companyGoogle)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .period(Period.ofMonths(12))
                .plan(trialPlan)
                .build();

        subscriptionService.subscribe(subscription);
    }

    private void initializePlanAndProducts() {
        Product post = Product.builder()
                .name("Post")
                .description("Use Start a post to share posts.")
                .build();
        post = productRepository.save(post);
        trialPlan = Plan.builder()
                .name("Trial")
                .description("Trial period.")
                .durationInMonths(12)
                .price(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(0)))
                .type(PlanType.TRIAL)
                .build();

        trialPlan = planRepository.save(trialPlan);

        PlanProduct postPlanProduct = PlanProduct.builder()
                .plan(trialPlan)
                .limited(true)
                .product(post)
                .quantity(120)
                .build();

        planProductRepository.save(postPlanProduct);
    }

    private VerificationToken initializeVerificationToken() {
        VerificationToken createdToken = VerificationToken.builder()
                .used(true)
                .activationCode(UUID.randomUUID())
                .build();
        return verificationTokenRepository.save(createdToken);
    }

    private void initializeAdmin() {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.ADMIN)
                .username("admin@gmail.com")
                .password(passwordEncoder.encode("Admin12345!"))
                .verificationToken(initializeVerificationToken())
                .enabled(true)
                .build());

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(adminRole)
                .build();

        userRoleRepository.save(userRole);
    }

    private void initializeSkills() {
        skillRepository.saveAll(
                Arrays.asList(
                        Skill.builder()
                                .status(SkillStatus.APPROVED)
                                .name("Java")
                                .code("Java")
                                .build(),
                        Skill.builder()
                                .status(SkillStatus.APPROVED)
                                .name("Spring Boot")
                                .code("Spring")
                                .build(),
                        Skill.builder()
                                .status(SkillStatus.APPROVED)
                                .name("MySQL")
                                .code("MySQL")
                                .build(),
                        Skill.builder()
                                .status(SkillStatus.APPROVED)
                                .name("JavaScript")
                                .code("JavaScript")
                                .build(),
                        Skill.builder()
                                .status(SkillStatus.APPROVED)
                                .name("Angular")
                                .code("Angular")
                                .build(),
                        Skill.builder()
                                .name("Linux system administration")
                                .code("Linux")
                                .status(SkillStatus.APPROVED)
                                .build(),
                        Skill.builder()
                                .name("Bash scripting")
                                .code("Bash")
                                .status(SkillStatus.APPROVED)
                                .build(),
                        Skill.builder()
                                .name("Jenkins")
                                .code("Jenkins")
                                .status(SkillStatus.APPROVED)
                                .build(),
                        Skill.builder()
                                .name("Ansible")
                                .code("Ansible")
                                .status(SkillStatus.APPROVED)
                                .build(),
                        Skill.builder()
                                .name("Docker")
                                .code("Docker")
                                .status(SkillStatus.APPROVED)
                                .build()
                ));
    }

    private void initializePositions() {
        positionRepository.saveAll(
                Arrays.asList(
                        Position.builder()
                                .name("Fullstack Developer")
                                .description("Fullstack Developer")
                                .code("FD")
                                .build(),
                        Position.builder()
                                .name("DevOps")
                                .description("DevOps")
                                .code("DO")
                                .build()
                )
        );
    }

    private void initializeSkillPositions() {
        positionSkillRepository.saveAll(
                Arrays.asList(
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("FD"))
                                .skill(skillRepository.getByCode("Java"))
                                .build(),
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("FD"))
                                .skill(skillRepository.getByCode("Spring"))
                                .build(),
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("FD"))
                                .skill(skillRepository.getByCode("MySQL"))
                                .build(),
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("FD"))
                                .skill(skillRepository.getByCode("JavaScript"))
                                .build(),
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("FD"))
                                .skill(skillRepository.getByCode("Angular"))
                                .build(),
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("DO"))
                                .skill(skillRepository.getByCode("Linux"))
                                .build(),
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("DO"))
                                .skill(skillRepository.getByCode("Bash"))
                                .build(),
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("DO"))
                                .skill(skillRepository.getByCode("Jenkins"))
                                .build(),
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("DO"))
                                .skill(skillRepository.getByCode("Ansible"))
                                .build(),
                        PositionSkill.builder()
                                .position(positionRepository.getByCode("DO"))
                                .skill(skillRepository.getByCode("Docker"))
                                .build()

                )
        );
    }

    private void initializeTerms() {
        termRepository.saveAll(
                Arrays.asList(
                        Term.builder()
                                .name("Monthly Salary(Euros)")
                                .code("MONTHLY-SALARY-EUROS")
                                .description("Monthly salary in euros.")
                                .type(TermType.INT)
                                .availableForSearch(true)
                                .build(),
                        Term.builder()
                                .name("Vacation days")
                                .code("VACATION-DAYS")
                                .description("Number of vacation days per year.")
                                .type(TermType.INT)
                                .availableForSearch(true)
                                .build(),
                        Term.builder()
                                .name("Remote")
                                .code("REMOTE")
                                .description("Remote work")
                                .type(TermType.BOOLEAN)
                                .availableForSearch(true)
                                .build(),
                        Term.builder()
                                .name("Education budget")
                                .code("EDUCATION-BUDGET")
                                .description("Budget for education")
                                .type(TermType.INT)
                                .availableForSearch(true)
                                .build()
                ));
    }

    private void initializeCompanies() {
        initializeGoogle();
        initializeFacebook();
    }

    private void initializeGoogle() {
        companyGoogle = initializeGoogleData();
        initializeGooglePosts(companyGoogle);
        initializeGoogleContacts(companyGoogle);
        initializeGoogleBenefits(companyGoogle);
        //initializeGoogleRequests(companyGoogle);
    }

    private Company initializeGoogleData() {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.COMPANY)
                .username("google@gmail.com")
                .password(passwordEncoder.encode("Google12345!"))
                .verificationToken(initializeVerificationToken())
                .enabled(true)
                .build());

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(companyRole)
                .build();

        userRoleRepository.save(userRole);

        CompanyLocation location = CompanyLocation.builder()
                .city(noviSad)
                .province("Vojvodina")
                .country(serbia)
                .address("Olge Petrov 27")
                .build();

        return companyRepository.save(Company.builder()
                .description("Google is an internet search engine. It uses a proprietary algorithm that's designed to retrieve and order search results to provide the most relevant and dependable sources of data possible.")
                .name("Google")
                .location(location)
                .user(user)
                .build());
    }

    private void initializeGooglePosts(Company company) {
        postRepository.saveAll(Arrays.asList(
                Post.builder()
                        .content("https://hybrid-it.rs/ f blandit o eu massa. https://upload.wikimedia.org/wikipedia/commons/9/9a/Gull_portrait_ca_usa.jpg")
                        .title("Duis title")
                        .url("http://dummyimage.com/136x100.png/ff4444/ffffff")
                        .country(serbia)
                        .company(company)
                        .build(),
                Post.builder()
                        .content("Fusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl.")
                        .title("Duis title")
                        .url("http://dummyimage.com/163x100.png/ff4444/ffffff")
                        .country(serbia)
                        .company(company)
                        .build(),
                Post.builder()
                        .content("Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis.")
                        .title("Duis title")
                        .url("http://dummyimage.com/207x100.png/cc0000/ffffff")
                        .country(croatia)
                        .company(company)
                        .build()));
    }

    private void initializeGoogleContacts(Company company) {
        contactRepository.saveAll(Arrays.asList(
                Contact.builder()
                        .type(ContactType.TELEPHONE)
                        .value("+7 (496) 649-2300")
                        .company(company)
                        .build(),
                Contact.builder()
                        .type(ContactType.EMAIL)
                        .value("ppavia2@oaic.gov.au")
                        .company(company)
                        .build(),
                Contact.builder()
                        .type(ContactType.URL)
                        .value("http://www.google.com")
                        .company(company)
                        .build()
        ));
    }

    private void initializeGoogleBenefits(Company company) {
        benefitRepository.saveAll(Arrays.asList(
                Benefit.builder()
                        .name("Insurance")
                        .description("Integer tincidunt ante vel ipsum. Praesent blandit lacinia erat.")
                        .isDefault(true)
                        .company(company)
                        .build(),
                Benefit.builder()
                        .name("Paid Vacation and Sick Time")
                        .description("Pellentesque at nulla. Suspendisse potenti. Cras in purus eu magna vulputate luctus.")
                        .isDefault(true)
                        .company(company)
                        .build(),
                Benefit.builder()
                        .name("Flexible Working hours")
                        .description("Aenean fermentum. Donec ut mauris eget massa tempor convallis.")
                        .isDefault(true)
                        .company(company)
                        .build()
        ));
    }

    private void initializeGoogleRequests(Company company) {
        Request request = Request.builder()
                .company(company)
                .note("Java dev senior project NMS")
                .status(RequestStatus.PENDING)
                .talent(talentGoran)
                .build();
        List<TalentTermRequest> talentTermRequests = new ArrayList<>();
        goranTalentTerms.forEach(goranTalentTerm -> {
            TalentTermRequest talentTermRequest;
            if (!goranTalentTerm.getNegotiable()) {
                talentTermRequest = TalentTermRequest.builder()
                        .request(request)
                        .talentTerm(goranTalentTerm)
                        .status(TalentTermRequestStatus.ACCEPTED)
                        .build();
            } else {
                if (goranTalentTerm.getUnitOfMeasure() == UnitOfMeasure.DAYS) {
                    talentTermRequest = TalentTermRequest.builder()
                            .request(request)
                            .talentTerm(goranTalentTerm)
                            .counterOffer("15")
                            .status(TalentTermRequestStatus.COUNTER_OFFER_COMPANY)
                            .build();
                } else if(goranTalentTerm.getTerm().getCode().equals("EDUCATION-BUDGET")) {
                    talentTermRequest = TalentTermRequest.builder()
                            .request(request)
                            .talentTerm(goranTalentTerm)
                            .counterOffer("2000")
                            .status(TalentTermRequestStatus.COUNTER_OFFER_TALENT)
                            .build();
                } else {
                    talentTermRequest = TalentTermRequest.builder()
                            .request(request)
                            .talentTerm(goranTalentTerm)
                            .status(TalentTermRequestStatus.ACCEPTED)
                            .build();
                }
            }
            talentTermRequests.add(talentTermRequest);
        });
        request.setTalentTermRequests(talentTermRequests);
        requestRepository.save(request);
    }

    private void initializeFacebook() {
        companyFacebook = initializeFacebookData();
        initializeFacebookPosts(companyFacebook);
        initializeFacebookContacts(companyFacebook);
        initializeFacebookBenefits(companyFacebook);
    }

    private Company initializeFacebookData() {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.COMPANY)
                .username("facebook@facebook.com")
                .password("Facebook12345!")
                .verificationToken(initializeVerificationToken())
                .enabled(true)
                .build());

        CompanyLocation location = CompanyLocation.builder()
                .city(zagreb)
                .country(croatia)
                .address("1257 Vermont Parkway")
                .build();

        return companyRepository.save(Company.builder()
                .description("Nullam sit amet turpis elementum ligula vehicula consequat. Morbi a ipsum. Integer a nibh.")
                .name("Facebook")
                .location(location)
                .user(user)
                .build());
    }

    private void initializeFacebookPosts(Company company) {
        postRepository.saveAll(Arrays.asList(
                Post.builder()
                        .content("Integer ac leo. Pellentesque ultrices mattis odio. Donec vitae nisi.")
                        .title("Duis title")
                        .url("http://dummyimage.com/190x100.png/dddddd/000000")
                        .country(serbia)
                        .company(company)
                        .build(),
                Post.builder()
                        .content("Proin interdum mauris non ligula pellentesque ultrices.")
                        .title("Proin title")
                        .url("http://dummyimage.com/236x100.png/cc0000/ffffff")
                        .country(croatia)
                        .company(company)
                        .build(),
                Post.builder()
                        .content("Maecenas tristique, est et tempus semper, est quam pharetra magna, ac consequat metus sapien ut nunc.")
                        .title("Maecenas title")
                        .url("http://dummyimage.com/146x100.png/5fa2dd/ffffff")
                        .country(serbia)
                        .company(company)
                        .build()
        ));
    }

    private void initializeFacebookContacts(Company company) {
        contactRepository.saveAll(Arrays.asList(
                Contact.builder()
                        .type(ContactType.MOBILE_PHONE)
                        .value("+86 (944) 125-1126")
                        .company(company)
                        .build(),
                Contact.builder()
                        .type(ContactType.EMAIL)
                        .value("groizni@google.nl")
                        .company(company)
                        .build(),
                Contact.builder()
                        .type(ContactType.URL)
                        .value("https://drupal.org/nec.xml?lorem=donec&ipsum=odio")
                        .company(company)
                        .build()

        ));
    }

    private void initializeFacebookBenefits(Company company) {
        benefitRepository.saveAll(Arrays.asList(
                Benefit.builder()
                        .name("Retirement plans")
                        .description("Integer tincidunt ante vel ipsum. Praesent blandit lacinia erat.")
                        .isDefault(true)
                        .company(company)
                        .build(),
                Benefit.builder()
                        .name("Food & Beverage")
                        .description("Pellentesque at nulla. Suspendisse potenti. Cras in purus eu magna vulputate luctus.")
                        .isDefault(true)
                        .company(company)
                        .build(),
                Benefit.builder()
                        .name("Health and wellness")
                        .description("Aenean fermentum. Donec ut mauris eget massa tempor convallis.")
                        .isDefault(true)
                        .company(company)
                        .build()
        ));
    }


    private void initializeTalents() {
        initializeNikola();
        //initializeGoran();
    }


    private void initializeNikola() {
        talentNikola = initializeNikolaData();
        initializeNikolaContacts(talentNikola);
        //initializeNikolaAvailableLocations(talentNikola);
        initializeNikolaPositions(talentNikola);
        initializeNikolaTalentTerms(talentNikola);
    }

    private Talent initializeNikolaData() {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.TALENT)
                .username("nikola@gmail.com")
                .password(passwordEncoder.encode("Nikola12345!"))
                .verificationToken(initializeVerificationToken())
                .enabled(true)
                .build());

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(talentRole)
                .build();

        userRoleRepository.save(userRole);

        Location currentLocation = Location.builder()
                .country("United States")
                .city("Cincinnati")
                .province("Ohio")
                .countryCode("US")
                .build();

        return talentRepository.save(Talent.builder()
                .firstName("Nikola")
                .middleName("Jozef")
                .lastName("Pantelic")
                .dateOfBirth(LocalDate.of(1995, 1, 8))
                .user(user)
                .available(true)
                .availabilityChangeDate(Instant.now())
                .availableLocations(List.of(
                        AvailableLocation.builder()
                                .country("Serbia")
                                .cities(Set.of("Novi Sad")).build(),
                        AvailableLocation.builder()
                                .country("Croatia")
                                .cities(Set.of("Split")).build()
                ))
                .build());
    }

    private void initializeNikolaContacts(Talent talent) {
        contactRepository.saveAll(
                Arrays.asList(
                        Contact.builder()
                                .type(ContactType.MOBILE_PHONE)
                                .value("+62 (810) 132-9688")
                                .talent(talent)
                                .build(),
                        Contact.builder()
                                .type(ContactType.EMAIL)
                                .value("alugtonf@taobao.com")
                                .talent(talent)
                                .build(),
                        Contact.builder()
                                .type(ContactType.URL)
                                .value("http://about.me/ultrices/posuere/cubilia/curae.json?volutpat=id")
                                .talent(talent)
                                .build()

                ));
    }

    private void initializeNikolaAvailableLocations(Talent talent) {
        availableLocationRepository.saveAll(
                List.of(
                        AvailableLocation.builder()
                                .country("United States")
                                .cities(Set.of("Dallas")).build(),
                        AvailableLocation.builder()
                                .country("Bosnia and Herzegovina")
                                .cities(Set.of("Vareš")).build()
                )
        );
    }

    private void initializeNikolaPositions(Talent talent) {
        Position position = positionRepository.getByCode("FD");

        talentPositionRepository.save(
                TalentPosition.builder()
                        .talent(talent)
                        .position(position)
                        .build()
        );

        initializeTalentPositionSkills(talent, position);
    }

    private void initializeTalentPositionSkills(Talent talent, Position position) {
        List<PositionSkill> positionSkills = positionSkillRepository.findByPosition(position);

        for (PositionSkill positionSkill : positionSkills) {
            talentSkillRepository.save(
                    TalentSkill.builder()
                            .talent(talent)
                            .skill(positionSkill.getSkill())
                            .build()
            );
        }
    }

    private void initializeNikolaTalentTerms(Talent talent) {
        talentTermRepository.saveAll(
                Arrays.asList(
                        TalentTerm.builder()
                                .value("2000")
                                .negotiable(true)
                                .talent(talent)
                                .term(termRepository.getByCode("MONTHLY-SALARY-EUROS"))
                                .unitOfMeasure(UnitOfMeasure.EURO)
                                .build(),
                        TalentTerm.builder()
                                .value("20")
                                .negotiable(false)
                                .talent(talent)
                                .term(termRepository.getByCode("VACATION-DAYS"))
                                .unitOfMeasure(UnitOfMeasure.DAYS)
                                .build()
                ));
    }

    private void initializeGoran() {
        talentGoran = initializeGoranData();
        initializeGoranContacts(talentGoran);
        initializeGoranAvailableLocations(talentGoran);
        initializeGoranPositions(talentGoran);
        initializeGoranTalentTerms(talentGoran);
    }

    private Talent initializeGoranData() {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.TALENT)
                .username("goransasic@gmail.com")
                .password(passwordEncoder.encode("Goran12345!"))
                .verificationToken(initializeVerificationToken())
                .enabled(true)
                .build());

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(talentRole)
                .build();

        userRoleRepository.save(userRole);

        Location currentLocation = Location.builder()
                .country("Costa Rica")
                .city("La Suiza")
                .province("Turrialba")
                .countryCode("CR")
                .build();
        Talent goran = Talent.builder()
                .firstName("Goran")
                .lastName("Sasic")
                .dateOfBirth(LocalDate.of(1995, 1, 8))
                .user(user)
                .available(true)
                .availabilityChangeDate(Instant.now())
                .build();
        currentLocation.setTalent(goran);

        return talentRepository.save(goran);
    }

    private void initializeGoranContacts(Talent talent) {
        contactRepository.saveAll(
                Arrays.asList(
                        Contact.builder()
                                .type(ContactType.MOBILE_PHONE)
                                .value("+86 (894) 637-0532")
                                .talent(talent)
                                .build(),
                        Contact.builder()
                                .type(ContactType.EMAIL)
                                .value("bjolliffz@cornell.edu")
                                .talent(talent)
                                .build(),
                        Contact.builder()
                                .type(ContactType.URL)
                                .value("https://goo.ne.jp/sodales/scelerisque/mauris/sit.jsp?a=congue&pede=vivamus")
                                .talent(talent)
                                .build()

                ));
    }

    private void initializeGoranAvailableLocations(Talent talent) {
        availableLocationRepository.saveAll(
                List.of(
                        AvailableLocation.builder()
                                .country("Mexico")
                                .cities(Set.of("Parque Industrial")).build(),
                        AvailableLocation.builder()
                                .country("Sweden")
                                .cities(Set.of("Nol")).build()
                )
        );
    }

    private void initializeGoranPositions(Talent talent) {
        Position position = positionRepository.getByCode("DO");

        talentPositionRepository.save(
                TalentPosition.builder()
                        .talent(talent)
                        .position(position)
                        .build()
        );

        initializeTalentPositionSkills(talent, position);
    }

    private void initializeGoranTalentTerms(Talent talent) {
        goranTalentTerms = talentTermRepository.saveAll(
                Arrays.asList(
                        TalentTerm.builder()
                                .value("5000")
                                .negotiable(false)
                                .talent(talent)
                                .unitOfMeasure(UnitOfMeasure.EURO)
                                .term(termRepository.getByCode("MONTHLY-SALARY-EUROS"))
                                .build(),
                        TalentTerm.builder()
                                .value("20")
                                .negotiable(true)
                                .talent(talent)
                                .unitOfMeasure(UnitOfMeasure.DAYS)
                                .term(termRepository.getByCode("VACATION-DAYS"))
                                .build(),
                        TalentTerm.builder()
                                .value("3000")
                                .negotiable(true)
                                .talent(talent)
                                .term(termRepository.getByCode("EDUCATION-BUDGET"))
                                .unitOfMeasure(UnitOfMeasure.EURO)
                                .build(),
                        TalentTerm.builder()
                                .value("REMOTE")
                                .negotiable(true)
                                .talent(talent)
                                .term(termRepository.getByCode("REMOTE"))
                                .build()
                ));
    }

    private void initializeAvailableChats() {
        availableChatRepository.saveAll(
                List.of(
                        AvailableChat.builder()
                            .company(companyGoogle.getUser())
                            .companyName(companyGoogle.getName())
                            .companyUsername(companyGoogle.getUser().getUsername())
                            .talent(talentNikola.getUser())
                            .talentName(talentNikola.getFullName())
                            .talentUsername(talentNikola.getUser().getUsername())
                            .build()
                )
        );
    }

}
