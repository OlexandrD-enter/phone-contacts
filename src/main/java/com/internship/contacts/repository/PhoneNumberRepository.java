package com.internship.contacts.repository;

import com.internship.contacts.model.Contact;
import com.internship.contacts.model.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
    List<PhoneNumber> findByContact(Contact contact);
    PhoneNumber findByPhoneNumberAndContact(String phoneNumber, Contact contact);
}