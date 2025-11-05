package com.boa.apigenerator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationArguments;
import org.springframework.beans.factory.annotation.Autowired;

import com.boa.apigenerator.service.ApiProjectGeneratorService;

@SpringBootApplication
public class ApiGeneratorApplication implements CommandLineRunner {

    @Autowired
    private ApiProjectGeneratorService generatorService;

    public static void main(String[] args) {
        SpringApplication.run(ApiGeneratorApplication.class, args);
    }

    /**
     * Support running from command line:
     * java -jar api-generator.jar --inputFile=/path/to/input.json --parentName=boa_hackathon_project1
     */
    @Override
    public void run(String... args) throws Exception {
        // If arguments are provided we will handle CLI mode, otherwise the application runs as web service
        if (args != null && args.length > 0) {
            System.out.println("Running in CLI mode with args:");
            for (String a : args) System.out.println("  " + a);
            generatorService.handleCommandLineArgs(args);
            // After CLI generation we exit the app
            System.exit(0);
        } else {
            System.out.println("No CLI args detected. Start the web service to use REST endpoints.");
        }
    }
}
