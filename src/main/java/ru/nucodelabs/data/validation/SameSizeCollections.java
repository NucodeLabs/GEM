package ru.nucodelabs.data.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Все поля типа {@code List}, названия которых перечислены в {@code value} должны иметь
 * один и тот же размер
 */
@Constraint(validatedBy = SameSizeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SameSizeCollections {

    String[] value();

    boolean nonEmptyOnly() default true;

    String message() default "Количество строк в данных неравное";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
