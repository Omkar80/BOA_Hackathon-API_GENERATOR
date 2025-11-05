# BOA_Hackathon-API_GENERATOR

This Spring Boot application generates a new Spring Boot microservice skeleton based on a simple JSON description.

## Input format (JSON)

Provide a JSON array of API specs. Each spec must be an object:

[
  {
    "apiName": "createUser",
    "parameters": [ {"name":"username","type":"String"}, {"name":"age","type":"Integer"} ],
    "returnType": "UserDto",
    "method": "POST"
  },
  {
    "apiName": "getUser",
    "parameters": [ {"name":"id","type":"Long"} ],
    "returnType": "UserDto",
    "method": "GET"
  }
]

## Usage

### CLI
Build jar and run:
```
mvn clean package
java -jar target/api-generator-0.1.0.jar --inputFile=/path/to/spec.json --parentName=boa_hackathon_project
```
The generator will create a directory `boa_hackathon_project1` containing a generated microservice skeleton.

### REST
Start the application and POST to:
- /api/generator/fromFile (multipart file upload)
- /api/generator/fromJson (application/json body)

## Notes
- Generated code is deliberately simple and human-readable.
- The generator creates one microservice under the specified parent name with an incrementing suffix.
