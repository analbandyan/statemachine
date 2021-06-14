package com.statemachine.statemachine.service;

import com.statemachine.statemachine.config.components.EnrollmentEvent;
import com.statemachine.statemachine.config.components.EnrollmentState;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class StateMachineWrapperService {

    private final StateMachineService<EnrollmentState, EnrollmentEvent> stateMachineService;

    public StateMachineWrapperService(StateMachineService<EnrollmentState, EnrollmentEvent> stateMachineService) {
        this.stateMachineService = stateMachineService;
    }

    public State<EnrollmentState, EnrollmentEvent> submit(Long userId, EnrollmentEvent event, Map<String, String> details) {
        StateMachine<EnrollmentState, EnrollmentEvent> stateMachine = stateMachineService.acquireStateMachine(userId.toString());
//        stateMachine.start();

        stateMachine.sendEvent(
                Mono.just(MessageBuilder
                        .withPayload(event)
                        .copyHeaders(details)
                        .build())
        ).blockLast();

        return stateMachine.getState();
    }

}
