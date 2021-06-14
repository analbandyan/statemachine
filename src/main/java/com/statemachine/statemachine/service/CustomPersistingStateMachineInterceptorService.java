package com.statemachine.statemachine.service;

import com.statemachine.statemachine.config.components.TransitionEvent;
import com.statemachine.statemachine.config.components.TransitionState;
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
public class CustomPersistingStateMachineInterceptorService extends JpaPersistingStateMachineInterceptor<TransitionState, TransitionEvent, String> {

    private final UserEnrollmentTransitionRepository userEnrollmentTransitionRepository;

    public CustomPersistingStateMachineInterceptorService(JpaStateMachineRepository jpaStateMachineRepository, UserEnrollmentTransitionRepository userEnrollmentTransitionRepository) {
        super(jpaStateMachineRepository);
        this.userEnrollmentTransitionRepository = userEnrollmentTransitionRepository;
    }

    @Override
    public void preStateChange(State<TransitionState, TransitionEvent> state, Message<TransitionEvent> message, Transition<TransitionState, TransitionEvent> transition,
                               StateMachine<TransitionState, TransitionEvent> stateMachine, StateMachine<TransitionState, TransitionEvent> rootStateMachine) {

        super.preStateChange(state, message, transition, stateMachine, rootStateMachine);
        createTransitionLog(message, transition, stateMachine);
    }

    private void createTransitionLog(Message<TransitionEvent> message, Transition<TransitionState, TransitionEvent> transition, StateMachine<TransitionState, TransitionEvent> stateMachine) {
        TransitionLog transitionLog = constructTransitionLog(message, transition, stateMachine);
        userEnrollmentTransitionRepository.save(transitionLog);
    }

    private TransitionLog constructTransitionLog(Message<TransitionEvent> message, Transition<TransitionState, TransitionEvent> transition, StateMachine<TransitionState, TransitionEvent> stateMachine) {
        Long userId = Long.valueOf(stateMachine.getId());
        TransitionState fromState = transition.getSource().getId();
        TransitionState toState = transition.getTarget().getId();
        TransitionEvent transitionEvent = transition.getTrigger().getEvent();
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
