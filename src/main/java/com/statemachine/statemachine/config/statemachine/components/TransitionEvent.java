package com.statemachine.statemachine.config.statemachine.components;

public enum TransitionEvent {
    NEXT,
    OPT_OUT_OF_ENROLLMENT,
    SKIP_ENROLLMENT_THIS_TIME,
    CONTINUE_TO_ENROLLMENT,
    PROFILE_ENRICHMENT_FIELDS_SUBMITTED,
    MATCH_PREFS_SUBMITTED,
    SUGGESTION_MADE,
    MATCH_REQUESTED
}
