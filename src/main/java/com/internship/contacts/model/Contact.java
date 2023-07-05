package com.internship.contacts.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "contact")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
    private List<Email> emails;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
    private List<PhoneNumber> phoneNumbers;
}