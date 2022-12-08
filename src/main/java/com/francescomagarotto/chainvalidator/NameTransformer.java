package com.francescomagarotto.chainvalidator;

import org.jetbrains.annotations.NotNull;

public class NameTransformer implements Transform<Pippo, String> {
    @Override
    public @NotNull String transform(Pippo input) {
        return input.getName();
    }
}
