package com.opdev.contact;

import com.opdev.model.contact.Contact;

import java.util.List;

public interface ContactService {

    List<Contact> add(List<Contact> newContacts, String username);

    List<Contact> edit(List<Contact> modified, String username);

    void remove(Long id, String username);

    List<Contact> findAll(String username);

}
