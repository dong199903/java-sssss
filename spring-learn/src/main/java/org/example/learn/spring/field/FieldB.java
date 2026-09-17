package org.example.learn.spring.field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FieldB {
    @Autowired
    public FieldA a;
}
