package com.examly.apigateway;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringappApplicationTests {

    private String clienttoken;
    private String managertoken;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper; // To parse JSON responses

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    @Test
    @Order(1)
    void backend_testRegisterManager() {
        String requestBody = "{\"userId\": 1,\"email\": \"demoadmin@gmail.com\", \"password\": \"admin@1234\", \"username\": \"admin123\", \"userRole\": \"Manager\", \"mobileNumber\": \"9876543210\"}";
        ResponseEntity<String> response = restTemplate.postForEntity("/api/register",
                new HttpEntity<>(requestBody, createHeaders()), String.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Order(2)
    void backend_testRegisterClient() {
        String requestBody = "{\"userId\": 2,\"email\": \"demouser@gmail.com\", \"password\": \"user@1234\", \"username\": \"user123\", \"userRole\": \"Client\", \"mobileNumber\": \"1122334455\"}";
        ResponseEntity<String> response = restTemplate.postForEntity("/api/register",
                new HttpEntity<>(requestBody, createHeaders()), String.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Order(3)
    void backend_testLoginManager() throws Exception {
        String requestBody = "{\"email\": \"demoadmin@gmail.com\", \"password\": \"admin@1234\"}";

        ResponseEntity<String> response = restTemplate.postForEntity("/api/login",
                new HttpEntity<>(requestBody, createHeaders()), String.class);

        // Check if response body is null
        Assertions.assertNotNull(response.getBody(), "Response body is null!");

        JsonNode responseBody = objectMapper.readTree(response.getBody());
        String token = responseBody.get("token").asText();
        managertoken = token;

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(token);
    }

    @Test
    @Order(4)
    void backend_testLoginClient() throws Exception {
        String requestBody = "{\"email\": \"demouser@gmail.com\", \"password\": \"user@1234\"}";

        ResponseEntity<String> response = restTemplate.postForEntity("/api/login",
                new HttpEntity<>(requestBody, createHeaders()), String.class);

        JsonNode responseBody = objectMapper.readTree(response.getBody());
        String token = responseBody.get("token").asText();
        clienttoken = token;

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(token);
    }

 @Test
@Order(5)
void backend_testAddSupportAgentWithRoleValidation() throws Exception {
    // Ensure tokens are available
    Assertions.assertNotNull(managertoken, "Manager token should not be null");
    Assertions.assertNotNull(clienttoken, "Client token should not be null");

    // Fixed static date (YYYY-MM-DD)
    String fixedDate = "2025-05-01";

    // Request Body for SupportAgent
    String requestBody = "{"
            + "\"name\": \"Ravi Kumar\","
            + "\"email\": \"ravi.kumar@example.com\","
            + "\"phone\": \"9876543210\","
            + "\"expertise\": \"Networking\","
            + "\"experience\": \"4 years\","
            + "\"status\": \"Available\","
            + "\"addedDate\": \"" + fixedDate + "\","
            + "\"profile\": \"Sample Resume Data\","
            + "\"shiftTiming\": \"9 AM - 6 PM\","
            + "\"remarks\": \"Skilled agent\""
            + "}";

    // ✅ Test with Manager Token
    HttpHeaders managerHeaders = createHeaders();
    managerHeaders.set("Authorization", "Bearer " + managertoken);
    HttpEntity<String> managerRequest = new HttpEntity<>(requestBody, managerHeaders);

    ResponseEntity<String> managerResponse = restTemplate.exchange("/api/supportAgent", HttpMethod.POST, managerRequest, String.class);
    System.out.println(managerResponse.getStatusCode() + " Status code for Manager adding SupportAgent");
    Assertions.assertEquals(HttpStatus.CREATED, managerResponse.getStatusCode());

    // ❌ Test with Client Token
    HttpHeaders clientHeaders = createHeaders();
    clientHeaders.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<String> clientRequest = new HttpEntity<>(requestBody, clientHeaders);

    ResponseEntity<String> clientResponse = restTemplate.exchange("/api/supportAgent", HttpMethod.POST, clientRequest, String.class);
    System.out.println(clientResponse.getStatusCode() + " Status code for Client trying to add SupportAgent");
    Assertions.assertEquals(HttpStatus.FORBIDDEN, clientResponse.getStatusCode());
}

@Test
@Order(6)
void backend_testGetSupportAgentForBothRoles() throws Exception {
    // Ensure tokens are available
    Assertions.assertNotNull(managertoken, "Manager token should not be null");
    Assertions.assertNotNull(clienttoken, "Client token should not be null");

    // Static agentId to test
    Long agentId = 1L; // Use an actual valid agentId in your database

    // ✅ Test with Manager Token (Expecting 200 OK)
    HttpHeaders managerHeaders = createHeaders();
    managerHeaders.set("Authorization", "Bearer " + managertoken);
    HttpEntity<String> managerRequest = new HttpEntity<>(managerHeaders);

    ResponseEntity<String> managerResponse = restTemplate.exchange("/api/supportAgent/" + agentId, HttpMethod.GET, managerRequest, String.class);
    System.out.println(managerResponse.getStatusCode() + " Status code for Manager retrieving SupportAgent");
    Assertions.assertEquals(HttpStatus.OK, managerResponse.getStatusCode());

    // ✅ Test with Client Token (Expecting 200 OK)
    HttpHeaders clientHeaders = createHeaders();
    clientHeaders.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<String> clientRequest = new HttpEntity<>(clientHeaders);

    ResponseEntity<String> clientResponse = restTemplate.exchange("/api/supportAgent/" + agentId, HttpMethod.GET, clientRequest, String.class);
    System.out.println(clientResponse.getStatusCode() + " Status code for Client retrieving SupportAgent");
    Assertions.assertEquals(HttpStatus.OK, clientResponse.getStatusCode());
}


@Test
@Order(7)
void backend_testGetAllSupportAgentsWithRoleValidation() throws Exception {
    // Ensure tokens are available
    Assertions.assertNotNull(managertoken, "Manager token should not be null");
    Assertions.assertNotNull(clienttoken, "Client token should not be null");

    // ✅ Test with Manager Token (Expecting 200 OK)
    HttpHeaders managerHeaders = createHeaders();
    managerHeaders.set("Authorization", "Bearer " + managertoken);
    HttpEntity<String> managerRequest = new HttpEntity<>(managerHeaders);

    ResponseEntity<String> managerResponse = restTemplate.exchange("/api/supportAgent", HttpMethod.GET, managerRequest, String.class);
    System.out.println(managerResponse.getStatusCode() + " Status code for Manager retrieving all SupportAgents");
    Assertions.assertEquals(HttpStatus.OK, managerResponse.getStatusCode());

    // ❌ Test with Client Token (Expecting 403 Forbidden)
    HttpHeaders clientHeaders = createHeaders();
    clientHeaders.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<String> clientRequest = new HttpEntity<>(clientHeaders);

    ResponseEntity<String> clientResponse = restTemplate.exchange("/api/supportAgent", HttpMethod.GET, clientRequest, String.class);
    System.out.println(clientResponse.getStatusCode() + " Status code for Client trying to retrieve all SupportAgents");
    Assertions.assertEquals(HttpStatus.FORBIDDEN, clientResponse.getStatusCode());
}


@Test
@Order(8)
void backend_testUpdateSupportAgent_ManagerOnly() {
    Assertions.assertNotNull(managertoken, "Manager token should not be null");
    Assertions.assertNotNull(clienttoken, "Client token should not be null");

    Long agentIdToUpdate = 1L;

    String updateRequestBody = "{"
        + "\"agentId\": " + agentIdToUpdate + ","
        + "\"name\": \"Ravi Kumar\","
        + "\"email\": \"ravi.kumar@example.com\","
        + "\"phone\": \"9876543210\","
        + "\"expertise\": \"Networking\","
        + "\"experience\": \"4 years\","
        + "\"status\": \"Available\","
        + "\"addedDate\": \"2025-05-12\","
        + "\"profile\": \"Updated resume data\","
        + "\"shiftTiming\": \"10 AM - 7 PM\","
        + "\"remarks\": \"Skilled in router troubleshooting\""
        + "}";

    // Manager can update
    HttpHeaders managerHeaders = createHeaders();
    managerHeaders.set("Authorization", "Bearer " + managertoken);
    HttpEntity<String> managerRequest = new HttpEntity<>(updateRequestBody, managerHeaders);
    ResponseEntity<String> managerResponse = restTemplate.exchange(
        "/api/supportAgent/" + agentIdToUpdate, HttpMethod.PUT, managerRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, managerResponse.getStatusCode());

    // Client should be forbidden
    HttpHeaders clientHeaders = createHeaders();
    clientHeaders.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<String> clientRequest = new HttpEntity<>(updateRequestBody, clientHeaders);
    ResponseEntity<String> clientResponse = restTemplate.exchange(
        "/api/supportAgent/" + agentIdToUpdate, HttpMethod.PUT, clientRequest, String.class);
    Assertions.assertEquals(HttpStatus.FORBIDDEN, clientResponse.getStatusCode());
}


@Test
@Order(9)
void backend_testCreateTicket() throws Exception {
    Assertions.assertNotNull(clienttoken, "User token should not be null");
    Assertions.assertNotNull(managertoken, "Manager token should not be null");
    String requestBody = "{"
    + "\"title\": \"Issue with Internet Connection\",\n"
    + "\"description\": \"The internet connection is very slow, affecting work.\",\n"
    + "\"priority\": \"High\",\n"
    + "\"status\": \"Open\",\n"
    + "\"createdDate\": \"2025-05-14\",\n"
    + "\"resolutionDate\": \"2025-05-20\",\n"
    + "\"issueCategory\": \"Technical\",\n"
    + "\"resolutionSummary\": \"Pending resolution by support team\",\n"
    + "\"satisfied\": true,\n"  // Assuming the client is satisfied
    + "\"user\": {\"userId\": 2},\n"  // Assuming the user has userId 2
    + "\"supportAgent\": {\"agentId\": 1}\n"  // Assuming the support agent has agentId 3
    + "}";


    // Client should be able to create a ticket
    HttpHeaders clientHeader = createHeaders();
    clientHeader.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<String> clientRequest = new HttpEntity<>(requestBody, clientHeader);
    ResponseEntity<String> clientResponse = restTemplate.exchange("/api/ticket", HttpMethod.POST, clientRequest, String.class);
    Assertions.assertEquals(HttpStatus.CREATED, clientResponse.getStatusCode());

    // Manager should NOT be able to create a ticket
    HttpHeaders managerHeader = createHeaders();
    managerHeader.set("Authorization", "Bearer " + managertoken);
    HttpEntity<String> managerRequest = new HttpEntity<>(requestBody, managerHeader);
    ResponseEntity<String> managerResponse = restTemplate.exchange("/api/ticket", HttpMethod.POST, managerRequest, String.class);
    Assertions.assertEquals(HttpStatus.FORBIDDEN, managerResponse.getStatusCode());
}

@Test
@Order(10)
void backend_testGetTicketByIdAccessControl() {
    Assertions.assertNotNull(clienttoken, "Client token should not be null");
    Assertions.assertNotNull(managertoken, "Manager token should not be null");

    Long ticketId = 1L; // Replace with a valid ticket ID that exists in your test DB

    // Client should have access
    HttpHeaders clientHeaders = createHeaders();
    clientHeaders.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<Void> clientRequest = new HttpEntity<>(clientHeaders);
    ResponseEntity<String> clientResponse = restTemplate.exchange(
        "/api/ticket/" + ticketId, HttpMethod.GET, clientRequest, String.class
    );
    Assertions.assertEquals(HttpStatus.OK, clientResponse.getStatusCode());

    // Manager should NOT have access
    HttpHeaders managerHeaders = createHeaders();
    managerHeaders.set("Authorization", "Bearer " + managertoken);
    HttpEntity<Void> managerRequest = new HttpEntity<>(managerHeaders);
    ResponseEntity<String> managerResponse = restTemplate.exchange(
        "/api/ticket/" + ticketId, HttpMethod.GET, managerRequest, String.class
    );
    Assertions.assertEquals(HttpStatus.FORBIDDEN, managerResponse.getStatusCode());
}

@Test
@Order(11)
void backend_testGetAllTicketsAccessByBothRoles() {
    Assertions.assertNotNull(clienttoken, "Client token should not be null");
    Assertions.assertNotNull(managertoken, "Manager token should not be null");

    // Client access
    HttpHeaders clientHeaders = createHeaders();
    clientHeaders.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<Void> clientRequest = new HttpEntity<>(clientHeaders);
    ResponseEntity<String> clientResponse = restTemplate.exchange(
        "/api/ticket", HttpMethod.GET, clientRequest, String.class
    );
    Assertions.assertEquals(HttpStatus.OK, clientResponse.getStatusCode());

    // Manager access
    HttpHeaders managerHeaders = createHeaders();
    managerHeaders.set("Authorization", "Bearer " + managertoken);
    HttpEntity<Void> managerRequest = new HttpEntity<>(managerHeaders);
    ResponseEntity<String> managerResponse = restTemplate.exchange(
        "/api/ticket", HttpMethod.GET, managerRequest, String.class
    );
    Assertions.assertEquals(HttpStatus.OK, managerResponse.getStatusCode());
}




@Test
@Order(12)
void backend_testAddFeedback() throws Exception {
    Assertions.assertNotNull(clienttoken, "User token should not be null");
    Assertions.assertNotNull(managertoken, "Manager token should not be null");

    String requestBody = "{"
    + "\"feedbackText\": \"The support agent was very helpful and resolved the issue quickly.\","
    + "\"date\": \"2025-05-14\","
    + "\"user\": {\"userId\": 2},"
    + "\"supportAgent\": {\"agentId\": 1},"
    + "\"ticket\": {\"ticketId\": 1},"
    + "\"category\": \"Service Quality\","
    + "\"rating\": 5"
    + "}";

    // Client should be able to add feedback
    HttpHeaders clientHeader = createHeaders();
    clientHeader.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<String> clientRequest = new HttpEntity<>(requestBody, clientHeader);
    ResponseEntity<String> clientResponse = restTemplate.exchange("/api/feedback", HttpMethod.POST, clientRequest, String.class);
    Assertions.assertEquals(HttpStatus.CREATED, clientResponse.getStatusCode());

    // Manager should NOT be able to add feedback
    HttpHeaders managerHeader = createHeaders();
    managerHeader.set("Authorization", "Bearer " + managertoken);
    HttpEntity<String> managerRequest = new HttpEntity<>(requestBody, managerHeader);
    ResponseEntity<String> managerResponse = restTemplate.exchange("/api/feedback", HttpMethod.POST, managerRequest, String.class);
    Assertions.assertEquals(HttpStatus.FORBIDDEN, managerResponse.getStatusCode());
}





@Test
@Order(13)
void backend_testGetFeedback() throws Exception {
    Assertions.assertNotNull(clienttoken, "User token should not be null");
    Assertions.assertNotNull(managertoken, "Manager token should not be null");

    Long feedbackId = 1L; // Assuming feedbackId 1 exists

    // Client should be able to view feedback details
    HttpHeaders clientHeader = createHeaders();
    clientHeader.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<String> clientRequest = new HttpEntity<>(clientHeader);
    ResponseEntity<String> clientResponse = restTemplate.exchange("/api/feedback/" + feedbackId, HttpMethod.GET, clientRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, clientResponse.getStatusCode());

    // Manager should also be able to view feedback details
    HttpHeaders managerHeader = createHeaders();
    managerHeader.set("Authorization", "Bearer " + managertoken);
    HttpEntity<String> managerRequest = new HttpEntity<>(managerHeader);
    ResponseEntity<String> managerResponse = restTemplate.exchange("/api/feedback/" + feedbackId, HttpMethod.GET, managerRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, managerResponse.getStatusCode());
}


@Test
@Order(14)
void backend_testGetAllFeedback() throws Exception {
    Assertions.assertNotNull(clienttoken, "User token should not be null");
    Assertions.assertNotNull(managertoken, "Manager token should not be null");

    // Client should be able to view all feedback
    HttpHeaders clientHeader = createHeaders();
    clientHeader.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<String> clientRequest = new HttpEntity<>(clientHeader);
    ResponseEntity<String> clientResponse = restTemplate.exchange("/api/feedback", HttpMethod.GET, clientRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, clientResponse.getStatusCode());

    // Manager should also be able to view all feedback
    HttpHeaders managerHeader = createHeaders();
    managerHeader.set("Authorization", "Bearer " + managertoken);
    HttpEntity<String> managerRequest = new HttpEntity<>(managerHeader);
    ResponseEntity<String> managerResponse = restTemplate.exchange("/api/feedback", HttpMethod.GET, managerRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, managerResponse.getStatusCode());
}

@Test
@Order(15)
void backend_testGetFeedbackByUserId() throws Exception {
    Assertions.assertNotNull(clienttoken, "User token should not be null");
    Assertions.assertNotNull(managertoken, "Manager token should not be null");

    Long userId = 2L; // Assuming userId 2 exists and has feedback

    // Client should be able to view feedback for their user ID
    HttpHeaders clientHeader = createHeaders();
    clientHeader.set("Authorization", "Bearer " + clienttoken);
    HttpEntity<String> clientRequest = new HttpEntity<>(clientHeader);
    ResponseEntity<String> clientResponse = restTemplate.exchange("/api/feedback/user/" + userId, HttpMethod.GET, clientRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, clientResponse.getStatusCode());

    // Manager should NOT be able to view feedback for any user
    HttpHeaders managerHeader = createHeaders();
    managerHeader.set("Authorization", "Bearer " + managertoken);
    HttpEntity<String> managerRequest = new HttpEntity<>(managerHeader);
    ResponseEntity<String> managerResponse = restTemplate.exchange("/api/feedback/user/" + userId, HttpMethod.GET, managerRequest, String.class);
    Assertions.assertEquals(HttpStatus.FORBIDDEN, managerResponse.getStatusCode());
}


}