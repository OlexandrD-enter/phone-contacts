package com.internship.contacts.dto;

import com.internship.contacts.utils.annotation.ValidEmailList;
import com.internship.contacts.utils.annotation.ValidPhoneList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDTO {
    private String name;
    @NotEmpty
    @ValidEmailList(message = "Invalid email address")
    private List<String> emails;
    @NotEmpty
    @ValidPhoneList(message = "Invalid phone number")
    private List<String> phoneNumbers;
    private MultipartFile imageFile;
}
