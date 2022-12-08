package com.francescomagarotto.chainvalidator.validators;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;


@FunctionalInterface
@SuppressWarnings("unused")
public interface Extractor<I, O> {
    @NotNull
    O transform(I input);

    default <V> Extractor<V, O> map(@NotNull Extractor<? super V, ? extends I> before) {
        Objects.requireNonNull(before);
        return (V v) -> transform(before.transform(v));
    }

}

