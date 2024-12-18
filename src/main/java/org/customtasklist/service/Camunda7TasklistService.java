package org.customtasklist.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class Camunda7TasklistService {

    @Value("${camunda7.base.url}")
    private String baseUrl;

    @Value("${camunda7.username}")
    private String username;

    @Value("${camunda7.password}")
    private String password;

    public ResponseEntity<String> completeTask(String taskId, Map<String, Object> variables) {
        String url = baseUrl + "engine-rest/task/" + taskId + "/complete";

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth("demo", "demo");
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Ensure variables are included in the request
            Map<String, Object> requestBody = variables != null
                    ? Map.of("variables", variables)
                    : Map.of();

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            System.out.println("Sending request to URL: " + url);
            System.out.println("Request Body: " + requestBody);

            restTemplate.postForEntity(url, request, String.class);

            return ResponseEntity.ok("Task " + taskId + " completed successfully in Camunda 7!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error completing task in Camunda 7: " + e.getMessage());
        }
    }


    /**
     * Fetches all tasks from Camunda 7's REST API.
     *
     * @return a ResponseEntity containing a list of tasks as JSON
     */
    public ResponseEntity<String> getAllTasks() {
        try {
            String tasksUrl = baseUrl + "engine-rest/task"; // Construct the endpoint for getting tasks
            System.out.println("Fetching tasks from URL: " + tasksUrl);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(username, password);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    tasksUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response;
        } catch (Exception e) {
            System.err.println("Error fetching tasks from Camunda 7: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
