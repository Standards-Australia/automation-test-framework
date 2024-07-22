package com.sa;

import exceptions.InvalidRESTMethod;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import restAPICommon.RestAPITestHelper;

import java.util.HashMap;
import java.util.Map;

import static restAPICommon.RestAPITestHelper.*;

public class RestAPIUnitTest {

    @Test
    public void getMethodTest() {
        RequestSpecification request =
                formRequest(null, null, null);
        ValidatableResponse response = null;
        try {
            response = RestAPITestHelper.getResponse("https://reqres.in/api/users?page=2", "GET", request);
        } catch (InvalidRESTMethod e) {
            Assert.assertTrue(false, e.getMessage());
        }
        Map<String, Object> validateMap = new HashMap<>();
        validateMap.put("page", "*");
        validateMap.put("data[0].id", "*");
        RestAPITestHelper.validateResponse(response, 200, validateMap);
    }
}
