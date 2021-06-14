package com.statemachine.statemachine.domain;

import com.statemachine.statemachine.config.components.TransitionState;
import com.statemachine.statemachine.config.components.TransitionEvent;
import com.statemachine.statemachine.domain.converter.MapJsonConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "milestone")
public class TransitionLog {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private TransitionState fromState;

    private TransitionState toState;

    private TransitionEvent transitionEvent;

    @Convert(converter = MapJsonConverter.class)
    private Map<String, Object> details;

}
