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
public enum StateTransition {

    INITIAL_TO_PENDING_ENROLLMENT_OVERVIEW(EnrollmentState.INITIAL, EnrollmentEvent.NEXT, hasPendingEnrollmentsCondition(), EnrollmentState.PENDING_ENROLLMENT_OVERVIEW),
    PENDING_ENROLLMENT_OVERVIEW_TO_PENDING_ENROLLMENT_OVERVIEW(EnrollmentState.PENDING_ENROLLMENT_OVERVIEW, EnrollmentEvent.OPT_OUT_OF_ENROLLMENT, hasPendingEnrollmentsCondition(), EnrollmentState.PENDING_ENROLLMENT_OVERVIEW),
    PENDING_ENROLLMENT_OVERVIEW_TO_MYCLIQ_WITH_INTRO(EnrollmentState.PENDING_ENROLLMENT_OVERVIEW, EnrollmentEvent.OPT_OUT_OF_ENROLLMENT, doesNotHavePendingEnrollmentsAndHasNotSeenMycliqCondition(), EnrollmentState.MYCLIQ_WITH_INTRO),
    PENDING_ENROLLMENT_OVERVIEW_TO_MYCLIQ_WITHOUT_INTRO(EnrollmentState.PENDING_ENROLLMENT_OVERVIEW, EnrollmentEvent.OPT_OUT_OF_ENROLLMENT, doesNotHavePendingEnrollmentsAndHasSeenMycliqCondition(), EnrollmentState.MYCLIQ_WITHOUT_INTRO),
    INITIAL_TO_MYCLIQ_WITH_INTRO(EnrollmentState.INITIAL, EnrollmentEvent.NEXT, doesNotHavePendingEnrollmentsAndHasNotSeenMycliqCondition(), EnrollmentState.MYCLIQ_WITH_INTRO),
    INITIAL_TO_MYCLIQ_WITHOUT_INTRO(EnrollmentState.INITIAL, EnrollmentEvent.NEXT, doesNotHavePendingEnrollmentsAndHasSeenMycliqCondition(), EnrollmentState.MYCLIQ_WITHOUT_INTRO),
    ;

    private final EnrollmentState initialState;
    private final EnrollmentEvent transitionEvent;
    private final Predicate<StateContext<EnrollmentState, EnrollmentEvent>> guardPredicate;
//    or private final Predicate<Long /*userId*/> condition; - this will be better if pasing argumen in statemachine is possible
    private final EnrollmentState targetState;

    public static EnrollmentState getRootState() {
        Set<EnrollmentState> targetStates = getTargetStates();
        return Arrays.stream(EnrollmentState.values())
                .filter(state -> !targetStates.contains(state))
        .findFirst()
        .orElseThrow();
    }

    public static Set<EnrollmentState> getEndStates() {
        Set<EnrollmentState> targetStates = getTargetStates();
        Set<EnrollmentState> initialStates = getInitialStates();

        return Arrays.stream(EnrollmentState.values())
                .filter(targetStates::contains)
                .filter(state -> !initialStates.contains(state))
                .collect(Collectors.toSet());
    }

    public static Set<EnrollmentState> getIntermediateStates() {
        EnrollmentState rootState = getRootState();
        Set<EnrollmentState> endStates = getEndStates();

        return Arrays.stream(EnrollmentState.values())
                .filter(state -> state != rootState)
                .filter(state -> !endStates.contains(state))
                .collect(Collectors.toSet());
    }

    private static Set<EnrollmentState> getTargetStates() {
        return Arrays.stream(StateTransition.values()).map(StateTransition::getTargetState).collect(Collectors.toSet());
    }

    private static Set<EnrollmentState> getInitialStates() {
        return Arrays.stream(StateTransition.values()).map(StateTransition::getInitialState).collect(Collectors.toSet());
    }

}
