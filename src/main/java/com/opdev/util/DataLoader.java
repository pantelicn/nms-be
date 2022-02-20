package com.opdev.util;

import com.opdev.model.company.Benefit;
import com.opdev.model.company.Company;
import com.opdev.model.contact.Contact;
import com.opdev.model.contact.ContactType;
import com.opdev.model.location.CompanyLocation;
import com.opdev.model.location.Location;
import com.opdev.model.post.Post;
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
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader extends RepositoryBundler implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        initializeAdmin();
        initializeCompanies();
        initializeSkills();
        initializePositions();
        initializeSkillPositions();
        initializeTerms();
        initializeTalents();
    }

    private void initializeAdmin() {
        userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.ADMIN)
                .username("admin@gmail.com")
                .password("Admin12345!")
                .build());
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
                                .name("Salary")
                                .code("Salary")
                                .description("Cras sed lacinia metus, ut aliquet metus. Integer posuere nibh non dui imperdiet")
                                .type(TermType.BIGINT)
                                .build(),
                        Term.builder()
                                .name("Parking")
                                .code("Parking")
                                .description("Vivamus mi sem, pharetra a scelerisque a, tincidunt congue")
                                .type(TermType.BOOLEAN)
                                .build(),
                        Term.builder()
                                .name("Holidays")
                                .code("Holidays")
                                .description("Phasellus fermentum vel sem et tempor. Nulla ac turpis finibus augue dapibus ornare.")
                                .type(TermType.INT)
                                .build()
                ));
    }

    private void initializeCompanies() {
        initializeGoogle();
        initializeFacebook();
    }

    private void initializeGoogle() {
        Company company = initializeGoogleData();
        initializeGooglePosts(company);
        initializeGoogleContacts(company);
        initializeGoogleBenefits(company);
    }

    private Company initializeGoogleData() {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.COMPANY)
                .username("google@gmail.com")
                .password("Google12345!")
                .build());

        CompanyLocation location = CompanyLocation.builder()
                .city("Novi Sad")
                .province("Vojvodina")
                .country("Serbia")
                .countryCode("RS")
                .address("Olge Petrov 27")
                .build();

        return companyRepository.save(Company.builder()
                .address1("San Francisco")
                .description("Nullam sit amet turpis elementum ligula vehicula consequat. Morbi a ipsum. Integer a nibh.")
                .name("Google")
                .location(location)
                .user(user)
                .build());
    }

    private void initializeGooglePosts(Company company) {
        postRepository.saveAll(Arrays.asList(
                Post.builder()
                        .description("Duis bibendum, felis sed interdum venenatis, turpis enim blandit mi, in porttitor pede justo eu massa.")
                        .url("http://dummyimage.com/136x100.png/ff4444/ffffff")
                        .country("Ghana")
                        .company(company)
                        .build(),
                Post.builder()
                        .description("Fusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl.")
                        .url("http://dummyimage.com/163x100.png/ff4444/ffffff")
                        .country("Indonesia")
                        .company(company)
                        .build(),
                Post.builder()
                        .description("Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis.")
                        .url("http://dummyimage.com/207x100.png/cc0000/ffffff")
                        .country("Argentina")
                        .company(company)
                        .build()));
    }

    private void initializeGoogleContacts(Company company) {
        contactRepository.saveAll(Arrays.asList(
                Contact.builder()
                        .type(ContactType.MOBILE_PHONE)
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
                        .value("http://ft.com/nibh/in/lectus/pellentesque/at.js?orci=tellus&pede=in&venenatis=sagittis")
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

    private void initializeFacebook() {
        Company company = initializeFacebookData();
        initializeFacebookPosts(company);
        initializeFacebookContacts(company);
        initializeFacebookBenefits(company);
    }

    private Company initializeFacebookData() {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.COMPANY)
                .username("facebook@facebook.com")
                .password("Facebook12345!")
                .build());

        CompanyLocation location = CompanyLocation.builder()
                .city("Moa")
                .province("Pinar del Río")
                .country("Cuba")
                .countryCode("CU")
                .address("1257 Vermont Parkway")
                .build();

        return companyRepository.save(Company.builder()
                .address1("Moa")
                .description("Nullam sit amet turpis elementum ligula vehicula consequat. Morbi a ipsum. Integer a nibh.")
                .name("Facebook")
                .location(location)
                .user(user)
                .build());
    }

    private void initializeFacebookPosts(Company company) {
        postRepository.saveAll(Arrays.asList(
                Post.builder()
                        .description("Integer ac leo. Pellentesque ultrices mattis odio. Donec vitae nisi.")
                        .url("http://dummyimage.com/190x100.png/dddddd/000000")
                        .country("Nigeria")
                        .company(company)
                        .build(),
                Post.builder()
                        .description("Proin interdum mauris non ligula pellentesque ultrices.")
                        .url("http://dummyimage.com/236x100.png/cc0000/ffffff")
                        .country("Japan")
                        .company(company)
                        .build(),
                Post.builder()
                        .description("Maecenas tristique, est et tempus semper, est quam pharetra magna, ac consequat metus sapien ut nunc.")
                        .url("http://dummyimage.com/146x100.png/5fa2dd/ffffff")
                        .country("China")
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
        initializeGoran();
    }


    private void initializeNikola() {
        Talent talent = initializeNikolaData();
        initializeNikolaContacts(talent);
        initializeNikolaAvailableLocations(talent);
        initializeNikolaPositions(talent);
        initializeNikolaTalentTerms(talent);
    }

    private Talent initializeNikolaData() {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.TALENT)
                .username("nikola@gmail.com")
                .password("Nikola12345!")
                .build());

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
                .currentLocation(currentLocation)
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
        locationRepository.saveAll(
                Arrays.asList(
                        Location.builder()
                                .country("United States")
                                .city("Dallas")
                                .province("Texas")
                                .countryCode("US")
                                .talent(talent)
                                .build(),
                        Location.builder()
                                .country("Bosnia and Herzegovina")
                                .city("Vareš")
                                .countryCode("BA")
                                .talent(talent)
                                .build()

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
                                .term(termRepository.getByCode("Salary"))
                                .build(),
                        TalentTerm.builder()
                                .value("20")
                                .negotiable(true)
                                .talent(talent)
                                .term(termRepository.getByCode("Holidays"))
                                .build()
                ));
    }

    private void initializeGoran() {
        Talent talent = initializeGoranData();
        initializeGoranContacts(talent);
        initializeGoranAvailableLocations(talent);
        initializeGoranPositions(talent);
        initializeGoranTalentTerms(talent);
    }

    private Talent initializeGoranData() {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.TALENT)
                .username("goransasic@gmail.com")
                .password("G0r@n123@1990")
                .build());

        Location currentLocation = Location.builder()
                .country("Costa Rica")
                .city("La Suiza")
                .province("Turrialba")
                .countryCode("CR")
                .build();

        return talentRepository.save(Talent.builder()
                .firstName("Goran")
                .lastName("Sasic")
                .dateOfBirth(LocalDate.of(1995, 1, 8))
                .user(user)
                .available(true)
                .availabilityChangeDate(Instant.now())
                .currentLocation(currentLocation)
                .build());
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
        locationRepository.saveAll(
                Arrays.asList(
                        Location.builder()
                                .country("Mexico")
                                .city("Parque Industrial")
                                .province("Chiapas")
                                .countryCode("MX")
                                .talent(talent)
                                .build(),
                        Location.builder()
                                .country("Sweden")
                                .city("Nol")
                                .countryCode("SE")
                                .talent(talent)
                                .build()

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
        talentTermRepository.saveAll(
                Arrays.asList(
                        TalentTerm.builder()
                                .value("5000")
                                .negotiable(true)
                                .talent(talent)
                                .term(termRepository.getByCode("Salary"))
                                .build(),
                        TalentTerm.builder()
                                .value("20")
                                .negotiable(true)
                                .talent(talent)
                                .term(termRepository.getByCode("Holidays"))
                                .build(),
                        TalentTerm.builder()
                                .value("true")
                                .negotiable(true)
                                .talent(talent)
                                .term(termRepository.getByCode("Parking"))
                                .build()
                ));
    }

}
