package com.statemachine.statemachine.service;

import com.statemachine.statemachine.dao.GdprConsentRepository;
import com.statemachine.statemachine.domain.GdprConsent;
import com.statemachine.statemachine.dto.GdprConsentDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class GdprConsentService {

    private final GdprConsentRepository gdprConsentRepository;

    public GdprConsentService(GdprConsentRepository gdprConsentRepository) {
        this.gdprConsentRepository = gdprConsentRepository;
    }

    public void update(GdprConsentDto gdprConsentDto) {
        GdprConsent gdprConsent = new GdprConsent();
        gdprConsent.setConsent(gdprConsentDto.getConsent());
        gdprConsentRepository.save(gdprConsent);
    }

    public Optional<Instant> getLastGdprConsentUpdateTime() {
        return gdprConsentRepository.findLatestConsent()
                .map(GdprConsent::getCreationTime);
    }
}
