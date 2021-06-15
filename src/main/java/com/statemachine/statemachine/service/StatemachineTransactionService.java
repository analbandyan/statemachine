package com.statemachine.statemachine.service;

import com.statemachine.statemachine.config.statemachine.components.TransitionEvent;
import com.statemachine.statemachine.config.statemachine.components.TransitionState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class StatemachineTransactionService {

    private final StateMachineService<TransitionState, TransitionEvent> stateMachineService;

    public StatemachineTransactionService(StateMachineService<TransitionState, TransitionEvent> stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public <T> T doInTransaction(Long userId, Function<StateMachine<TransitionState, TransitionEvent>, T> operation) {
        String stateMachineId = getStateMachineIdByUserId(userId);
        StateMachine<TransitionState, TransitionEvent> stateMachine = stateMachineService.acquireStateMachine(stateMachineId, true);
        T result = operation.apply(stateMachine);
        stateMachineService.releaseStateMachine(stateMachineId, true);
        return result;
    }

    public void doInTransactionWithoutResult(Long userId, Consumer<StateMachine<TransitionState, TransitionEvent>> operation) {
        doInTransaction(userId, statemachine -> {
            operation.accept(statemachine);
            return null;
        });
    }

    private static String getStateMachineIdByUserId(Long userId) {
        return userId.toString();
    }
}
