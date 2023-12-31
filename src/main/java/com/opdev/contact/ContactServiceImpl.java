package com.opdev.contact;

import com.opdev.company.service.CompanyService;
import com.opdev.exception.ApiContactEditValidationException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.company.Company;
import com.opdev.model.contact.Contact;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.User;
import com.opdev.repository.ContactRepository;
import com.opdev.talent.TalentService;
import com.opdev.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository repository;

    private final UserService userService;

    private final TalentService talentService;

    private final CompanyService companyService;

    @Override
    @Transactional
    public List<Contact> add(final List<Contact> newContacts, final String username) {
        List<Contact> created = new ArrayList<>();
        newContacts.forEach(newContact -> created.add(add(newContact, username)));
        return created;
    }

    @Override
    @Transactional
    public List<Contact> edit(final List<Contact> modified, final String username) {
        List<Contact> foundForUsername = findAll(username);
        repository.deleteAll(foundForUsername);
        modified.forEach(contact -> {
            setTalentOrCompany(username, contact);
        });
        return repository.saveAll(modified);
    }

    @Override
    @Transactional
    public void remove(Long id, final String username) {
        Objects.requireNonNull(id);
        Contact found = get(id);
        validate(found, username);
        repository.deleteById(id);
        LOGGER.info("Contact with id {} is removed", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findAll(String username) {
        Objects.requireNonNull(username);
        Optional<Talent> foundTalent = talentService.findByUsername(username);
        if (foundTalent.isPresent()) {
            return repository.findByTalentId(foundTalent.get().getId());
        }
        Company foundCompany = companyService.getByUsername(username);
        return repository.findByCompanyId(foundCompany.getId());
    }

    private Contact add(Contact newContact, String username) {
        Objects.requireNonNull(newContact);
        setTalentOrCompany(username, newContact);
        final User loggedUser = userService.getLoggedInUser();
        loggedUser.setCreatedBy(loggedUser);
        loggedUser.setModifiedBy(loggedUser);
        final Contact created = repository.save(newContact);
        LOGGER.info("New contact {} has been added.", created);
        return created;
    }

    private Contact get(Long id) {
        Objects.requireNonNull(id);
        return repository.findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Contact")
                .id(id.toString()).build());
    }

    private void setTalentOrCompany(String username, Contact newContact) {
        Objects.requireNonNull(username);
        Optional<Talent> talent = talentService.findByUsername(username);
        if (talent.isPresent()) {
            newContact.setTalent(talent.get());
        } else {
            Company company = companyService.getByUsername(username);
            newContact.setCompany(company);
        }
    }

    private void validate(Contact old, Contact modified) {
        Objects.requireNonNull(old);
        Objects.requireNonNull(modified);
        if ((modified.getCompany() != null && old.getCompany() == null) || (modified.getCompany() == null && old.getCompany() != null)) {
            throw new ApiContactEditValidationException("Requested change of contact is not possible because sender is not owner of the contact");
        }
        if ((modified.getTalent() != null && old.getTalent() == null) || (modified.getTalent() == null && old.getTalent() != null)) {
            throw new ApiContactEditValidationException("Requested change of contact is not possible because sender is not owner of the contact");
        }
        if (modified.getCompany() != null && old.getCompany() != null && !old.getCompany().getId().equals(modified.getCompany().getId())) {
            throw new ApiContactEditValidationException("Requested change of contact is not possible because sender is not owner of the contact");
        }
        if (modified.getTalent() != null && old.getTalent() != null && !old.getTalent().getId().equals(modified.getTalent().getId())) {
            throw new ApiContactEditValidationException("Requested change of contact is not possible because sender is not owner of the contact");
        }
    }

    private void validate(Contact contact, String username) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(contact);

        Optional<Talent> talent = talentService.findByUsername(username);
        if (talent.isPresent()) {
            if (contact.getTalent() != null && !contact.getTalent().getId().equals(talent.get().getId())) {
                throw new ApiContactEditValidationException("Requested action is not possible because sender is not owner of the contact");
            }
        } else {
            Company company = companyService.getByUsername(username);
            if (contact.getCompany() != null && !contact.getCompany().getId().equals(company.getId())) {
                throw new ApiContactEditValidationException("Requested change of contact is not possible because sender is not owner of the contact");
            }
        }
    }
}
