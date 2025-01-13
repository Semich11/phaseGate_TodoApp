package academy.learnprogramming.validators;

import academy.learnprogramming.data.model.Users;
import academy.learnprogramming.exceptions.ObjectNotValidException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ObjectValidator<T> {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public void validate (T objectToValidate){
        Set<ConstraintViolation<T>> violation = validator.validate(objectToValidate);
        violation.forEach(v -> System.out.println("\n\n\n\n\n\n\n\n" + v.getPropertyPath() + ": " + v.getMessage() + "\n\n\n\n\n\n\n\n"));

        if (!violation.isEmpty()) {
            var errorMessages = violation
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
            throw new ObjectNotValidException(errorMessages);
        }
    }
}
