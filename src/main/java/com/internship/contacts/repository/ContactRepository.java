package com.internship.contacts.repository;

import com.internship.contacts.model.Contact;
import com.internship.contacts.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUser_Username(String username);
    Optional<Contact> findByName(String name);
    Optional<Contact> findByNameAndUserUsername(String name, String username);
}

