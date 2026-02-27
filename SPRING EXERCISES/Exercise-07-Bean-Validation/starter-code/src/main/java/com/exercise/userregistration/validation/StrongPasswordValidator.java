package com.exercise.userregistration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

// TODO 9: Implement ConstraintValidator<StrongPassword, String>
//         ConstraintValidator<A, T> means: validates type T using annotation A
//
// TODO 10: Implement isValid(String value, ConstraintValidatorContext context):
//          - If value is null: return true (let @NotBlank handle null — avoid double messages)
//          - Use this regex pattern to validate: 
//            ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$
//          Explanation:
//            (?=.*[a-z])     — at least one lowercase letter
//            (?=.*[A-Z])     — at least one uppercase letter
//            (?=.*\d)        — at least one digit
//            (?=.*[@$!%*?&]) — at least one special character
//            {8,}            — minimum 8 characters total
public class StrongPasswordValidator {

    // TODO 9: implement ConstraintValidator<StrongPassword, String>

    // TODO 10: implement isValid
}
