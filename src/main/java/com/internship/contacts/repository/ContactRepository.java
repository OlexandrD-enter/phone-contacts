package com.internship.contacts.repository;

import com.internship.contacts.dto.ContactDTO;
import com.internship.contacts.model.Contact;
import com.internship.contacts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUser_Username(String username);
    Contact findByNameAndUser(String name, User user);
}

