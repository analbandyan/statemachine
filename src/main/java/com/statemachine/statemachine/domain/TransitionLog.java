package com.statemachine.statemachine.domain;

import com.statemachine.statemachine.config.statemachine.components.TransitionEvent;
import com.statemachine.statemachine.config.statemachine.components.TransitionState;
import com.statemachine.statemachine.domain.converter.MapJsonConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TransitionLog {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private TransitionState fromState;

    private TransitionState toState;

    private TransitionEvent transitionEvent;

    @CreationTimestamp
    private Instant creationTime;

    @Convert(converter = MapJsonConverter.class)
    private Map<String, Object> details;

}
