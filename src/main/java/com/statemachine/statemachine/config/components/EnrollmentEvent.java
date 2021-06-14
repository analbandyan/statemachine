package com.statemachine.statemachine.config.components;

public enum EnrollmentEvent {
    NEXT,
    OPT_OUT_OF_ENROLLMENT,
    SKIP_ENROLLMENT_THIS_TIME,
    CONTINUE_TO_ENROLLMENT,
//    ENROLLMENT_INTRO_PASSED,
    PROFILE_ENRICHMENT_FIELDS_SUBMITTED,
    MATCH_PREFS_SUBMITTED,
    SUGGESTION_MADE,
    MATCH_REQUESTED
}