package com.francescomagarotto.chainvalidator.validators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("rawtypes")
public class ValidatorChain<E> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ValidatorChain.class);
    private final List<Validator> validators;
    private final E entity;

    private ValidatorChain(E entity, List<Validator> validators) {
        this.entity = entity;
        this.validators = validators;
    }

    /**
     * Creates a new chain of validators
     * @param entity the domain element and input type for extractor functions.
     * @return a new {@see Chain} of validators for the domain element.
     * @param <E> the type of the domain element
     */
    public static <E> Chain<E> of(E entity) {
        return new Chain<>(entity);
    }

    /**
     * Applies the extractor function and then tests the predicate
     * for each element in the chain.
     * @return true if all predicates return true, otherwise false.
     */
    @SuppressWarnings("unchecked")
    public boolean validate() {
        boolean valid = true;
        Iterator<Validator> valdidatorsIterator = validators.iterator();
        while (valdidatorsIterator.hasNext() && valid) {
            Validator validator = valdidatorsIterator.next();
            valid = validator.predicate.test(validator.extractor.apply(entity));
            if (!valid && validator.errorMessage != null) {
                LOGGER.error(validator.errorMessage);
            }
        }
        return valid;
    }

    public static class Chain<E> {
        private final E entity;
        private final List<Validator> validators;

        public Chain(@NotNull E entity) {
            Objects.requireNonNull(entity);
            this.entity = entity;
            this.validators = new LinkedList<>();
        }

        /**
         * Links an element in the chain
         * @param predicate the predicate to test
         * @param extractor the function that extracts the object from the domain element to pass into predicate
         * @return the Chain instance
         * @param <O> the extractor function return type
         */
        public <O> Chain<E> link(@NotNull Function<E, O> extractor,
                                  @NotNull Predicate<O> predicate) {
            validators.add(new Validator<>(extractor, predicate));
            return this;
        }

        /**
         * Links an element in the chain
         * @param predicate the predicate to test
         * @param extractor the function that extracts the object from the domain element to pass into predicate
         * @param errorMessage the error message to print in case the predicate test returns false
         * @return the Chain instance
         * @param <O> the transform function return type
         */
        public <O> Chain<E> link(@NotNull Function<E, O> extractor,
                                  @NotNull Predicate<O> predicate,
                                  @Nullable String errorMessage) {
            validators.add(new Validator<>(extractor, predicate, errorMessage));
            return this;
        }

        /**
         * Bond the chain
         * @return a new ValidatorChain instance
         */
        public ValidatorChain<E> bond() {
            return new ValidatorChain<>(entity, validators);
        }

    }

    public static class Validator<I, O> {
        private static final String EXTRACTOR_MUST_NOT_BE_NULL = "Extractor must not be null";
        private static final String PREDICATE_MUST_NOT_BE_NULL = "Predicate must not be null";
        private final Function<I, O> extractor;
        private final Predicate<O> predicate;
        @Nullable private final String errorMessage;

        public Validator(@NotNull Function<I, O> extractor, @NotNull Predicate<O> predicate) {
            this(extractor, predicate, null);
        }

        public Validator(
                @NotNull Function<I, O> extractor,
                @NotNull Predicate<O> predicate,
                @Nullable String errorMessage) {
            Objects.requireNonNull(extractor, EXTRACTOR_MUST_NOT_BE_NULL);
            Objects.requireNonNull(predicate, PREDICATE_MUST_NOT_BE_NULL);
            this.extractor = extractor;
            this.predicate = predicate;
            this.errorMessage = errorMessage;
        }
    }

}
