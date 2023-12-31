package com.internship.contacts.repository;

import com.internship.contacts.model.Contact;
import com.internship.contacts.model.Email;
import com.internship.contacts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findAllByContactUser(User user);
    void deleteAllByContact(Contact contact);
}
