package com.geoqueryai.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geoqueryai.backend.dto.AiQueryRequest;
import com.geoqueryai.backend.dto.AiQueryResponse;
import com.geoqueryai.backend.service.AiQueryService;


// =========================
// ALLOW REACT FRONTEND
// =========================
@CrossOrigin(
        origins = "http://localhost:5173"
)


// =========================
// AI REST CONTROLLER
// =========================
@RestController
@RequestMapping("/api/ai")
public class AiQueryController {

    private final AiQueryService aiQueryService;


    // =========================
    // CONSTRUCTOR
    // =========================
    public AiQueryController(
            AiQueryService aiQueryService) {

        this.aiQueryService =
                aiQueryService;
    }


    // =========================
    // AI NATURAL LANGUAGE QUERY
    // =========================
    // POST /api/ai/query
    @PostMapping("/query")
    public ResponseEntity<AiQueryResponse>
            query(

                    @RequestBody
                    AiQueryRequest request) {

        AiQueryResponse response =
                aiQueryService
                        .interpretQuery(
                                request.getQuery()
                        );

        return ResponseEntity.ok(
                response
        );
    }
}