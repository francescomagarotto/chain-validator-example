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

public class ValidatorChain<E> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ValidatorChain.class);
    private final List<Validator<E, ?>> validators;
    private final E entity;

    private ValidatorChain(E entity, List<Validator<E, ?>> validators) {
        this.entity = entity;
        this.validators = validators;
    }

    /**
     * Creates a new chain of validators
     *
     * @param entity the domain element and input type for extractor functions.
     * @param <E>    the type of the domain element
     * @return a new {@see Chain} of validators for the domain element.
     */
    public static <E> Chain<E> of(E entity) {
        return new Chain<>(entity);
    }

    /**
     * Applies the extractor function and then tests the predicate
     * for each element in the chain.
     *
     * @return true if all predicates return true, otherwise false.
     */
    public boolean validate() {
        boolean valid = true;
        Iterator<Validator<E, ?>> valdidatorsIterator = validators.iterator();
        while (valdidatorsIterator.hasNext() && valid) {
            Validator<E, ?> validator = valdidatorsIterator.next();
            valid = validator.valid(entity);
        }
        return valid;
    }

    public static class Chain<E> {
        private final E entity;
        private final List<Validator<E, ?>> validators;

        public Chain(@NotNull E entity) {
            Objects.requireNonNull(entity);
            this.entity = entity;
            this.validators = new LinkedList<>();
        }

        /**
         * Links an element in the chain
         *
         * @param predicate the predicate to test
         * @param extractor the function that extracts the object from the domain element to pass into predicate
         * @param <O>       the extractor function return type
         * @return the Chain instance
         */
        public <O> Chain<E> link(@NotNull Function<E, O> extractor,
                                 @NotNull Predicate<O> predicate) {
            validators.add(new Validator<>(extractor, predicate));
            return this;
        }

        /**
         * Links an element in the chain
         *
         * @param predicate    the predicate to test
         * @param extractor    the function that extracts the object from the domain element to pass into predicate
         * @param errorMessage the error message to print in case the predicate test returns false
         * @param <O>          the transform function return type
         * @return the Chain instance
         */
        public <O> Chain<E> link(@NotNull Function<E, O> extractor,
                                 @NotNull Predicate<O> predicate,
                                 @Nullable String errorMessage) {
            validators.add(new Validator<>(extractor, predicate, errorMessage));
            return this;
        }

        /**
         * Bond the chain
         *
         * @return a new ValidatorChain instance
         */
        public ValidatorChain<E> bond() {
            return new ValidatorChain<>(entity, validators);
        }

    }


}
