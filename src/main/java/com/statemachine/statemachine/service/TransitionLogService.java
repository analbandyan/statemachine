package com.statemachine.statemachine.service;

import com.statemachine.statemachine.dao.TransitionLogRepository;
import com.statemachine.statemachine.domain.TransitionLog;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransitionLogService {

    private final TransitionLogRepository transitionLogRepository;

    public TransitionLogService(TransitionLogRepository transitionLogRepository) {
        this.transitionLogRepository = transitionLogRepository;
    }

    public Optional<TransitionLog> findLatestTransition(Long userId) {
        return transitionLogRepository.findLatestTransition(userId);
    }

}
