package com.statemachine.statemachine.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/statemachine")
public class StateMachineController {

    @GetMapping("/users/{userId}/any-endpoint")
    public ResponseEntity<Void> getPreferences(@PathVariable Long userId) {
        log.info("request handled");
        return ResponseEntity.ok()
                .build();
    }

}
