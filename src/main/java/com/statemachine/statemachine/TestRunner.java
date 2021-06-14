package com.statemachine.statemachine;

import com.statemachine.statemachine.config.components.EnrollmentEvent;
import com.statemachine.statemachine.config.components.EnrollmentState;
import com.statemachine.statemachine.config.components.StateTransitionConditions;
import com.statemachine.statemachine.domain.TransitionLog;
import com.statemachine.statemachine.dto.UserEnrollmentTransitionRepository;
import com.statemachine.statemachine.service.EnrollmentStateTransitionService;
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

    private final StateMachineFactory<EnrollmentState, EnrollmentEvent> stateMachineFactory;
    private final EnrollmentStateTransitionService enrollmentStateTransitionService;
    private final UserEnrollmentTransitionRepository userEnrollmentTransitionRepository;
    private final StateMachineWrapperService stateMachineWrapperService;

    public TestRunner(StateMachineFactory<EnrollmentState, EnrollmentEvent> stateMachineFactory,
                      EnrollmentStateTransitionService enrollmentStateTransitionService, UserEnrollmentTransitionRepository userEnrollmentTransitionRepository,
                      StateMachineWrapperService stateMachineWrapperService) {
        this.stateMachineFactory = stateMachineFactory;
        this.enrollmentStateTransitionService = enrollmentStateTransitionService;
        this.userEnrollmentTransitionRepository = userEnrollmentTransitionRepository;
        this.stateMachineWrapperService = stateMachineWrapperService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Long userId = 123L;
        State<EnrollmentState, EnrollmentEvent> state;
        List<TransitionLog> all = userEnrollmentTransitionRepository.findAll();


        StateTransitionConditions.hasPendingEnrollments = true;
        StateTransitionConditions.hasAlreadySeenMycliq = false;

        state = stateMachineWrapperService.submit(userId, EnrollmentEvent.NEXT, Map.of("key1", "val1", "key2", "val2"));
//        state = enrollmentStateTransitionService.submit(userId, EnrollmentEvent.NEXT, Map.of("key1", "val1", "key2", "val2"));
        log.info("current state = " + state.getId());
        all = userEnrollmentTransitionRepository.findAll();

        StateTransitionConditions.hasPendingEnrollments = true;
        StateTransitionConditions.hasAlreadySeenMycliq = false;
        state = stateMachineWrapperService.submit(userId, EnrollmentEvent.OPT_OUT_OF_ENROLLMENT, Map.of("key3", "val3", "key4", "val4"));
//        state = enrollmentStateTransitionService.submit(userId, EnrollmentEvent.OPT_OUT_OF_ENROLLMENT, Map.of("key3", "val3", "key4", "val4"));
        log.info("current state = " + state.getId());
        all = userEnrollmentTransitionRepository.findAll();

        StateTransitionConditions.hasPendingEnrollments = false;
        StateTransitionConditions.hasAlreadySeenMycliq = false;
        state = stateMachineWrapperService.submit(userId, EnrollmentEvent.OPT_OUT_OF_ENROLLMENT, Map.of("key5", "val5", "key6", "val6"));
//        state = enrollmentStateTransitionService.submit(userId, EnrollmentEvent.OPT_OUT_OF_ENROLLMENT, Map.of("key5", "val5", "key6", "val6"));
        log.info("current state = " + state.getId());
        all = userEnrollmentTransitionRepository.findAll();

        System.out.println();
    }
}
