package com.boa.apigenerator.model;

import java.util.List;
import java.util.Map;

/**
 * Represents one API description:
 * {
 *   "apiName": "createUser",
 *   "parameters": [ {"name":"username","type":"String"}, {"name":"age","type":"Integer"} ],
 *   "returnType": "UserDto",
 *   "method": "POST"
 * }
 */
public class ApiSpec {
    private String apiName;
    private List<Map<String,String>> parameters;
    private String returnType;
    private String method;

    public ApiSpec() {}

    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }

    public List<Map<String,String>> getParameters() { return parameters; }
    public void setParameters(List<Map<String,String>> parameters) { this.parameters = parameters; }

    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}
