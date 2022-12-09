package com.francescomagarotto.chainvalidator;

import com.francescomagarotto.chainvalidator.validators.Extractor;
import org.jetbrains.annotations.NotNull;


public class NameTransformer implements Extractor<Person, String> {
    @Override
    public @NotNull String transform(Person input) {
        return input.getName();
    }
}
