package com.boa.apigenerator.service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.boa.apigenerator.model.ApiSpec;

import org.springframework.stereotype.Service;

/**
 * Generates a Spring Boot microservice project skeleton based on provided API specs.
 *
 * Improvements:
 * - Determines next available project serial by scanning the filesystem so generated
 *   projects are named: <parentBaseName>_<serial> (e.g. boa_hackathon_project_1).
 * - Avoids overwriting existing projects.
 */
@Service
public class ApiProjectGeneratorService {

    private static final Pattern TRAILING_DIGITS_UNDERSCORE = Pattern.compile("^(.*?)(?:_+\\d+)$");
    private static final Pattern MATCH_UNDERSCORE_NUMBER = Pattern.compile("^%s_(\\d+)$"); // will be formatted
    private static final Pattern MATCH_NO_UNDERSCORE_NUMBER = Pattern.compile("^%s(\\d+)$"); // legacy format

    /**
     * Generates projects under a parent directory. Returns the path of the created folder.
     *
     * Example final path: boa_hackathon_project_1/generated-service
     */
    public String generateProjects(List<ApiSpec> specs, String parentBaseName) throws Exception {
        if (specs == null || specs.isEmpty()) {
            throw new IllegalArgumentException("No API specs provided");
        }

        // normalize base name (strip trailing underscores + digits if someone passed 'boa_hackathon_project_1')
        String normalizedBase = normalizeBaseName(parentBaseName);

        // determine the next available project folder name by scanning current working directory
        String parentName = determineNextParentName(normalizedBase);

        // create directories: parentName/generated-service
        File parentDir = new File(parentName);
        if (!parentDir.exists()) {
            boolean ok = parentDir.mkdirs();
            if (!ok && !parentDir.exists()) {
                throw new IllegalStateException("Failed to create parent directory: " + parentDir.getAbsolutePath());
            }
        }

        File serviceDir = new File(parentDir, "generated-service");
        if (!serviceDir.exists()) {
            boolean ok = serviceDir.mkdirs();
            if (!ok && !serviceDir.exists()) {
                throw new IllegalStateException("Failed to create service directory: " + serviceDir.getAbsolutePath());
            }
        }

        // Create Maven pom.xml for generated service
        writeFile(new File(serviceDir, "pom.xml"), Templates.generatedServicePom());

        // Create src structure
        File srcMain = new File(serviceDir, "src/main/java/com/boa/generated");
        srcMain.mkdirs();
        File res = new File(serviceDir, "src/main/resources");
        res.mkdirs();

        // Write Application class
        writeFile(new File(srcMain, "GeneratedServiceApplication.java"), Templates.generatedApplicationClass());

        // Write controller that exposes the APIs described
        writeFile(new File(srcMain, "GeneratedController.java"), Templates.generatedController(specs));

        // Write a simple service class
        writeFile(new File(srcMain, "GeneratedBusinessService.java"), Templates.generatedServiceClass(specs));

        // Write simple DTO and entity placeholders
        writeFile(new File(srcMain, "models.java"), Templates.generatedModels(specs));

        // application.properties
        writeFile(new File(res, "application.properties"), "server.port=0\n");

        return "Generated project at: " + parentDir.getAbsolutePath();
    }

    /**
     * CLI handler for --inputFile and --parentName
     */
    public void handleCommandLineArgs(String[] args) throws Exception {
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

    /* ---------- Helper methods ---------- */

    /**
     * Determine the next parent folder name by scanning current directory for existing folders.
     * The final name will be: <base>_<nextIndex>
     */
    private String determineNextParentName(String base) {
        // Build regex patterns using the provided base
        Pattern pUnderscore = Pattern.compile(String.format(MATCH_UNDERSCORE_NUMBER.pattern(), Pattern.quote(base)));
        Pattern pNoUnderscore = Pattern.compile(String.format(MATCH_NO_UNDERSCORE_NUMBER.pattern(), Pattern.quote(base)));

        int maxIndex = 0;
        File cwd = new File(".");
        File[] entries = cwd.listFiles(File::isDirectory);

        if (entries != null) {
            for (File f : entries) {
                String name = f.getName();
                Matcher m1 = pUnderscore.matcher(name);
                Matcher m2 = pNoUnderscore.matcher(name);
                if (m1.matches()) {
                    try {
                        int idx = Integer.parseInt(m1.group(1));
                        if (idx > maxIndex) maxIndex = idx;
                    } catch (NumberFormatException e) {
                        // skip
                    }
                } else if (m2.matches()) {
                    // legacy format baseNNN without underscore
                    try {
                        int idx = Integer.parseInt(m2.group(1));
                        if (idx > maxIndex) maxIndex = idx;
                    } catch (NumberFormatException e) {
                        // skip
                    }
                }
            }
        }

        int next = maxIndex + 1;
        return base + "_" + next;
    }

    /**
     * Normalize the parent base name by removing trailing underscores and digits.
     * Examples:
     *  - "boa_hackathon_project_1" -> "boa_hackathon_project"
     *  - "boa_hackathon_project__02" -> "boa_hackathon_project"
     */
    private String normalizeBaseName(String raw) {
        if (raw == null || raw.isBlank()) {
            return "boa_hackathon_project";
        }
        String trimmed = raw.trim();
        // If ends with _digits or digits, strip them
        Matcher m = TRAILING_DIGITS_UNDERSCORE.matcher(trimmed);
        if (m.matches()) {
            return m.group(1);
        }
        // also remove trailing underscores only
        return trimmed.replaceAll("_+$", "");
    }

    private void writeFile(File f, String content) throws Exception {
        Files.createDirectories(f.toPath().getParent());
        try (FileWriter fw = new FileWriter(f, false)) {
            fw.write(content);
        }
    }
}
