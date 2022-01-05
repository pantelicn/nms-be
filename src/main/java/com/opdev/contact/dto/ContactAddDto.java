package com.opdev.contact.dto;

import com.opdev.model.contact.Contact;
import com.opdev.model.contact.ContactType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class ContactAddDto {

    @NonNull
    private ContactType type;

    @NonNull
    private String value;

    public Contact asContact() {
        return Contact.builder()
                .value(value)
                .type(type)
                .build();
    }

}
