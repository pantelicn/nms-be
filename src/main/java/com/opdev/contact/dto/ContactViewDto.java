package com.opdev.contact.dto;

import java.util.Objects;

import com.opdev.model.contact.Contact;
import com.opdev.model.contact.ContactType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContactViewDto {

    private Long id;

    private ContactType type;

    private String value;

    public ContactViewDto(Contact contact) {
        Objects.requireNonNull(contact);

        id = contact.getId();
        type = contact.getType();
        value = contact.getValue();
    }

}
