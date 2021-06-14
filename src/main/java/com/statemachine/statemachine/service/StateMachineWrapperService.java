package com.statemachine.statemachine.service;

import com.statemachine.statemachine.config.components.TransitionEvent;
import com.statemachine.statemachine.config.components.TransitionState;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

@Service
public class StateMachineWrapperService {

    private final StateMachineService<TransitionState, TransitionEvent> stateMachineService;

    public StateMachineWrapperService(StateMachineService<TransitionState, TransitionEvent> stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public State<TransitionState, TransitionEvent> submit(Long userId, TransitionEvent event, Map<String, String> details) {
        String machineId = getStateMachineIdByUserId(userId);
        StateMachine<TransitionState, TransitionEvent> stateMachine = stateMachineService.acquireStateMachine(machineId);

        stateMachine.sendEvent(
                Mono.just(MessageBuilder
                        .withPayload(event)
                        .copyHeaders(details)
                        .build())
        ).blockLast();

        stateMachineService.releaseStateMachine(machineId);

        return stateMachine.getState();
    }

    public void resetStateMachineToState(Long userId, TransitionState transitionState) {
        String machineId = getStateMachineIdByUserId(userId);
        StateMachine<TransitionState, TransitionEvent> stateMachine = stateMachineService.acquireStateMachine(machineId, true);

        stateMachine.getStateMachineAccessor().doWithAllRegions(
                new Consumer<StateMachineAccess<TransitionState, TransitionEvent>>() {

                    @Override
                    public void accept(StateMachineAccess<TransitionState, TransitionEvent> stateMachineAccess) {
                        stateMachineAccess.resetStateMachine(
                                new DefaultStateMachineContext<>(transitionState, null, null, null, null, stateMachine.getId())
                        );
                    }

                }
        );
    }

    private static String getStateMachineIdByUserId(Long userId) {
        return userId.toString();
    }

}
