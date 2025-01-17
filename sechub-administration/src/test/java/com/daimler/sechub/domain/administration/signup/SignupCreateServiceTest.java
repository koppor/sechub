// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.administration.signup;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.daimler.sechub.domain.administration.user.UserRepository;
import com.daimler.sechub.sharedkernel.messaging.DomainMessage;
import com.daimler.sechub.sharedkernel.messaging.DomainMessageService;
import com.daimler.sechub.sharedkernel.messaging.MessageDataKeys;
import com.daimler.sechub.sharedkernel.messaging.UserMessage;

public class SignupCreateServiceTest {

	private AnonymousSignupCreateService serviceToTest;
	private DomainMessageService mockedEventBusService;

	@Before
	public void before() {
		mockedEventBusService = mock(DomainMessageService.class);

		serviceToTest = new AnonymousSignupCreateService();
		serviceToTest.eventBusService =mockedEventBusService;
		serviceToTest.userRepository = mock(UserRepository.class);
		serviceToTest.userSelfRegistrationRepository = mock(SignupRepository.class);
	}

	@Test
	public void a_created_signup_sends_event_containing_userid_and_email() {
		/* prepare */
		SignupJsonInput userSelfRegistrationInput = mock(SignupJsonInput.class);
		when(userSelfRegistrationInput.getUserId()).thenReturn("schlaubi");
		when(userSelfRegistrationInput.getEmailAdress()).thenReturn("schlaubi@schlumpfhausen.de");

		/* execute */
		serviceToTest.register(userSelfRegistrationInput);

		/* test */
		ArgumentCaptor<DomainMessage> domainMessageCaptor = ArgumentCaptor.forClass(DomainMessage.class);
		verify(mockedEventBusService).sendAsynchron(domainMessageCaptor.capture());

		DomainMessage messageSendByService = domainMessageCaptor.getValue();
		assertNotNull("no message send!", messageSendByService);
		UserMessage signupDataInMessage = messageSendByService.get(MessageDataKeys.USER_SIGNUP_DATA);
		assertNotNull("no signup data inside message!", signupDataInMessage);
		// check event contains expected data
		assertEquals("schlaubi", signupDataInMessage.getUserId());
		assertEquals("schlaubi@schlumpfhausen.de", signupDataInMessage.getEmailAdress());
	}

}
