package com.francescomagarotto.chainvalidator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ValidatorChain<E> {

    private LinkedList<Pair<Predicate, Transform>> couples;
    private final E entity;

    private ValidatorChain(E entity, LinkedList<Pair<Predicate, Transform>> couples) {
        this.entity = entity;
        this.couples = couples;
    }

    public static <E> Chain<E> of(E entity) {
        return new Chain<>(entity);
    }

    public boolean validate() {
        boolean valid = true;
        Iterator<Pair<Predicate, Transform>> pairIterator = couples.iterator();
        Pair<Predicate, Transform> pair;
        while(pairIterator.hasNext() && valid) {
            pair = pairIterator.next();
            valid = pair.l.test(pair.r.transform(entity));
        }
        return valid;
    }

    public static class Chain<E> {
        private final LinkedList<Pair<Predicate, Transform>> couples;
        private final E entity;

        public Chain(E entity) {
            this.couples = new LinkedList<>();
            this.entity = entity;
        }

        public <O> Chain<E> chain(Predicate<O> predicate, Transform<E, O> transform) {
            couples.add(new Pair<>(predicate, transform));
            return this;
        }

        public ValidatorChain<E> buildChain() {
            return new ValidatorChain<>(entity, couples);
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
