package com.example.android.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks an AutoParcel-annotated type for proper Gson serialization.
 * <p/>
 * This annotation is needed because the {@linkplain java.lang.annotation.Retention retention} of {@code @AutoValue}
 * does not allow reflection at runtime.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface AutoGson {
}