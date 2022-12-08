package com.francescomagarotto.chainvalidator;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ValidatorChain<E> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ValidatorChain.class);
    private final LinkedList<ValidatorPairs> couples;
    private final WeakHashMap<ValidatorPairs, String> errors;
    private final E entity;

    private ValidatorChain(E entity, LinkedList<ValidatorPairs> couples, WeakHashMap<ValidatorPairs, String> errors) {
        this.entity = entity;
        this.couples = couples;
        this.errors = errors;
    }

    /**
     * Create a new chain of validators
     * @param entity the domain element and input type for transform functions.
     * @return a new {@see Chain} of validators for the domain element.
     * @param <E> the type of the domain element
     */
    public static <E> Chain<E> of(E entity) {
        return new Chain<>(entity);
    }

    /**
     * Applies the transformation function and test the predicate
     * for each element in the chain.
     * @return true if all predicates return true, otherwise false.
     */
    public boolean validate() {
        boolean valid = true;
        Iterator<ValidatorPairs> pairIterator = couples.iterator();
        ValidatorPairs validatorPairs;
        while (pairIterator.hasNext() && valid) {
            validatorPairs = pairIterator.next();
            valid = validatorPairs.p.test(validatorPairs.t.transform(entity));
            if (!valid && errors.get(validatorPairs) != null) {
                LOGGER.error(errors.get(validatorPairs));
            }
        }
        return valid;
    }

    public static class Chain<E> {
        private final LinkedList<ValidatorPairs> couples;
        private final WeakHashMap<ValidatorPairs, String> errors;
        private final E entity;

        public Chain(@NotNull E entity) {
            this.couples = new LinkedList<>();
            this.entity = entity;
            this.errors = new WeakHashMap<>();
        }

        /**
         * Add an element to the chain
         * @param predicate the predicate to test
         * @param transform the transform function from the domain element to predicate input type
         * @return the Chain instance
         * @param <O> the transform function return type
         */
        public <O> Chain<E> chain(@NotNull Predicate<O> predicate,
                                  @NotNull Transform<E, O> transform) {
            couples.add(new ValidatorPairs(predicate, transform));
            return this;
        }

        /**
         * Add an element to the chain
         * @param predicate the predicate to test
         * @param transform the transform function from the domain element to predicate input type
         * @param errorMessage the error message to print in case the predicate test returns false
         * @return the Chain instance
         * @param <O> the transform function return type
         */
        public <O> Chain<E> chain(@NotNull Predicate<O> predicate,
                                  @NotNull Transform<E, O> transform,
                                  @NotNull String errorMessage) {
            ValidatorPairs p = new ValidatorPairs(predicate, transform);
            couples.add(p);
            errors.put(p, errorMessage);
            return this;
        }

        /**
         * Build the chain
         * @return a new ValidatorChain instance
         */
        public ValidatorChain<E> buildChain() {
            return new ValidatorChain<>(entity, couples, errors);
        }
    }

    private static class ValidatorPairs {
        private final Predicate p;
        private final Transform t;

        public ValidatorPairs(Predicate p, Transform t) {
            this.p = p;
            this.t = t;
        }

    }

}
