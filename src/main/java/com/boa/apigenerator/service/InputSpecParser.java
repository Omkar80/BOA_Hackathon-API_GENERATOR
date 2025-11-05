package com.boa.apigenerator.service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import com.boa.apigenerator.model.ApiSpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Utility to parse input file. The input must be a JSON array of ApiSpec objects.
 */
public class InputSpecParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<ApiSpec> parseFromFile(File f) throws Exception {
        String content = Files.readString(f.toPath());
        return mapper.readValue(content, new TypeReference<List<ApiSpec>>() {});
    }
}
