package com.internship.contacts.utils.annotation;

import com.internship.contacts.utils.ValidEmailListValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmailListValidator.class)
public @interface ValidEmailList {
    String message() default "Invalid email address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
