package com.ias;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SecretsManagerHelper {

    private final SecretsManagerClient secretsManagerClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> getSecretAsMap(String secretName) {
        String secret = getSecret(secretName);
        return parseSecret(secret);
    }

    private String getSecret(String secretName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
        return response.secretString();
    }

    private Map<String, String> parseSecret(String secret) {
        try {
            return objectMapper.readValue(secret, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error al parsear el secreto", e);
        }
    }
}
