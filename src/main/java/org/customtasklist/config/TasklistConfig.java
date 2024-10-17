    package org.customtasklist.config;

    import io.camunda.tasklist.CamundaTaskListClient;
    import io.camunda.tasklist.exception.TaskListException;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class TasklistConfig {

        @Value("${camunda.client.id}")
        String camundaClientId;

        @Value("${camunda.client.secret}")
        String camundaClientSecret;

        @Value("${camunda.tasklist.url}")
        String camundaTasklistUrl;


        /**
         * Configures and provides a CamundaTaskListClient bean for interacting with the Camunda Tasklist API.
         * The client uses SaaS authentication with the provided client ID and secret to authenticate with the Tasklist URL.
         *
         * @return a configured {@link CamundaTaskListClient} for interacting with the Camunda Tasklist
         * @throws TaskListException if any issues occur during client creation
         */
        @Bean
        public CamundaTaskListClient camundaTaskListClient() throws TaskListException {
            return CamundaTaskListClient
                    .builder()
                    .taskListUrl(camundaTasklistUrl)
                    .saaSAuthentication(camundaClientId, camundaClientSecret)
                    .build();
        }
    }
