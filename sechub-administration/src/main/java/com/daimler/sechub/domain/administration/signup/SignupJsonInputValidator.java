// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.administration.signup;

import static com.daimler.sechub.domain.administration.signup.SignupJsonInput.*;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.daimler.sechub.sharedkernel.validation.ApiVersionValidation;
import com.daimler.sechub.sharedkernel.validation.UserIdValidation;
import com.daimler.sechub.sharedkernel.validation.ValidationResult;

@Component
public class SignupJsonInputValidator implements Validator {
	private static final Logger LOG = LoggerFactory.getLogger(SignupJsonInputValidator.class);

	@Autowired
	UserIdValidation useridValidation;

	EmailValidator emailValidator = EmailValidator.getInstance();

	@Autowired
	ApiVersionValidation apiVersionValidation;

	@Override
	public boolean supports(Class<?> clazz) {
		return SignupJsonInput.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SignupJsonInput selfRegistration = (SignupJsonInput) target;
		LOG.debug("Start validation for self registration of: {}", selfRegistration.getUserId());

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, PROPERTY_API_VERSION, "field.required");

		ValidationResult apiVersionValidationResult = apiVersionValidation.validate(selfRegistration.getApiVersion());
		if (!apiVersionValidationResult.isValid()) {
			errors.rejectValue(PROPERTY_API_VERSION, "api.error.unsupported.version",
					apiVersionValidationResult.getErrorDescription());
			return;
		}
		ValidationResult userIdValidationResult = useridValidation.validate(selfRegistration.getUserId());
		if (!userIdValidationResult.isValid()) {
			errors.rejectValue(PROPERTY_USER_ID, "api.error.registration.userid.invalid",
					userIdValidationResult.getErrorDescription());
			return;
		}

		if (!emailValidator.isValid(selfRegistration.getEmailAdress())) {
			errors.rejectValue(PROPERTY_EMAIL_ADRESS, "api.error.email.invalid",
					"Invalid email adress");
			return;
		}
		LOG.debug("Selfregistration of {} was accepted", selfRegistration.getUserId());

	}


}
