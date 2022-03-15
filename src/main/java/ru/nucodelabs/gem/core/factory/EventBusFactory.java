package ru.nucodelabs.gem.core.factory;

import com.google.common.eventbus.EventBus;

import java.util.EmptyStackException;
import java.util.Stack;

public class EventBusFactory {

    private final Stack<EventBus> stack = new Stack<>();

    public EventBus create() {
        EventBus eventBus = new EventBus();
        stack.push(eventBus);
        return eventBus;
    }

    public EventBus getLastCreated() throws EmptyStackException {
        return stack.peek();
    }

}
