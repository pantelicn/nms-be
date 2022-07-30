package com.opdev.contact;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.config.security.Roles;
import com.opdev.contact.dto.ContactAddDto;
import com.opdev.contact.dto.ContactEditDto;
import com.opdev.contact.dto.ContactViewDto;
import com.opdev.model.contact.Contact;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/contacts")
public class ContactController {

    private final ContactService service;

    @PostMapping
    @PreAuthorize("(hasAnyRole('" + Roles.COMPANY + "', '" + Roles.TALENT + "'))")
    public ResponseEntity<List<ContactViewDto>> add(@Valid @RequestBody List<ContactAddDto> newContactDtos, Principal user) {
        final List<Contact> newContacts = newContactDtos.stream().map(ContactAddDto::asContact).collect(Collectors.toList());
        final List<Contact> created = service.add(newContacts, user.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created.stream().map(ContactViewDto::new).collect(Collectors.toList()));
    }

    @PutMapping
    @PreAuthorize("(hasAnyRole('" + Roles.COMPANY + "', '" + Roles.TALENT + "'))")
    public ResponseEntity<List<ContactViewDto>> edit(@Valid @RequestBody List<ContactEditDto> newContactDtos, Principal user) {
        final List<Contact> newContacts = newContactDtos.stream().map(ContactEditDto::asContact).collect(Collectors.toList());
        final List<Contact> updated = service.edit(newContacts, user.getName());
        return ResponseEntity.ok(updated.stream().map(ContactViewDto::new).collect(Collectors.toList()));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("(hasAnyRole('" + Roles.COMPANY + "', '" + Roles.TALENT + "'))")
    public ResponseEntity<Void> delete(@PathVariable  Long id, Principal user) {
        service.remove(id, user.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("(hasAnyRole('" + Roles.COMPANY + "', '" + Roles.TALENT + "'))")
    public ResponseEntity<List<ContactViewDto>> findAll(Principal user) {
        List<Contact> found = service.findAll(user.getName());
        final List<ContactViewDto> response = found.stream().map(ContactViewDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

}
