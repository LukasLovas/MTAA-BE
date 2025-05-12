package com.example.mtaa.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionChangedEvent extends ApplicationEvent {
    private final Long userId;

    public TransactionChangedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}