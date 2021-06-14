package com.statemachine.statemachine.config.components;

public enum EnrollmentState {

    INITIAL,
    PENDING_ENROLLMENT_OVERVIEW,
//    UNENROLLED,
//    ENROLLMENT_INTRO,
    PROFILE_ENRICHMENT_FIELDS,
    MATCH_PREFERENCES,
    SUGGEST_A_MATCH,
    SELF_MATCH,
    MYCLIQ_WITH_INTRO,
    MYCLIQ_WITHOUT_INTRO

}
