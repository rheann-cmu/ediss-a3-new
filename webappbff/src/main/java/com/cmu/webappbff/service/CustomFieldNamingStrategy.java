package com.cmu.webappbff.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Field;

public class CustomFieldNamingStrategy implements FieldNamingStrategy {

    @Override
    public String translateName(Field field) {
        JsonProperty jsonPropertyAnnotation = field.getAnnotation(JsonProperty.class);
        if (jsonPropertyAnnotation != null && !jsonPropertyAnnotation.value().isEmpty()) {
            return jsonPropertyAnnotation.value();
        }
        return field.getName();
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(new CustomFieldNamingStrategy())
                .create();

        // Now Gson will use @JsonProperty annotations to determine field names
    }
}

