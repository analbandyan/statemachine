package com.statemachine.statemachine.service;

import com.statemachine.statemachine.config.statemachine.components.TransitionEvent;
import com.statemachine.statemachine.config.statemachine.components.TransitionState;
import org.springframework.messaging.support.MessageBuilder;
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
    private final StatemachineTransactionService statemachineTransactionService;
    private final StateMachinePersisterService stateMachinePersisterService;

    public StateMachineWrapperService(StateMachineService<TransitionState, TransitionEvent> stateMachineService,
                                      StatemachineTransactionService statemachineTransactionService,
                                      StateMachinePersisterService stateMachinePersisterService) {
        this.stateMachineService = stateMachineService;
        this.statemachineTransactionService = statemachineTransactionService;
        this.stateMachinePersisterService = stateMachinePersisterService;
    }

    public State<TransitionState, TransitionEvent> submit(Long userId, TransitionEvent event, Map<String, String> details) {
        return statemachineTransactionService.doInTransaction(userId, statemachine -> {
            statemachine.sendEvent(
                    Mono.just(MessageBuilder
                            .withPayload(event)
                            .copyHeaders(details)
                            .build())
            ).blockLast();
            return statemachine.getState();
        });
    }

    public void resetStateMachineToState(Long userId, TransitionState transitionState) {
        statemachineTransactionService.doInTransactionWithoutResult(userId, statemachine -> {

            statemachine.getStateMachineAccessor().doWithAllRegions(
                    new Consumer<StateMachineAccess<TransitionState, TransitionEvent>>() {
                        @Override
                        public void accept(StateMachineAccess<TransitionState, TransitionEvent> stateMachineAccess) {
                            stateMachineAccess.resetStateMachine(
                                    new DefaultStateMachineContext<>(transitionState, null, null, null, null, statemachine.getId())
                            );
                            stateMachinePersisterService.persist(statemachine.getId(), statemachine);
                        }

                    }
            );
        });

    }

}
