package com.statemachine.statemachine.dao;

import com.statemachine.statemachine.domain.GdprConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GdprConsentRepository extends JpaRepository<GdprConsent, Long> {

    default Optional<GdprConsent> findLatestConsent() {
        return findTopByOrderByCreationTimeDesc();
    }

    Optional<GdprConsent> findTopByOrderByCreationTimeDesc();

}
