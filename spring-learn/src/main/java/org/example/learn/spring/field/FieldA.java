package org.example.learn.spring.field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FieldA {
    @Autowired
    public FieldB b;
}
