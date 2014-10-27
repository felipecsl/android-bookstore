package com.example.android.model;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

// Adapted from https://gist.github.com/JakeWharton/0d67d01badcee0ae7bc9
public final class AutoParcelAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (!rawType.isAnnotationPresent(AutoGson.class))
            return null;

        String packageName = rawType.getPackage().getName();
        String className = rawType.getName().substring(packageName.length() + 1).replace('$', '_');
        String autoValueName = packageName + ".AutoParcel_" + className;

        try {
            Class<?> autoValueType = Class.forName(autoValueName);
            return (TypeAdapter<T>) gson.getAdapter(autoValueType);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load AutoValue type " + autoValueName, e);
        }
    }
}