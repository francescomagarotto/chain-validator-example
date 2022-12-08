package com.francescomagarotto.chainvalidator;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
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
				.chain(null, Objects::nonNull));
	}

	/**
	 * Objects.requireNonNull throws a NullPointerException,
	 * but as params are annotated with org.jetbrains.annotations.NotNull an IllegalArgumentException is thrown instead
	 */
	@Test
	public void invalidPredicate() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> ValidatorChain.of(person)
				.chain(p -> p.name, null));
	}

	@Test
	public void of() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person).buildChain();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertTrue(validatorChain.validate());
	}

	@Test
	public void chainOfOne_true() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.chain(p -> p.name, name -> name.equals("Mario"))
				.buildChain();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertTrue(validatorChain.validate());
	}

	@Test
	public void chainOfOne_false() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.chain(p -> p.name, name -> name.equals("Mauro"))
				.buildChain();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertFalse(validatorChain.validate());
	}

	@Test
	public void chainOfMore_true() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.chain(p -> p.name, name -> name.equals("Mario"))
				.chain(p -> p.surname, surname -> surname.equals("Rossi"))
				.buildChain();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertTrue(validatorChain.validate());
	}

	@Test
	public void chainOfMore_false() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.chain(p -> p.name, name -> name.equals("Mauro"))
				.chain(p -> p.surname, surname -> surname.equals("Bianchi"))
				.buildChain();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertFalse(validatorChain.validate());
	}

	@Test
	public void chainOfMore_mixed() {
		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.chain(p -> p.name, name -> name.equals("Mario"))
				.chain(p -> p.surname, surname -> surname.equals("Bianchi"))
				.buildChain();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertFalse(validatorChain.validate());
	}

	@Test
	public void chainOfMore_mixed_withErrorMessage() {
		initLogger();

		final ValidatorChain<Person> validatorChain = ValidatorChain.of(person)
				.chain(p -> p.name, name -> name.equals("Mario"))
				.chain(p -> p.surname, surname -> surname.equals("Rossi"), "Invalid surname")
				.chain(p -> p.age, age -> age > 18)
				.chain(p -> p.developer, developer -> developer, "The person must be a developer!")
				.buildChain();

		Assertions.assertNotNull(validatorChain);
		Assertions.assertFalse(validatorChain.validate());
		Assertions.assertTrue(memoryAppender.contains("The person must be a developer!", Level.ERROR));
	}

	private void initLogger() {
		final Logger logger = (Logger) LoggerFactory.getLogger(ValidatorChain.class);
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