package com.statemachine.statemachine.service;

import com.statemachine.statemachine.config.statemachine.components.TransitionEvent;
import com.statemachine.statemachine.config.statemachine.components.TransitionState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StateMachinePersisterService {

    private final StateMachinePersister<TransitionState, TransitionEvent, String> persister;

    public StateMachinePersisterService(StateMachineRuntimePersister<TransitionState, TransitionEvent, String> stateMachineRuntimePersister) {
        persister = new DefaultStateMachinePersister<>(stateMachineRuntimePersister);
    }

    public void persist(String stateMachineId, StateMachine<TransitionState, TransitionEvent> stateMachine) {
        try {
            persister.persist(stateMachine, stateMachineId);
        } catch (Exception e) {
            log.error("Failed to persist state machine state: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
