package com.statemachine.statemachine.controller;

import com.statemachine.statemachine.dto.GdprConsentDto;
import com.statemachine.statemachine.service.GdprConsentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/gdpr-consent")
public class GdprConsentController {

    private final GdprConsentService gdprConsentService;

    public GdprConsentController(GdprConsentService gdprConsentService) {
        this.gdprConsentService = gdprConsentService;
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody GdprConsentDto gdprConsentDto) {
        gdprConsentService.update(gdprConsentDto);
        return ResponseEntity.ok()
                .build();
    }

}
