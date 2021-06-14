package com.statemachine.statemachine.service;

import com.statemachine.statemachine.config.components.EnrollmentEvent;
import com.statemachine.statemachine.config.components.EnrollmentState;
import com.statemachine.statemachine.config.components.StateTransition;
import com.statemachine.statemachine.domain.TransitionLog;
import com.statemachine.statemachine.dto.UserEnrollmentTransitionRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class EnrollmentStateTransitionService {

    private final UserEnrollmentTransitionRepository userEnrollmentTransitionRepository;
    private final StateMachineFactory<EnrollmentState, EnrollmentEvent> stateMachineFactory;

    public EnrollmentStateTransitionService(UserEnrollmentTransitionRepository userEnrollmentTransitionRepository,
                                            StateMachineFactory<EnrollmentState, EnrollmentEvent> stateMachineFactory) {
        this.userEnrollmentTransitionRepository = userEnrollmentTransitionRepository;
        this.stateMachineFactory = stateMachineFactory;
    }

    public State<EnrollmentState, EnrollmentEvent> submit(Long userId, EnrollmentEvent event, Map<String, String> details) {
        StateMachine<EnrollmentState, EnrollmentEvent> stateMachine = loadStateMachine(userId);
        stateMachine.sendEvent(
                Mono.just(MessageBuilder
                        .withPayload(event)
                        .copyHeaders(details)
                        .build())
        ).blockLast();
        return stateMachine.getState();
    }

    private StateMachine<EnrollmentState, EnrollmentEvent> loadStateMachine(Long userId) {
        StateMachine<EnrollmentState, EnrollmentEvent> stateMachine = stateMachineFactory.getStateMachine(userId.toString());

        stateMachine.stopReactively().block();

        initState(stateMachine, userId);

        stateMachine.startReactively().block();

        return stateMachine;
    }

    private void initState(StateMachine<EnrollmentState, EnrollmentEvent> stateMachine, Long userId) {
        EnrollmentState latestState = getLatestStateOrDefault(userId, StateTransition::getRootState);

        stateMachine.getStateMachineAccessor().doWithAllRegions(
                new Consumer<StateMachineAccess<EnrollmentState, EnrollmentEvent>>() {
                    @Override
                    public void accept(StateMachineAccess<EnrollmentState, EnrollmentEvent> stateMachineAccess) {
                        stateMachineAccess.resetStateMachine(
                                new DefaultStateMachineContext<>(latestState, null, null, null, null, stateMachine.getId())
                        );

                        stateMachineAccess.addStateMachineInterceptor(new StateMachineInterceptorAdapter<>() {
                            @Override
                            public void preStateChange(State<EnrollmentState, EnrollmentEvent> state, Message<EnrollmentEvent> message, Transition<EnrollmentState, EnrollmentEvent> transition,
                                                       StateMachine<EnrollmentState, EnrollmentEvent> stateMachine, StateMachine<EnrollmentState, EnrollmentEvent> rootStateMachine) {

                                TransitionLog transitionLog = constructUserEnrollmentTransition(message, transition, stateMachine);

                                userEnrollmentTransitionRepository.save(transitionLog);
                            }

                            private TransitionLog constructUserEnrollmentTransition(Message<EnrollmentEvent> message, Transition<EnrollmentState, EnrollmentEvent> transition, StateMachine<EnrollmentState, EnrollmentEvent> stateMachine) {
                                Long userId = Long.valueOf(stateMachine.getId());
                                EnrollmentState fromState = transition.getSource().getId();
                                EnrollmentState toState = transition.getTarget().getId();
                                EnrollmentEvent transitionEvent = message.getPayload();
                                MessageHeaders details = message.getHeaders();

                                TransitionLog transitionLog = new TransitionLog();
                                transitionLog.setUserId(userId);
                                transitionLog.setFromState(fromState);
                                transitionLog.setToState(toState);
                                transitionLog.setTransitionEvent(transitionEvent);
                                transitionLog.setDetails(details);

                                return transitionLog;
                            }
                        });
                    }
                }
        );
    }

    private EnrollmentState getLatestStateOrDefault(Long userId, Supplier<EnrollmentState> defaultStateSupplier) {
        Optional<TransitionLog> latestTransition = userEnrollmentTransitionRepository.findLatestTransition(userId);
        return latestTransition.map(TransitionLog::getToState).orElseGet(defaultStateSupplier);
    }

}
