package com.internship.contacts.utils;

import com.internship.contacts.utils.annotation.ValidPhoneList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidPhoneListValidator implements ConstraintValidator<ValidPhoneList, List<String>> {
    @Override
    public boolean isValid(List<String> phoneNumbers, ConstraintValidatorContext context) {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return true;
        }
        for (String phoneNumber : phoneNumbers) {
            if (!isValidPhoneNumber(phoneNumber)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+?3?8?(0\\d{9})$";
        return phoneNumber.matches(phoneRegex);
    }
}

