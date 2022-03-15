package ru.nucodelabs.gem.core.factory;

import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.model.SectionImpl;

import java.util.EmptyStackException;
import java.util.Stack;

public class SectionFactory {

    private final Stack<Section> stack = new Stack<>();

    public Section create() {
        Section section = new SectionImpl();
        stack.push(section);
        return section;
    }

    public Section getLastCreated() throws EmptyStackException {
        return stack.peek();
    }
}
