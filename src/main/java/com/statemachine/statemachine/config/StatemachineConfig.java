package com.statemachine.statemachine.config;

import com.statemachine.statemachine.config.components.EnrollmentEvent;
import com.statemachine.statemachine.config.components.EnrollmentState;
import com.statemachine.statemachine.config.components.StateTransition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaRepositoryState;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableStateMachineFactory
@EntityScan("com")//to override/reorder StateMachineJpaRepositoriesAutoConfiguration's @EntityScan
public class StatemachineConfig extends EnumStateMachineConfigurerAdapter<EnrollmentState, EnrollmentEvent> {

    @Autowired
    private StateMachineRuntimePersister<EnrollmentState, EnrollmentEvent, String> stateMachineRuntimePersister;

    @Override
    public void configure(StateMachineConfigurationConfigurer<EnrollmentState, EnrollmentEvent> config) throws Exception {
        var statemachineListenerAdapter = new StateMachineListenerAdapter<EnrollmentState, EnrollmentEvent>() {
            @Override
            public void stateChanged(State<EnrollmentState, EnrollmentEvent> from, State<EnrollmentState, EnrollmentEvent> to) {
                log.info(String.format("state changed from %s to %s", from, to));
            }
        };

        config.withConfiguration()
                .autoStartup(false)
                .listener(statemachineListenerAdapter)
                .and()
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister);
    }

    @Override
    public void configure(StateMachineStateConfigurer<EnrollmentState, EnrollmentEvent> states) throws Exception {
        StateConfigurer<EnrollmentState, EnrollmentEvent> stateConfigurer = states.withStates();

        stateConfigurer.initial(StateTransition.getRootState());

        StateTransition.getIntermediateStates().forEach(stateConfigurer::state);

        StateTransition.getEndStates().forEach(stateConfigurer::end);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EnrollmentState, EnrollmentEvent> transitions) throws Exception {
        Arrays.stream(StateTransition.values())
                .forEach(stateTransition -> configureStateTrnsition(transitions, stateTransition))
        ;
    }

    private static void configureStateTrnsition(StateMachineTransitionConfigurer<EnrollmentState, EnrollmentEvent> transitions, StateTransition stateTransition) {
        externalTransitionConfigurer(transitions).source(stateTransition.getInitialState())
                .target(stateTransition.getTargetState())
                .event(stateTransition.getTransitionEvent())
                .guard(context -> stateTransition.getGuardPredicate().test(context));
    }

    private static ExternalTransitionConfigurer<EnrollmentState, EnrollmentEvent> externalTransitionConfigurer(StateMachineTransitionConfigurer<EnrollmentState, EnrollmentEvent> transitions) {
        try {
            return transitions.withExternal();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create external transition config: " + e.getMessage(), e);
        }
    }


    //--------------------

//    @Bean
//    public StateMachineRuntimePersister<EnrollmentState, EnrollmentEvent, String> stateMachineRuntimePersister(
//            JpaStateMachineRepository jpaStateMachineRepository) {
//        return new JpaPersistingStateMachineInterceptor<EnrollmentState, EnrollmentEvent, String>(jpaStateMachineRepository);
//    }

    @Bean
    public StateMachineService<EnrollmentState, EnrollmentEvent> stateMachineService(
            StateMachineFactory<EnrollmentState, EnrollmentEvent> stateMachineFactory,
            StateMachineRuntimePersister<EnrollmentState, EnrollmentEvent, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }

}
