package com.statemachine.statemachine.dto;

import com.statemachine.statemachine.domain.TransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEnrollmentTransitionRepository extends JpaRepository<TransitionLog, Long> {

    default Optional<TransitionLog> findLatestTransition(Long userId) {
        return findTopByUserIdOrderById(userId);
    }

    Optional<TransitionLog> findTopByUserIdOrderById(Long userId);

}
