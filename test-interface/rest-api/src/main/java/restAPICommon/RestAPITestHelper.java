package restAPICommon;

import exceptions.InvalidRESTMethod;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.json.JSONObject;

import java.util.Map;


public class RestAPITestHelper {
    private static Logger logger;

    static {
        RestAPITestHelper.logger = LogManager.getLogger(RestAPITestHelper.class);
    }

/*    public RestAPITestHelper() {
        //Do Nothing. Declares that class will never be instantiated.
    }*/

    /**
     * Forms a request.
     *
     * @param headers     Request headers as {@link java.util.Map Map&lt;String,String&gt;}
     * @param parameters  Request parameters as {@link java.util.Map Map&lt;String,String&gt;}
     * @param requestBody Request body as {@link org.json.JSONObject JSONObject}
     * @return {@link io.restassured.specification.RequestSpecification RequestSpecification}
     * @author nishad.chayanakhawa
     * @since v2.0.0
     */
    public static RequestSpecification attachRequestComponents
    (Map<String, String> headers, Map<String, String> parameters, JSONObject requestBody) {
        //if header map is not null, attach each header from map
        //		RequestSpecification mySpec = new RequestSpecBuilder().setUrlEncodingEnabled(false).build();
        RequestSpecification request =
                RestAssured
                        .given()
                        //				.spec(mySpec)
                        .urlEncodingEnabled(false);
        if (headers != null) {
            logger.info("Request Headers: [{}]", () -> headers.toString());
            for (String headerParameterName : headers.keySet()) {
                request.header(headerParameterName, headers.get(headerParameterName));
            }
        }
        //if parameter map is not null, attach each parameter from map
        if (parameters != null) {
            logger.info("Request Parameters: [{}]", () -> parameters.toString());
            for (String parameterName : parameters.keySet()) {
                request.param(parameterName, parameters.get(parameterName));
            }
        }
        //if request body is not null, attach request body
        if (requestBody != null) {
            logger.info("Request Body: [{}]", () -> requestBody.toString());
            request.body(requestBody.toString());
        }
        return request;
    }

    /**
     * Forms request specification
     *
     * @param headers     Request headers as {@link java.util.Map Map&lt;String,String&gt;}
     * @param parameters  Request parameters as {@link java.util.Map Map&lt;String,String&gt;}
     * @param requestBody Request body as {@link org.json.JSONObject JSONObject}
     * @return {@link io.restassured.specification.RequestSpecification RequestSpecification}
     * @author nishad.chayanakhawa
     * @since v2.0.0
     */
    public static RequestSpecification formRequest
    (Map<String, String> headers, Map<String, String> parameters, JSONObject requestBody) {
        //create REST request
        //request = RestAssured.given();
        //attach request header, parameter and request body as applicable
        return attachRequestComponents(headers, parameters, requestBody);
        //return request
        //return request;
    }

    /**
     * Forms request specification with proxy embedded in the request
     * <b>Author:</b> Nishad Chayanakhawa(CONID: c000177; n.arun.chayanakhawa@accenture.com)<br>
     * <b>Last Maintained in:</b> v1.1.0
     * @param proxyHost - String Proxy Host URL
     * @param proxyPort - int Proxy Port number
     * @param user - String Proxy user name
     * @param password - String proxy password
     * @param headers - headers Map collection
     * @param parameters - parameters Map collection
     * @param requestBody - JSONObject request body
     * @return
     */
    //	public static RequestSpecification formRequest
    //	(String proxyHost, int proxyPort, String user, String password,
    //			Map<String,String> headers,Map<String,String> parameters,JSONObject requestBody) {
    //		//create REST request
    //		RequestSpecification request = RestAssured.given();
    //		//attach proxy settings to run behind firewall
    //		request.proxy(
    //				host(proxyHost).withPort(proxyPort)
    //				.withAuth(user,password));
    //		//attach request header, parameter and request body as applicable
    //		attachRequestComponents(headers,parameters,requestBody);
    //		//return request
    //		return request;
    //	}

    /**
     * Get REST API response
     *
     * @param url     URL as {@link java.lang.String String}
     * @param method  Method name as {@link java.lang.String String}
     * @param request {@link io.restassured.specification.RequestSpecification RequestSpecification}
     *                generated by {@link #formRequest(Map, Map, JSONObject) formRequest}
     * @return - {@link io.restassured.response.ValidatableResponse ValidatableResponse}
     * @throws InvalidRESTMethod Thrown when unsupported method is used.
     * @author nishad.chayanakhawa
     * @since v2.0.0
     */
    public static ValidatableResponse getResponse
    (String url, String method, RequestSpecification request) throws InvalidRESTMethod {
        if (logger.isInfoEnabled()) {
            logger.info("Generation response. Method: [" + method + "] URL: [" + url + "].");
        }
        //invoke function corresponding to method
        switch (method) {
            case "GET":
                return request.get(url).then();
            case "POST":
                return request.post(url).then();
            case "PUT":
                return request.put(url).then();
            case "DELETE":
                return request.delete(url).then();
            case "PATCH":
                return request.patch(url).then();
            case "OPTIONS":
                return request.options(url).then();
            default:
                //throw exception when method is not supported
                throw new InvalidRESTMethod(method);
        }
    }

    /**
     * Validate REST response
     *
     * @param response           {@link io.restassured.response.ValidatableResponse ValidatableResponse}
     *                           generated through {@link #getResponse(String, String, RequestSpecification) getResponse}
     * @param expectedStatusCode Expected status code as int
     * @param valueValidations   Validations as as {@link java.util.Map Map&lt;String,String&gt;}
     * @author nishad.chayanakhawa
     * @since v2.0.0
     */
    public static void validateResponse
    (ValidatableResponse response,
     int expectedStatusCode, Map<String, Object> valueValidations) {
        logger.info("Response Headers [{}]", () -> response.extract().headers().toString());
        logger.info("Response Body [{}]", () -> response.extract().body().asString());
        logger.info("Response Status [{}]", () -> response.extract().statusCode());
        //if status code is non-zero, assert on the same
        if (expectedStatusCode > 0) {
            if (logger.isInfoEnabled()) {
                logger.info("Validating for status code [" + expectedStatusCode + "]");
            }
            response.assertThat()
                    .statusCode(expectedStatusCode);
        }

        //if validation Map is not null, proceed with validations
        if (valueValidations != null) {
            logger.info("Validating for response body [" + valueValidations.toString() + "]");
            for (String jsonPath : valueValidations.keySet()) {
                //based on value, validate actual value or just non-null criteria
                switch (valueValidations.get(jsonPath).toString()) {
                    case "*":
                        //in case value is '*', validate if value is non-null
                        response.assertThat()
                                .body(jsonPath, Matchers.notNullValue());
                        break;
                    default:
                        //When value is other than '*', assert the string against response
                        response.assertThat()
                                .body(jsonPath, Matchers.notNullValue());
                        Object expectedValue = valueValidations.get(jsonPath);
                        if (expectedValue.toString().contains("%%")) {
                            String expectedWildcard = expectedValue.toString().replace("%%", "");
                            response.assertThat()
                                    .body(jsonPath, Matchers.containsString(expectedWildcard));
                        } else {
                            response.assertThat()
                                    .body(jsonPath, Matchers.equalToObject(valueValidations.get(jsonPath)));
                        }
                        break;
                }
            }
        }
    }
}
