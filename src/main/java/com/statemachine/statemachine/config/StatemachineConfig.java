package com.statemachine.statemachine.config;

import com.statemachine.statemachine.config.components.TransitionEvent;
import com.statemachine.statemachine.config.components.TransitionState;
import com.statemachine.statemachine.config.components.StateTransitionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
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
public class StatemachineConfig extends EnumStateMachineConfigurerAdapter<TransitionState, TransitionEvent> {

    @Autowired
    private StateMachineRuntimePersister<TransitionState, TransitionEvent, String> stateMachineRuntimePersister;

    @Override
    public void configure(StateMachineConfigurationConfigurer<TransitionState, TransitionEvent> config) throws Exception {
        var statemachineChangeLogger = new StateMachineListenerAdapter<TransitionState, TransitionEvent>() {
            @Override
            public void stateChanged(State<TransitionState, TransitionEvent> from, State<TransitionState, TransitionEvent> to) {
                log.info(String.format("state changed from %s to %s", from, to));
            }
        };
        
        config.withConfiguration()
                .autoStartup(false)
                .listener(statemachineChangeLogger)
                .and()
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister);
    }

    @Override
    public void configure(StateMachineStateConfigurer<TransitionState, TransitionEvent> states) throws Exception {
        StateConfigurer<TransitionState, TransitionEvent> stateConfigurer = states.withStates();

        stateConfigurer.initial(StateTransitionConfig.getRootState());

        StateTransitionConfig.getIntermediateStates().forEach(stateConfigurer::state);

        StateTransitionConfig.getEndStates().forEach(stateConfigurer::end);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TransitionState, TransitionEvent> transitions) throws Exception {
        Arrays.stream(StateTransitionConfig.values())
                .forEach(stateTransitionConfig -> configureStateTrnsition(transitions, stateTransitionConfig))
        ;
    }

    private static void configureStateTrnsition(StateMachineTransitionConfigurer<TransitionState, TransitionEvent> transitions, StateTransitionConfig stateTransitionConfig) {
        externalTransitionConfigurer(transitions).source(stateTransitionConfig.getInitialState())
                .target(stateTransitionConfig.getTargetState())
                .event(stateTransitionConfig.getTransitionEvent())
                .guard(context -> stateTransitionConfig.getGuardPredicate().test(context));
    }

    private static ExternalTransitionConfigurer<TransitionState, TransitionEvent> externalTransitionConfigurer(StateMachineTransitionConfigurer<TransitionState, TransitionEvent> transitions) {
        try {
            return transitions.withExternal();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create external transition config: " + e.getMessage(), e);
        }
    }


    //--------------------

//    @Bean
//    public StateMachineRuntimePersister<TransitionState, TransitionEvent, String> stateMachineRuntimePersister(
//            JpaStateMachineRepository jpaStateMachineRepository) {
//        return new JpaPersistingStateMachineInterceptor<TransitionState, TransitionEvent, String>(jpaStateMachineRepository);
//    }

    @Bean
    public StateMachineService<TransitionState, TransitionEvent> stateMachineService(
            StateMachineFactory<TransitionState, TransitionEvent> stateMachineFactory,
            StateMachineRuntimePersister<TransitionState, TransitionEvent, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }

}
