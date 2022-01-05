package com.opdev.repository;

import java.util.List;

import com.opdev.model.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByCompanyId(Long id);

    List<Contact> findByTalentId(Long id);

}
