package com.francescomagarotto.chainvalidator;


import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Transform<I, O> {
    @NotNull
    O transform(I input);
}
