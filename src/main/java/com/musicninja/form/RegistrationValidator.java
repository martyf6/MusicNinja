package com.musicninja.form;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class RegistrationValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Registration.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Registration registration = (Registration) obj;
	    
	    if (!(registration.getPassword()).equals(registration.getConfirmPassword())) {
	      errors.rejectValue("confirmPassword", "confirmPassword.notEqual");
	    }
	}

}
