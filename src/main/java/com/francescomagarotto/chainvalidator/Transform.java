package com.francescomagarotto.chainvalidator;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;


@FunctionalInterface
public interface Transform<I, O> {
    @NotNull
    O transform(I input);

    default <V> Transform<V, O> map(@NotNull Transform<? super V, ? extends I> before) {
        Objects.requireNonNull(before);
        return (V v) -> transform(before.transform(v));
    }

}

