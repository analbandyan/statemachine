package com.statemachine.statemachine.service;

import com.statemachine.statemachine.config.statemachine.components.TransitionEvent;
import com.statemachine.statemachine.config.statemachine.components.TransitionState;
import com.statemachine.statemachine.dao.TransitionLogRepository;
import com.statemachine.statemachine.domain.TransitionLog;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class CustomPersistingStateMachineInterceptorService extends JpaPersistingStateMachineInterceptor<TransitionState, TransitionEvent, String> {

    private final TransitionLogRepository transitionLogRepository;

    public CustomPersistingStateMachineInterceptorService(JpaStateMachineRepository jpaStateMachineRepository, TransitionLogRepository transitionLogRepository) {
        super(jpaStateMachineRepository);
        this.transitionLogRepository = transitionLogRepository;
    }

    @Override
    public void preStateChange(State<TransitionState, TransitionEvent> state, Message<TransitionEvent> message, Transition<TransitionState, TransitionEvent> transition,
                               StateMachine<TransitionState, TransitionEvent> stateMachine, StateMachine<TransitionState, TransitionEvent> rootStateMachine) {

        super.preStateChange(state, message, transition, stateMachine, rootStateMachine);
        createTransitionLog(message, transition, stateMachine);
    }

    private void createTransitionLog(Message<TransitionEvent> message, Transition<TransitionState, TransitionEvent> transition, StateMachine<TransitionState, TransitionEvent> stateMachine) {
        TransitionLog transitionLog = constructTransitionLog(message, transition, stateMachine);
        transitionLogRepository.save(transitionLog);
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
        transitionLog.setCreationTime(Instant.now());
        transitionLog.setDetails(details);

        return transitionLog;
    }

}
