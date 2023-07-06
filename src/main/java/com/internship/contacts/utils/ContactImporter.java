package com.internship.contacts.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.contacts.dto.ContactDTO;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ContactImporter {
    public static List<ContactDTO> importFromJson(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = file.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<List<ContactDTO>>() {});
        }
    }
}
