package com.statemachine.statemachine.config.statemachine.components;

import org.springframework.statemachine.StateContext;

import java.util.function.Predicate;

public class StateTransitionConditions {

    public static boolean hasPendingEnrollments = false;
    public static boolean hasAlreadySeenMycliq = false;

    public static Predicate<StateContext<TransitionState, TransitionEvent>> hasPendingEnrollmentsCondition() {
        return (context) -> hasPendingEnrollments;
    }


    public static Predicate<StateContext<TransitionState, TransitionEvent>> doesNotHavePendingEnrollmentsCondition() {
        return hasPendingEnrollmentsCondition().negate();
    }

    public static Predicate<StateContext<TransitionState, TransitionEvent>> hasAlreadySeenMycliqCondition() {
        return (context) -> hasAlreadySeenMycliq;
    }

    public static Predicate<StateContext<TransitionState, TransitionEvent>> hasNotSeenMycliqCondition() {
        return hasAlreadySeenMycliqCondition().negate();
    }

    public static Predicate<StateContext<TransitionState, TransitionEvent>> doesNotHavePendingEnrollmentsAndHasNotSeenMycliqCondition() {
        return doesNotHavePendingEnrollmentsCondition().and(hasNotSeenMycliqCondition());
    }

    public static Predicate<StateContext<TransitionState, TransitionEvent>> doesNotHavePendingEnrollmentsAndHasSeenMycliqCondition() {
        return doesNotHavePendingEnrollmentsCondition().and(hasAlreadySeenMycliqCondition());
    }


}
