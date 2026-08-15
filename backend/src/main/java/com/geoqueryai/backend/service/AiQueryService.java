package com.geoqueryai.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geoqueryai.backend.dto.AiQueryResponse;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class AiQueryService {

    // =========================
    // OPENAI CONFIGURATION
    // =========================
    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.model:gpt-5.6}")
    private String model;


    // =========================
    // JSON MAPPER
    // =========================
    // Spring Boot 4 uses Jackson 3.
    private final JsonMapper jsonMapper;


    // =========================
    // HTTP CLIENT
    // =========================
    private final HttpClient httpClient =
            HttpClient.newHttpClient();


    // =========================
    // CONSTRUCTOR
    // =========================
    public AiQueryService(
            JsonMapper jsonMapper) {

        this.jsonMapper = jsonMapper;
    }


    // =========================
    // INTERPRET USER GIS QUERY
    // =========================
    public AiQueryResponse interpretQuery(
            String userQuery) {

        // =========================
        // VALIDATE QUERY
        // =========================
        if (userQuery == null
                || userQuery.isBlank()) {

            throw new IllegalArgumentException(
                    "AI query cannot be empty."
            );
        }


        // =========================
        // CHECK API KEY
        // =========================
        if (apiKey == null
                || apiKey.isBlank()) {

            throw new IllegalStateException(
                    "OPENAI_API_KEY is not configured."
            );
        }


        try {

            // =========================
            // AI SYSTEM INSTRUCTIONS
            // =========================
            String instructions = """
                You are GeoQueryAI.

                Your job is to convert a user's GIS request
                into structured JSON.

                Supported actions:

                1. nearby
                   Requires:
                   - latitude
                   - longitude
                   - distance in meters

                2. contains
                   Requires:
                   - latitude
                   - longitude

                3. help
                   Use this when required information is missing
                   or the request is unsupported.

                Important rules:

                - Never invent coordinates.
                - Never invent a distance.
                - If required information is missing,
                  return action "help".
                - Keep the message short.

                Return JSON using exactly these fields:

                {
                  "action": "nearby | contains | help",
                  "message": "short message",
                  "latitude": number or null,
                  "longitude": number or null,
                  "distance": number or null
                }
                """;


            // =========================
            // BUILD OPENAI REQUEST BODY
            // =========================
            Map<String, Object> requestBody =
                    Map.of(
                            "model",
                            model,

                            "input",
                            List.of(

                                    Map.of(
                                            "role",
                                            "system",
                                            "content",
                                            instructions
                                    ),

                                    Map.of(
                                            "role",
                                            "user",
                                            "content",
                                            userQuery
                                    )
                            )
                    );


            // =========================
            // CONVERT BODY TO JSON
            // =========================
            String jsonBody =
                    jsonMapper.writeValueAsString(
                            requestBody
                    );


            // =========================
            // CREATE HTTP REQUEST
            // =========================
            HttpRequest request =
                    HttpRequest
                            .newBuilder()

                            .uri(
                                    URI.create(
                                            "https://api.openai.com/v1/responses"
                                    )
                            )

                            .header(
                                    "Authorization",
                                    "Bearer " + apiKey
                            )

                            .header(
                                    "Content-Type",
                                    "application/json"
                            )

                            .POST(
                                    HttpRequest
                                            .BodyPublishers
                                            .ofString(
                                                    jsonBody
                                            )
                            )

                            .build();


            // =========================
            // SEND REQUEST TO OPENAI
            // =========================
            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse
                                    .BodyHandlers
                                    .ofString()
                    );


            // =========================
            // CHECK HTTP STATUS
            // =========================
            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new RuntimeException(
                        "OpenAI request failed. HTTP "
                                + response.statusCode()
                                + ". Response: "
                                + response.body()
                );
            }


            // =========================
            // PARSE OPENAI RESPONSE
            // =========================
            JsonNode root =
                    jsonMapper.readTree(
                            response.body()
                    );


            // =========================
            // EXTRACT AI TEXT
            // =========================
            String outputText =
                    extractOutputText(root);


            if (outputText == null
                    || outputText.isBlank()) {

                throw new RuntimeException(
                        "OpenAI returned no usable response."
                );
            }


            // =========================
            // REMOVE OPTIONAL
            // MARKDOWN CODE FENCES
            // =========================
            outputText =
                    outputText
                            .replace(
                                    "```json",
                                    ""
                            )
                            .replace(
                                    "```",
                                    ""
                            )
                            .trim();


            // =========================
            // CONVERT AI JSON
            // TO RESPONSE DTO
            // =========================
            return jsonMapper.readValue(
                    outputText,
                    AiQueryResponse.class
            );


        } catch (InterruptedException exception) {

            // Restore interrupted state
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "AI request was interrupted.",
                    exception
            );


        } catch (Exception exception) {

            throw new RuntimeException(
                    "AI query failed: "
                            + exception.getMessage(),
                    exception
            );
        }
    }


    // =========================
    // EXTRACT OUTPUT TEXT
    // FROM OPENAI RESPONSES API
    // =========================
    private String extractOutputText(
            JsonNode root) {

        JsonNode output =
                root.path("output");


        // =========================
        // MAKE SURE OUTPUT IS ARRAY
        // =========================
        if (!output.isArray()) {
            return null;
        }


        // =========================
        // LOOP THROUGH OUTPUT ITEMS
        // =========================
        for (JsonNode item : output) {

            // We want message objects
            if (!"message".equals(
                    item
                            .path("type")
                            .asText()
            )) {

                continue;
            }


            JsonNode content =
                    item.path("content");


            if (!content.isArray()) {
                continue;
            }


            // =========================
            // FIND TEXT CONTENT
            // =========================
            for (JsonNode part : content) {

                String type =
                        part
                                .path("type")
                                .asText();


                if (
                        "output_text".equals(type)
                        &&
                        part.has("text")
                ) {

                    return part
                            .path("text")
                            .asText();
                }


                // Fallback
                if (part.has("text")) {

                    return part
                            .path("text")
                            .asText();
                }
            }
        }


        return null;
    }
}