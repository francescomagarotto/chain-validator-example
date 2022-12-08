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

    public static <E> Chain<E> of(E entity) {
        return new Chain<>(entity);
    }

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

        public Chain(E entity) {
            this.couples = new LinkedList<>();
            this.entity = entity;
            this.errors = new WeakHashMap<>();
        }

        public <O> Chain<E> chain(Predicate<O> predicate, Transform<E, O> transform) {
            couples.add(new ValidatorPairs(predicate, transform));
            return this;
        }

        public <O> Chain<E> chain(Predicate<O> predicate, Transform<E, O> transform, @NotNull String errorMessage) {
            ValidatorPairs p = new ValidatorPairs(predicate, transform);
            couples.add(p);
            errors.put(p, errorMessage);
            return this;
        }

        public ValidatorChain<E> buildChain() {
            return new ValidatorChain<>(entity, couples, errors);
        }
    }

    public static class ValidatorPairs {
        private Predicate p;
        private Transform t;

        public ValidatorPairs(Predicate p, Transform t) {
            this.p = p;
            this.t = t;
        }

        public Predicate getPredicate() {
            return p;
        }

        public Transform getTransformer() {
            return t;
        }

        public void setPredicate(Predicate p) {
            this.p = p;
        }

        public void setTransform(Transform t) {
            this.t = t;
        }
    }

}
