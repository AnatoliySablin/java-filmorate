package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
@Documented
public @interface ValidReleaseDate {
    String message() default "Дата релиза не может быть раньше 28.12.1895 года";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}