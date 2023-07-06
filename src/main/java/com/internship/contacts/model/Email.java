package com.internship.contacts.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "email", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "contact_id"}))
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    private String email;
}
