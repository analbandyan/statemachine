package com.statemachine.statemachine.config.components;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.statemachine.statemachine.config.components.StateTransitionConditions.*;

@RequiredArgsConstructor
@Getter
public enum StateTransitionConfig {

    INITIAL_TO_PENDING_ENROLLMENT_OVERVIEW(TransitionState.INITIAL, TransitionEvent.NEXT, hasPendingEnrollmentsCondition(), TransitionState.PENDING_ENROLLMENT_OVERVIEW),
    PENDING_ENROLLMENT_OVERVIEW_TO_PENDING_ENROLLMENT_OVERVIEW(TransitionState.PENDING_ENROLLMENT_OVERVIEW, TransitionEvent.OPT_OUT_OF_ENROLLMENT, hasPendingEnrollmentsCondition(), TransitionState.PENDING_ENROLLMENT_OVERVIEW),
    PENDING_ENROLLMENT_OVERVIEW_TO_MYCLIQ_WITH_INTRO(TransitionState.PENDING_ENROLLMENT_OVERVIEW, TransitionEvent.OPT_OUT_OF_ENROLLMENT, doesNotHavePendingEnrollmentsAndHasNotSeenMycliqCondition(), TransitionState.MYCLIQ_WITH_INTRO),
    PENDING_ENROLLMENT_OVERVIEW_TO_MYCLIQ_WITHOUT_INTRO(TransitionState.PENDING_ENROLLMENT_OVERVIEW, TransitionEvent.OPT_OUT_OF_ENROLLMENT, doesNotHavePendingEnrollmentsAndHasSeenMycliqCondition(), TransitionState.MYCLIQ_WITHOUT_INTRO),
    INITIAL_TO_MYCLIQ_WITH_INTRO(TransitionState.INITIAL, TransitionEvent.NEXT, doesNotHavePendingEnrollmentsAndHasNotSeenMycliqCondition(), TransitionState.MYCLIQ_WITH_INTRO),
    INITIAL_TO_MYCLIQ_WITHOUT_INTRO(TransitionState.INITIAL, TransitionEvent.NEXT, doesNotHavePendingEnrollmentsAndHasSeenMycliqCondition(), TransitionState.MYCLIQ_WITHOUT_INTRO),
    ;

    private final TransitionState initialState;
    private final TransitionEvent transitionEvent;
    private final Predicate<StateContext<TransitionState, TransitionEvent>> guardPredicate;
//    or private final Predicate<Long /*userId*/> condition; - this will be better if pasing argumen in statemachine is possible
    private final TransitionState targetState;

    public static TransitionState getRootState() {
        Set<TransitionState> targetStates = getTargetStates();
        return Arrays.stream(TransitionState.values())
                .filter(state -> !targetStates.contains(state))
        .findFirst()
        .orElseThrow();
    }

    public static Set<TransitionState> getEndStates() {
        Set<TransitionState> targetStates = getTargetStates();
        Set<TransitionState> initialStates = getInitialStates();

        return Arrays.stream(TransitionState.values())
                .filter(targetStates::contains)
                .filter(state -> !initialStates.contains(state))
                .collect(Collectors.toSet());
    }

    public static Set<TransitionState> getIntermediateStates() {
        TransitionState rootState = getRootState();
        Set<TransitionState> endStates = getEndStates();

        return Arrays.stream(TransitionState.values())
                .filter(state -> state != rootState)
                .filter(state -> !endStates.contains(state))
                .collect(Collectors.toSet());
    }

    private static Set<TransitionState> getTargetStates() {
        return Arrays.stream(StateTransitionConfig.values()).map(StateTransitionConfig::getTargetState).collect(Collectors.toSet());
    }

    private static Set<TransitionState> getInitialStates() {
        return Arrays.stream(StateTransitionConfig.values()).map(StateTransitionConfig::getInitialState).collect(Collectors.toSet());
    }

}
