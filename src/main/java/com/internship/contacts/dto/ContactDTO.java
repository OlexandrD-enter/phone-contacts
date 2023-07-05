package com.internship.contacts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDTO {
    private Long id;
    private String name;
    private Long userId;
    private List<String> emails;
    private List<String> phoneNumbers;
}
