package com.statemachine.statemachine.config.components;

import org.springframework.statemachine.StateContext;

import java.util.function.Predicate;

public class StateTransitionConditions {

    public static boolean hasPendingEnrollments = false;
    public static boolean hasAlreadySeenMycliq = false;

    public static Predicate<StateContext<EnrollmentState, EnrollmentEvent>> hasPendingEnrollmentsCondition() {
        return (context) -> hasPendingEnrollments;
    }


    public static Predicate<StateContext<EnrollmentState, EnrollmentEvent>> doesNotHavePendingEnrollmentsCondition() {
        return hasPendingEnrollmentsCondition().negate();
    }

    public static Predicate<StateContext<EnrollmentState, EnrollmentEvent>> hasAlreadySeenMycliqCondition() {
        return (context) -> hasAlreadySeenMycliq;
    }

    public static Predicate<StateContext<EnrollmentState, EnrollmentEvent>> hasNotSeenMycliqCondition() {
        return hasAlreadySeenMycliqCondition().negate();
    }

    public static Predicate<StateContext<EnrollmentState, EnrollmentEvent>> doesNotHavePendingEnrollmentsAndHasNotSeenMycliqCondition() {
        return doesNotHavePendingEnrollmentsCondition().and(hasNotSeenMycliqCondition());
    }

    public static Predicate<StateContext<EnrollmentState, EnrollmentEvent>> doesNotHavePendingEnrollmentsAndHasSeenMycliqCondition() {
        return doesNotHavePendingEnrollmentsCondition().and(hasAlreadySeenMycliqCondition());
    }


}
