package com.opdev.contact;

import com.opdev.model.contact.Contact;

import java.util.List;

public interface ContactService {

    Contact add(Contact newContact, String username);

    Contact edit(Contact modified, String username);

    void remove(Long id, String username);

    List<Contact> findAll(String username);

}
