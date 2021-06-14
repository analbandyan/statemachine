package com.statemachine.statemachine.service;

import com.statemachine.statemachine.config.components.EnrollmentEvent;
import com.statemachine.statemachine.config.components.EnrollmentState;
import com.statemachine.statemachine.domain.TransitionLog;
import com.statemachine.statemachine.dto.UserEnrollmentTransitionRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomPersistingStateMachineInterceptorService extends JpaPersistingStateMachineInterceptor<EnrollmentState, EnrollmentEvent, String> {

    private final UserEnrollmentTransitionRepository userEnrollmentTransitionRepository;

    public CustomPersistingStateMachineInterceptorService(JpaStateMachineRepository jpaStateMachineRepository, UserEnrollmentTransitionRepository userEnrollmentTransitionRepository) {
        super(jpaStateMachineRepository);
        this.userEnrollmentTransitionRepository = userEnrollmentTransitionRepository;
    }

    @Override
    public void preStateChange(State<EnrollmentState, EnrollmentEvent> state, Message<EnrollmentEvent> message, Transition<EnrollmentState, EnrollmentEvent> transition,
                               StateMachine<EnrollmentState, EnrollmentEvent> stateMachine, StateMachine<EnrollmentState, EnrollmentEvent> rootStateMachine) {

        super.preStateChange(state, message, transition, stateMachine, rootStateMachine);
        createTransitionLog(message, transition, stateMachine);
    }

    private void createTransitionLog(Message<EnrollmentEvent> message, Transition<EnrollmentState, EnrollmentEvent> transition, StateMachine<EnrollmentState, EnrollmentEvent> stateMachine) {
        TransitionLog transitionLog = constructTransitionLog(message, transition, stateMachine);
        userEnrollmentTransitionRepository.save(transitionLog);
    }

    private TransitionLog constructTransitionLog(Message<EnrollmentEvent> message, Transition<EnrollmentState, EnrollmentEvent> transition, StateMachine<EnrollmentState, EnrollmentEvent> stateMachine) {
        Long userId = Long.valueOf(stateMachine.getId());
        EnrollmentState fromState = transition.getSource().getId();
        EnrollmentState toState = transition.getTarget().getId();
        EnrollmentEvent transitionEvent = transition.getTrigger().getEvent();
        MessageHeaders details = message.getHeaders();

        TransitionLog transitionLog = new TransitionLog();
        transitionLog.setUserId(userId);
        transitionLog.setFromState(fromState);
        transitionLog.setToState(toState);
        transitionLog.setTransitionEvent(transitionEvent);
        transitionLog.setDetails(details);

        return transitionLog;
    }

}
