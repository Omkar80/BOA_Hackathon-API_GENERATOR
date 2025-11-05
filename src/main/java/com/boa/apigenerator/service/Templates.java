package com.boa.apigenerator.service;

import java.util.List;
import java.util.Map;

import com.boa.apigenerator.model.ApiSpec;

/**
 * Template helper producing strings for files.
 * Keep templates simple and human-readable. You may extend templates to produce more elaborate code.
 */
public class Templates {

    public static String generatedServicePom() {
        return """<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n""" +
               "  <modelVersion>4.0.0</modelVersion>\n" +
               "  <groupId>com.boa</groupId>\n" +
               "  <artifactId>generated-service</artifactId>\n" +
               "  <version>0.0.1-SNAPSHOT</version>\n" +
               "  <properties>\n" +
               "    <java.version>17</java.version>\n" +
               "    <spring.boot.version>3.2.0</spring.boot.version>\n" +
               "  </properties>\n" +
               "  <dependencyManagement>\n" +
               "    <dependencies>\n" +
               "      <dependency>\n" +
               "        <groupId>org.springframework.boot</groupId>\n" +
               "        <artifactId>spring-boot-dependencies</artifactId>\n" +
               "        <version>${spring.boot.version}</version>\n" +
               "        <type>pom</type>\n" +
               "        <scope>import</scope>\n" +
               "      </dependency>\n" +
               "    </dependencies>\n" +
               "  </dependencyManagement>\n" +
               "  <dependencies>\n" +
               "    <dependency>\n" +
               "      <groupId>org.springframework.boot</groupId>\n" +
               "      <artifactId>spring-boot-starter-web</artifactId>\n" +
               "    </dependency>\n" +
               "  </dependencies>\n" +
               "  <build>\n" +
               "    <plugins>\n" +
               "      <plugin>\n" +
               "        <groupId>org.springframework.boot</groupId>\n" +
               "        <artifactId>spring-boot-maven-plugin</artifactId>\n" +
               "      </plugin>\n" +
               "    </plugins>\n" +
               "  </build>\n" +
               "</project>\n";
    }

    public static String generatedApplicationClass() {
        return "package com.boa.generated;\n\n" +
               "import org.springframework.boot.SpringApplication;\n" +
               "import org.springframework.boot.autoconfigure.SpringBootApplication;\n\n" +
               "@SpringBootApplication\n" +
               "public class GeneratedServiceApplication {\n" +
               "    public static void main(String[] args) {\n" +
               "        SpringApplication.run(GeneratedServiceApplication.class, args);\n" +
               "    }\n" +
               "}\n";
    }

    public static String generatedController(List<ApiSpec> specs) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.boa.generated;\n\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import java.util.*;\n\n");
        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"/api\")\n");
        sb.append("public class GeneratedController {\n\n");
        sb.append("    @Autowired\n    private GeneratedBusinessService business;\n\n");
        for (ApiSpec s : specs) {
            String method = s.getMethod() == null ? "GET" : s.getMethod().toUpperCase();
            String apiName = StringUtilsSafe(s.getApiName());
            String returnType = s.getReturnType() == null ? "String" : s.getReturnType();
            String mapping = mappingAnnotation(method, apiName);
            sb.append("    " + mapping + "\n");
            sb.append("    public " + returnType + " " + apiName + generateMethodSignature(s) + " {\n");
            sb.append("        return business." + apiName + generateBusinessCallSignature(s) + ";\n");
            sb.append("    }\n\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private static String mappingAnnotation(String method, String apiName) {
        switch (method) {
            case "POST": return "@PostMapping(\"/" + apiName + "\")";
            case "PUT": return "@PutMapping(\"/" + apiName + "\")";
            case "DELETE": return "@DeleteMapping(\"/" + apiName + "\")";
            default: return "@GetMapping(\"/" + apiName + "\")";
        }
    }

    private static String StringUtilsSafe(String v) {
        if (v == null || v.isEmpty()) return "unnamedApi";
        return v.replaceAll("\\s+", "");
    }

    private static String generateMethodSignature(ApiSpec s) {
        StringBuilder sig = new StringBuilder();
        sig.append("(");
        if (s.getParameters() != null) {
            boolean first = true;
            for (Map<String,String> p : s.getParameters()) {
                if (!first) sig.append(", ");
                String pname = p.getOrDefault("name", "param");
                String ptype = p.getOrDefault("type", "String");
                sig.append("@RequestParam(" + "\"" + pname + "\"" + ") " + ptype + " " + pname);
                first = false;
            }
        }
        sig.append(")");
        return sig.toString();
    }

    private static String generateBusinessCallSignature(ApiSpec s) {
        StringBuilder call = new StringBuilder();
        call.append("(");
        if (s.getParameters() != null) {
            boolean first = true;
            for (Map<String,String> p : s.getParameters()) {
                if (!first) call.append(", ");
                call.append(p.getOrDefault("name", "param"));
                first = false;
            }
        }
        call.append(")");
        return call.toString();
    }

    public static String generatedServiceClass(List<ApiSpec> specs) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.boa.generated;\n\n");
        sb.append("import org.springframework.stereotype.Service;\n\n");
        sb.append("@Service\n");
        sb.append("public class GeneratedBusinessService {\n\n");
        for (ApiSpec s : specs) {
            String apiName = StringUtilsSafe(s.getApiName());
            String returnType = s.getReturnType() == null ? "String" : s.getReturnType();
            sb.append("    public " + returnType + " " + apiName + generatePlainSignature(s) + " {\n");
            sb.append("        // TODO: replace with real business logic. returning placeholder.\n");
            if (returnType.equals("String")) {
                sb.append("        return \"OK: " + apiName + "\";\n");
            } else {
                sb.append("        return new " + returnType + "();\n");
            }
            sb.append("    }\n\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private static String generatePlainSignature(ApiSpec s) {
        StringBuilder sig = new StringBuilder();
        sig.append("(");
        if (s.getParameters() != null) {
            boolean first = true;
            for (Map<String,String> p : s.getParameters()) {
                if (!first) sig.append(", ");
                String pname = p.getOrDefault("name", "param");
                String ptype = p.getOrDefault("type", "String");
                sig.append(ptype + " " + pname);
                first = false;
            }
        }
        sig.append(")");
        return sig.toString();
    }

    public static String generatedModels(List<ApiSpec> specs) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.boa.generated;\n\n");
        sb.append("// Simple container for DTO/entity placeholder classes\n\n");
        for (ApiSpec s : specs) {
            String rt = s.getReturnType();
            if (rt != null && !rt.isBlank() && !rt.equals("String")) {
                sb.append("public class " + rt + " {\n");
                sb.append("    // Add fields that match your returned object\n");
                sb.append("    public " + rt + "() {}\n");
                sb.append("}\n\n");
            }
        }
        if (sb.toString().endsWith("\n\n")) return sb.toString();
        return sb.toString();
    }
}
