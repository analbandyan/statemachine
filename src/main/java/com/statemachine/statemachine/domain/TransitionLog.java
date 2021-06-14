package com.statemachine.statemachine.domain;

import com.statemachine.statemachine.config.components.EnrollmentState;
import com.statemachine.statemachine.config.components.EnrollmentEvent;
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

    private EnrollmentState fromState;

    private EnrollmentState toState;

    private EnrollmentEvent transitionEvent;

    @Convert(converter = MapJsonConverter.class)
    private Map<String, Object> details;

}
