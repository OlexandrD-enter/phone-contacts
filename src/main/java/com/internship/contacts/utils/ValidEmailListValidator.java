package com.internship.contacts.utils;

import com.internship.contacts.utils.annotation.ValidEmailList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidEmailListValidator implements ConstraintValidator<ValidEmailList, List<String>> {
    @Override
    public boolean isValid(List<String> emails, ConstraintValidatorContext context) {
        if (emails == null || emails.isEmpty()) {
            return true;
        }
        for (String email : emails) {
            if (!isValidEmail(email)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }
}
