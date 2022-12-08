package com.francescomagarotto.chainvalidator;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.function.Predicate;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ValidatorChain<E> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ValidatorChain.class);
    private final LinkedList<Pair<Predicate, Transform>> couples;
    private final WeakHashMap<Pair<Predicate, Transform>, String> errors;
    private final E entity;

    private ValidatorChain(E entity, LinkedList<Pair<Predicate, Transform>> couples, WeakHashMap<Pair<Predicate, Transform>, String> errors) {
        this.entity = entity;
        this.couples = couples;
        this.errors = errors;
    }

    public static <E> Chain<E> of(E entity) {
        return new Chain<>(entity);
    }

    public boolean validate() {
        boolean valid = true;
        Iterator<Pair<Predicate, Transform>> pairIterator = couples.iterator();
        Pair<Predicate, Transform> pair;
        while (pairIterator.hasNext() && valid) {
            pair = pairIterator.next();
            valid = pair.l.test(pair.r.transform(entity));
            if (!valid) {
                LOGGER.error(errors.get(pair));
            }
        }
        return valid;
    }

    public static class Chain<E> {
        private final LinkedList<Pair<Predicate, Transform>> couples;
        private final WeakHashMap<Pair<Predicate, Transform>, String> errors;
        private final E entity;

        public Chain(E entity) {
            this.couples = new LinkedList<>();
            this.entity = entity;
            this.errors = new WeakHashMap<>();
        }

        public <O> Chain<E> chain(Predicate<O> predicate, Transform<E, O> transform) {
            couples.add(new Pair<>(predicate, transform));
            return this;
        }

        public <O> Chain<E> chain(Predicate<O> predicate, Transform<E, O> transform, @NotNull String errorMessage) {
            Pair p = new Pair<>(predicate, transform);
            couples.add(p);
            errors.put(p, errorMessage);
            return this;
        }

        public ValidatorChain<E> buildChain() {
            return new ValidatorChain<>(entity, couples, errors);
        }
    }

    public static class Pair<L, R> {
        private L l;
        private R r;

        public Pair(L l, R r) {
            this.l = l;
            this.r = r;
        }

        public L getL() {
            return l;
        }

        public R getR() {
            return r;
        }

        public void setL(L l) {
            this.l = l;
        }

        public void setR(R r) {
            this.r = r;
        }
    }

}
