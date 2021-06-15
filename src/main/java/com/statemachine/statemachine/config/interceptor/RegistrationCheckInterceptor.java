package com.statemachine.statemachine.config.interceptor;

import com.statemachine.statemachine.config.statemachine.components.StateTransitionConfig;
import com.statemachine.statemachine.domain.TransitionLog;
import com.statemachine.statemachine.exceptions.RegistrationCheckNotPassedException;
import com.statemachine.statemachine.service.GdprConsentService;
import com.statemachine.statemachine.service.TransitionLogService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
public class RegistrationCheckInterceptor implements HandlerInterceptor {

    private final GdprConsentService gdprConsentService;
    private final TransitionLogService transitionLogService;

    public RegistrationCheckInterceptor(GdprConsentService gdprConsentService, TransitionLogService transitionLogService) {
        this.gdprConsentService = gdprConsentService;
        this.transitionLogService = transitionLogService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        Long userId = getUserId(request);
        if(userId == null) {
            return false;
        }

        Optional<Instant> lastGdprConsentUpdateTime = gdprConsentService.getLastGdprConsentUpdateTime();
        if(lastGdprConsentUpdateTime.isPresent()) {
            Optional<TransitionLog> latestTransitionOpt = transitionLogService.findLatestTransition(userId);
            if(latestTransitionOpt.isEmpty()) {
                registrationCheckNotPassed(String.format("User with id %d hasn't accepted accept GDPR consent.", userId));
            }

            TransitionLog latestTransition = latestTransitionOpt.get();
            if(!StateTransitionConfig.isEndState(latestTransition.getToState())) {
                registrationCheckNotPassed(String.format("User with id %d hasn't accepted accept GDPR consent.", userId));
            }

            if(latestTransition.getCreationTime().isBefore(lastGdprConsentUpdateTime.get())) {
                registrationCheckNotPassed(String.format("User with id %d hasn't accepted accept the last GDPR consent.", userId));
            }
        }

        return true;
    }

    private static Long getUserId(HttpServletRequest request) {
        Map<String, String> pathVariables = (Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String userIdStr = pathVariables.get("userId");
        if(userIdStr != null) {
            return Long.valueOf(userIdStr);
        }
        return null;
    }

    private void registrationCheckNotPassed(String message) {
        throw new RegistrationCheckNotPassedException(message);
    }

}
