package com.francescomagarotto.chainvalidator;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.francescomagarotto.chainvalidator.validators.Validator;
import com.francescomagarotto.chainvalidator.validators.ValidatorChain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ValidatorChainTest {

	private Person person;
	private MemoryAppender memoryAppender;

	@BeforeEach
	public void setUp() {
		person = new Person();
		person.name = "Mario";
		person.surname = "Rossi";
		person.age = 25;
		person.developer = false;
	}

	/**
	 * Objects.requireNonNull throws a NullPointerException,
	 * but as params are annotated with org.jetbrains.annotations.NotNull an IllegalArgumentException is thrown instead
	 */
	@Test
	public void invalidEntity() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> ValidatorChain.of(null));
	}

	/**
	 * Objects.requireNonNull throws a NullPointerException,
	 * but as params are annotated with org.jetbrains.annotations.NotNull an IllegalArgumentException is thrown instead
	 */
	@Test
	public void invalidExtractor() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> ValidatorChain.of(person)
				.link(null, Objects::nonNull));
	}

	/**
	 * Objects.requireNonNull throws a NullPointerException,
	 * but as params are annotated with org.jetbrains.annotations.NotNull an IllegalArgumentException is thrown instead
	 */
	@Test
	public void invalidPredicate() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> ValidatorChain.of(person)
				.link(p -> p.name, null));
	}

	@Test
	public void of() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person).bond();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertTrue(validatorChain.validate());
	}

	@Test
	public void linkOfOneTrue() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.link(p -> p.name, name -> name.equals("Mario"))
				.bond();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertTrue(validatorChain.validate());
	}

	@Test
	public void linkOfOneFalse() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.link(p -> p.name, name -> name.equals("Mauro"))
				.bond();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertFalse(validatorChain.validate());
	}

	@Test
	public void linkOfMoreTrue() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.link(p -> p.name, name -> name.equals("Mario"))
				.link(p -> p.surname, surname -> surname.equals("Rossi"))
				.bond();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertTrue(validatorChain.validate());
	}

	@Test
	public void linkOfMoreFalse() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.link(p -> p.name, name -> name.equals("Mauro"))
				.link(p -> p.surname, surname -> surname.equals("Bianchi"))
				.bond();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertFalse(validatorChain.validate());
	}

	@Test
	public void linkOfMoreMixed() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.link(p -> p.name, name -> name.equals("Mario"))
				.link(p -> p.surname, surname -> surname.equals("Bianchi"))
				.bond();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertFalse(validatorChain.validate());
	}

	@Test
	public void linkOfMoreMixedWithErrorMessage() {
		initLogger();

		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.link(p -> p.name, name -> name.equals("Mario"))
				.link(p -> p.surname, surname -> surname.equals("Rossi"), "Invalid surname")
				.link(p -> p.age, age -> age > 18)
				.link(p -> p.developer, developer -> developer, "The person must be a developer!")
				.bond();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertFalse(validatorChain.validate());
		Assertions.assertTrue(memoryAppender.contains("The person must be a developer!", Level.ERROR));
	}

	private void initLogger() {
		final Logger logger = (Logger) LoggerFactory.getLogger(Validator.class);
		memoryAppender = new MemoryAppender();
		memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		logger.addAppender(memoryAppender);
		memoryAppender.start();
	}

	private static class Person {
		public String name;
		public String surname;
		public Integer age;
		public Boolean developer;
	}
}