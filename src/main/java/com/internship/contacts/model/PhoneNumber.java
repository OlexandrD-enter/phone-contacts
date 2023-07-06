package com.internship.contacts.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@Table(name = "phone_number", uniqueConstraints = @UniqueConstraint(columnNames = {"phoneNumber", "contact_id"}))
public class PhoneNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    private String phoneNumber;
}

