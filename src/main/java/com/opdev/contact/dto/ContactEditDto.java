package com.opdev.contact.dto;

import com.opdev.model.contact.Contact;
import com.opdev.model.contact.ContactType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RequiredArgsConstructor
public class ContactEditDto {

    @NonNull
    private ContactType type;

    @NonNull
    private String value;

    public Contact asContact() {
        return Contact.builder()
                .type(type)
                .value(value)
                .build();
    }

}
