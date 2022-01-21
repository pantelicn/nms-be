package com.opdev.test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import com.opdev.common.services.Profiles;
import com.opdev.model.company.Benefit;
import com.opdev.model.company.Company;
import com.opdev.model.company.Post;
import com.opdev.model.location.CompanyLocation;
import com.opdev.model.location.Location;
import com.opdev.model.talent.*;
import com.opdev.model.term.TalentTerm;
import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import com.opdev.model.user.Setting;
import com.opdev.model.user.Setting.SettingBuilder;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.repository.*;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Profile(Profiles.DEV_PROFILE)
class TestDataServiceImpl implements TestDataService {

        private final PasswordEncoder passwordEncoder;
        private final UserRepository userRepository;
        private final SettingRepository settingRepository;
        private final TalentRepository talentRepository;
        private final LocationRepository locationRepository;
        private final CompanyRepository companyRepository;
        private final PositionRepository positionRepository;
        private final TalentPositionRepository talentPositionRepository;
        private final SkillRepository skillRepository;
        private final PositionSkillRepository positionSkillRepository;
        private final BenefitRepository benefitRepository;
        private final TermRepository termRepository;
        private final TalentTermRepository talentTermRepository;
        private final PostRepository postRepository;

        @Transactional
        @Override
        public void insert() {

                final User userTotti = User.builder().username("500-matora@sindikat.is")
                                .enabled(Boolean.TRUE)
                                .type(UserType.TALENT).build();
                final User userGruja = User.builder().username("lav-pivo@sindikat.ba")
                                .enabled(Boolean.TRUE)
                                .type(UserType.TALENT).build();
                final User userMikica = User.builder().username("kafa@gmail.com")
                                .enabled(Boolean.TRUE).type(UserType.TALENT)
                                .build();
                final User userSindikat = User.builder().username("zavisnost@sindikat.rs")
                                .enabled(Boolean.TRUE)
                                .type(UserType.COMPANY).build();
                final User userPantela = User.builder().username("nikola@pantelic.rs")
                                .enabled(Boolean.TRUE)
                                .type(UserType.COMPANY).build();
                final List<User> users = Arrays.asList(userTotti, userGruja, userMikica, userSindikat, userPantela);
                users.forEach(userRepository::save);
                final User userGoran = userRepository.findByUsername("gox69@opdev.rs")
                                .orElseThrow(() -> new RuntimeException("The user is not found."));
                final User userNikola = userRepository.findByUsername("znikola@xxx.xxx")
                                .orElseThrow(() -> new RuntimeException("The user is not found."));

                final SettingBuilder settingBuilder = Setting.builder().name("language").value("en");
                final Setting tottiSetting = settingBuilder.user(userTotti).build();
                final Setting grujaSetting = settingBuilder.user(userGruja).build();
                final Setting mikicaSetting = settingBuilder.user(userMikica).build();
                final Setting goranSetting = settingBuilder.user(userGoran).build();
                final Setting nikolaSetting = settingBuilder.user(userNikola).build();
                final Setting sindikatSetting = settingBuilder.user(userSindikat).build();
                Arrays.asList(tottiSetting, grujaSetting, mikicaSetting, goranSetting, nikolaSetting, sindikatSetting)
                                .forEach(settingRepository::save);

                userTotti.getSettings().add(tottiSetting);
                userGruja.getSettings().add(grujaSetting);
                userMikica.getSettings().add(mikicaSetting);
                userGoran.getSettings().add(goranSetting);
                userNikola.getSettings().add(nikolaSetting);
                userSindikat.getSettings().add(sindikatSetting);
                users.forEach(userRepository::save);

                final Talent talentTotti = Talent.builder().firstName("Frančesko").middleName("Roma").lastName("Totti")
                                .user(userTotti).available(Boolean.TRUE)
                                .dateOfBirth(LocalDate.of(1980, Month.JANUARY, 15))
                                .availabilityChangeDate(Instant.now()).build();
                final Talent talentGruja = Talent.builder().firstName("Gruja").middleName("Sindikat")
                                .lastName("Direktor").user(userGruja).available(Boolean.TRUE)
                                .dateOfBirth(LocalDate.of(1981, Month.FEBRUARY, 15))
                                .availabilityChangeDate(Instant.now()).build();
                final Talent talentMikica = Talent.builder().firstName("Mikica").middleName("Lepak").lastName("Kafa")
                                .user(userMikica).available(Boolean.FALSE)
                                .dateOfBirth(LocalDate.of(1982, Month.MARCH, 15)).availabilityChangeDate(Instant.now())
                                .build();
                final Talent talentGoran = Talent.builder().firstName("Goran").middleName("xxx").lastName("Šašić")
                                .user(userGoran).available(Boolean.TRUE)
                                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1)).availabilityChangeDate(Instant.now())
                                .build();
                final Talent talentNikola = Talent.builder().firstName("Nikola").middleName("xxx").lastName("Zarić")
                                .user(userNikola).available(Boolean.TRUE)
                                .dateOfBirth(LocalDate.of(1991, Month.MARCH, 10)).availabilityChangeDate(Instant.now())
                                .build();
                Arrays.asList(talentTotti, talentGruja, talentMikica, talentGoran, talentNikola)
                                .forEach(talentRepository::save);

                final Location locationIsland = Location.builder().city("Reykjavik").country("Iceland")
                                .talent(talentTotti).countryCode("is").build();
                final Location locationNoviSad = Location.builder().city("Novi Sad").country("Serbia").countryCode("rs")
                                .talent(talentGruja).build();
                final Location locationMontreal = Location.builder().city("Montreal").country("Canada").province("QC")
                                .talent(talentNikola).countryCode("ca").build();
                Arrays.asList(locationIsland, locationNoviSad, locationMontreal).forEach(locationRepository::save);

                // TODO: update the location in the talents...

                final CompanyLocation companyLocation = CompanyLocation.builder().city("Novi sad")
                        .country("Republika Srbija").province("Vojvodina").countryCode("007").build();
                final Company companySindikat = Company.builder().user(userSindikat).name("Sindikat Zavisnosti")
                                .description("Flour for all.").address1("Mikica's apartment")
                                .location(companyLocation).build();
                final Company companyPantela = Company.builder().user(userPantela).name("Pantela")
                                .description("No description.").address1("Temerinska 1").location(companyLocation)
                                .build();
                Arrays.asList(companySindikat, companyPantela).forEach(companyRepository::save);

                final Position frontendDev = Position.builder().code("FRONTEND_DEV")
                                .description("Frontend developer for IT").name("Frontend developer").build();

                positionRepository.save(frontendDev);

                final TalentPosition goranPosition = TalentPosition.builder().position(frontendDev).talent(talentGoran)
                                .build();

                final TalentPosition nikolaPosition = TalentPosition.builder().position(frontendDev)
                                .talent(talentNikola).build();

                talentPositionRepository.save(goranPosition);
                talentPositionRepository.save(nikolaPosition);

                final Skill angular = Skill.builder().name("Angular").code("ANGULAR").status(SkillStatus.APPROVED).build();
                final Skill react = Skill.builder().name("React").code("REACT").status(SkillStatus.APPROVED).build();
                skillRepository.save(angular);
                skillRepository.save(react);

                final PositionSkill frontendAngular = PositionSkill.builder().position(frontendDev).skill(angular).build();
                final PositionSkill frontendReact = PositionSkill.builder().position(frontendDev).skill(react).build();

                positionSkillRepository.save(frontendAngular);
                positionSkillRepository.save(frontendReact);

                final Benefit remote = Benefit.builder().name("Remote work").description("Unlimited remote working options.")
                        .isDefault(true).company(companySindikat).build();
                final Benefit insurance = Benefit.builder().name("Health insurance").description("Basic health insurance plans covered by the company.")
                        .isDefault(true).company(companySindikat).build();

                benefitRepository.save(remote);
                benefitRepository.save(insurance);

                final Term remoteTerm = Term.builder().code("REMOTE").name("Remote").description("Remote work").name("Remote").type(TermType.BOOLEAN).build();
                final Term salaryTerm = Term.builder().code("MIN_SALARY").name("Minimum salary").description("Minimum salary").type(TermType.INT).build();

                termRepository.save(remoteTerm);
                termRepository.save(salaryTerm);

                final TalentTerm goranRemote = TalentTerm.builder().talent(talentGoran).term(remoteTerm).negotiable(true).value("true").build();
                final TalentTerm goranSalary = TalentTerm.builder().talent(talentGoran).term(salaryTerm).negotiable(true).value("8000").build();
                final TalentTerm nikolaSalary = TalentTerm.builder().talent(talentNikola).term(salaryTerm).negotiable(true).value("2000").build();

                talentTermRepository.save(goranRemote);
                talentTermRepository.save(goranSalary);
                talentTermRepository.save(nikolaSalary);

                final Post pantelaPost = Post.builder().company(companyPantela).country("Serbia").description("Live Laugh Learn.").build();

                List.of(pantelaPost).forEach(postRepository::save);

        }

}
