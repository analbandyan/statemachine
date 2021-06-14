package com.statemachine.statemachine.dto;

import com.statemachine.statemachine.domain.TransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransitionLogRepository extends JpaRepository<TransitionLog, Long> {

    default Optional<TransitionLog> findLatestTransition(Long userId) {
        return findTopByUserIdOrderById(userId);
    }

    Optional<TransitionLog> findTopByUserIdOrderById(Long userId);

}
