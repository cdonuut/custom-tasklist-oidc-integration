# Custom Tasklist OIDC Integration

This project is a custom tasklist application that integrates with Camunda Tasklist via OpenID Connect (OIDC) for authentication.

## Prerequisites

- **Java 11 or higher** installed
- **Maven** installed
- **Spring Boot** compatible IDE (e.g., IntelliJ IDEA)
- A **Camunda Tasklist** and **OIDC provider** (e.g., Azure AD)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/cdonuut/custom-tasklist-oidc-integration.git
cd custom-tasklist-oidc-integration
```

### 2. Install dependencies

Make sure you have Maven installed, then install the dependencies by running:

```bash
mvn clean install
```

### 3. Create the `application.properties` file

You need to create an `application.properties` file in the `src/main/resources/` directory. This file will contain your specific configurations for Camunda and OIDC. Here is a template:

```properties
# Application name
spring.application.name=custom-tasklist

# Camunda Tasklist integration
camunda.client.id=YOUR_CAMUNDA_CLIENT_ID
camunda.client.secret=YOUR_CAMUNDA_CLIENT_SECRET
camunda.tasklist.url=YOUR_CAMUNDA_TASKLIST_URL

# OIDC configuration
spring.security.oauth2.client.registration.custom.client-id=YOUR_OIDC_CLIENT_ID
spring.security.oauth2.client.registration.custom.client-secret=YOUR_OIDC_CLIENT_SECRET
spring.security.oauth2.client.registration.custom.scope=openid,profile,email
spring.security.oauth2.client.registration.custom.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.custom.redirect-uri=http://localhost:8080/login/oauth2/code/custom

# OIDC Provider (e.g., Azure AD)
spring.security.oauth2.client.provider.custom.issuer-uri=YOUR_OIDC_ISSUER_URI
```

Replace the placeholders (e.g., `YOUR_CAMUNDA_CLIENT_ID`, `YOUR_OIDC_CLIENT_ID`) with your actual values.

### 4. Run the Application

Once the `application.properties` is set up, you can run the application using Maven:

```bash
mvn spring-boot:run
```

### 5. Access the Application

The application will be available at [http://localhost:8080](http://localhost:8080). You will be redirected to your OIDC provider for authentication.

## Contributing

Feel free to submit issues or contribute via pull requests.

---

*This README and the code comments were generated with ❤️ by ChatGPT.*
