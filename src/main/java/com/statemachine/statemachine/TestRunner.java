package com.statemachine.statemachine;

import com.statemachine.statemachine.config.statemachine.components.StateTransitionConditions;
import com.statemachine.statemachine.config.statemachine.components.TransitionEvent;
import com.statemachine.statemachine.config.statemachine.components.TransitionState;
import com.statemachine.statemachine.dao.TransitionLogRepository;
import com.statemachine.statemachine.domain.TransitionLog;
import com.statemachine.statemachine.service.StateMachineWrapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TestRunner implements ApplicationRunner {

    private final StateMachineFactory<TransitionState, TransitionEvent> stateMachineFactory;
    private final TransitionLogRepository transitionLogRepository;
    private final StateMachineWrapperService stateMachineWrapperService;

    public TestRunner(StateMachineFactory<TransitionState, TransitionEvent> stateMachineFactory,
                      TransitionLogRepository transitionLogRepository,
                      StateMachineWrapperService stateMachineWrapperService) {
        this.stateMachineFactory = stateMachineFactory;
        this.transitionLogRepository = transitionLogRepository;
        this.stateMachineWrapperService = stateMachineWrapperService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Long userId = 123L;
        State<TransitionState, TransitionEvent> state;
        List<TransitionLog> all = transitionLogRepository.findAll();

        StateTransitionConditions.hasPendingEnrollments = true;
        StateTransitionConditions.hasAlreadySeenMycliq = false;

        state = stateMachineWrapperService.submit(userId, TransitionEvent.NEXT, Map.of("key1", "val1", "key2", "val2"));
        log.info("current state = " + state.getId());
        all = transitionLogRepository.findAll();

        StateTransitionConditions.hasPendingEnrollments = true;
        StateTransitionConditions.hasAlreadySeenMycliq = false;
        state = stateMachineWrapperService.submit(userId, TransitionEvent.OPT_OUT_OF_ENROLLMENT, Map.of("key3", "val3", "key4", "val4"));
        log.info("current state = " + state.getId());
        all = transitionLogRepository.findAll();

        StateTransitionConditions.hasPendingEnrollments = false;
        StateTransitionConditions.hasAlreadySeenMycliq = false;
        state = stateMachineWrapperService.submit(userId, TransitionEvent.OPT_OUT_OF_ENROLLMENT, Map.of("key5", "val5", "key6", "val6"));
        log.info("current state = " + state.getId());
        all = transitionLogRepository.findAll();


        stateMachineWrapperService.resetStateMachineToState(userId, TransitionState.PENDING_ENROLLMENT_OVERVIEW);
        StateTransitionConditions.hasPendingEnrollments = false;
        StateTransitionConditions.hasAlreadySeenMycliq = true;
        state = stateMachineWrapperService.submit(userId, TransitionEvent.OPT_OUT_OF_ENROLLMENT, Map.of("key7", "val7", "key8", "val8"));
        log.info("current state = " + state.getId());
        all = transitionLogRepository.findAll();

        System.out.println();
    }
}
