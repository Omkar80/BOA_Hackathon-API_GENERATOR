package com.boa.apigenerator.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.boa.apigenerator.service.ApiProjectGeneratorService;
import com.boa.apigenerator.service.InputSpecParser;
import com.boa.apigenerator.model.ApiSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Simple REST controller that accepts:
 * - multipart file upload containing JSON spec described in README
 * - or direct JSON body list of API specs
 */
@RestController
@RequestMapping("/api/generator")
public class GeneratorController {

    @Autowired
    private ApiProjectGeneratorService generatorService;

    @PostMapping(value = "/fromFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String generateFromFile(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "parentName", defaultValue = "boa_hackathon_project1") String parentName) throws Exception {
        Path temp = Files.createTempFile("apigen-spec-", ".json");
        file.transferTo(temp.toFile());
        List<ApiSpec> specs = InputSpecParser.parseFromFile(temp.toFile());
        return generatorService.generateProjects(specs, parentName);
    }

    @PostMapping(value = "/fromJson", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String generateFromJson(@RequestBody List<ApiSpec> specs,
                                   @RequestParam(value = "parentName", defaultValue = "boa_hackathon_project1") String parentName) throws Exception {
        return generatorService.generateProjects(specs, parentName);
    }
}
