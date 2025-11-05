package com.boa.apigenerator.service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.boa.apigenerator.model.ApiSpec;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Generates a simple Spring Boot microservice project skeleton based on provided API specs.
 * The generated project is intentionally minimal but complete and compilable.
 */
@Service
public class ApiProjectGeneratorService {

    private final AtomicInteger projectCounter = new AtomicInteger(1);

    /**
     * Generates projects under a parent directory. Returns the path of the created folder.
     */
    public String generateProjects(List<ApiSpec> specs, String parentBaseName) throws Exception {
        if (specs == null || specs.isEmpty()) {
            throw new IllegalArgumentException("No API specs provided");
        }
        // Decide directory name
        int idx = projectCounter.getAndIncrement();
        String parentName = parentBaseName + idx;
        File parentDir = new File(parentName);
        if (!parentDir.exists()) parentDir.mkdirs();

        // For each run we generate one microservice project under parentDir named "generated-service"
        File serviceDir = new File(parentDir, "generated-service");
        if (!serviceDir.exists()) serviceDir.mkdirs();

        // Create Maven pom.xml for generated service
        writeFile(new File(serviceDir, "pom.xml"),
            Templates.generatedServicePom());

        // Create src structure
        File srcMain = new File(serviceDir, "src/main/java/com/boa/generated");
        srcMain.mkdirs();
        File res = new File(serviceDir, "src/main/resources");
        res.mkdirs();

        // Write Application class
        writeFile(new File(srcMain, "GeneratedServiceApplication.java"),
            Templates.generatedApplicationClass());

        // Write controller that exposes the APIs described
        writeFile(new File(srcMain, "GeneratedController.java"),
            Templates.generatedController(specs));

        // Write a simple service class
        writeFile(new File(srcMain, "GeneratedBusinessService.java"),
            Templates.generatedServiceClass(specs));

        // Write simple DTO and entity placeholders
        writeFile(new File(srcMain, "models.java"), Templates.generatedModels(specs));

        // application.properties
        writeFile(new File(res, "application.properties"), "server.port=0\n");

        return "Generated project at: " + parentDir.getAbsolutePath();
    }

    public void handleCommandLineArgs(String[] args) throws Exception {
        // Simple CLI parsing: --inputFile=/path/to/file.json --parentName=boa_hackathon_project
        String inputFile = null;
        String parentName = "boa_hackathon_project";
        for (String a : args) {
            if (a.startsWith("--inputFile=")) {
                inputFile = a.substring("--inputFile=".length());
            } else if (a.startsWith("--parentName=")) {
                parentName = a.substring("--parentName=".length());
            }
        }
        if (inputFile == null) {
            throw new IllegalArgumentException("Please provide --inputFile=/path/to/spec.json");
        }
        List<com.boa.apigenerator.model.ApiSpec> specs = InputSpecParser.parseFromFile(new File(inputFile));
        System.out.println(generateProjects(specs, parentName));
    }

    private void writeFile(File f, String content) throws Exception {
        Files.createDirectories(f.toPath().getParent());
        try (FileWriter fw = new FileWriter(f, false)) {
            fw.write(content);
        }
    }
}
