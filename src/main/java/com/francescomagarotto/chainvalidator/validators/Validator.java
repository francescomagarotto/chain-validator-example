package com.francescomagarotto.chainvalidator.validators;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @param <E> input entity type
 * @param <P> projection type
 */
public class Validator<E, P> {

    private final static Logger LOGGER = LoggerFactory.getLogger(Validator.class);
    private static final String EXTRACTOR_MUST_NOT_BE_NULL = "Extractor must not be null";
    private static final String PREDICATE_MUST_NOT_BE_NULL = "Predicate must not be null";
    private final Function<E, P> extractor;
    private final Predicate<P> predicate;
    @Nullable
    private final String errorMessage;

    public Validator(@NotNull Function<E, P> extractor, @NotNull Predicate<P> predicate) {
        this(extractor, predicate, null);
    }

    public Validator(
            @NotNull Function<E, P> extractor,
            @NotNull Predicate<P> predicate,
            @Nullable String errorMessage) {
        Objects.requireNonNull(extractor, EXTRACTOR_MUST_NOT_BE_NULL);
        Objects.requireNonNull(predicate, PREDICATE_MUST_NOT_BE_NULL);
        this.extractor = extractor;
        this.predicate = predicate;
        this.errorMessage = errorMessage;
    }

    public boolean valid(@NotNull E input) {
        boolean result = true;
        try {
            P projections = extractor.apply(input);
            result = predicate.test(projections);
            if (!result && StringUtils.isNotBlank(errorMessage)) {
                LOGGER.error(errorMessage);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            result = false;
        }
        return result;
    }
}