package de.vsc.coi;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ArgumentCaptor<T> extends BaseMatcher<T> {

    private final List<T> captures;
    private final Class<T> forClass;

    public ArgumentCaptor(final Class<T> forClass) {
        super();
        this.forClass = forClass;
        this.captures = new ArrayList<>();
    }

    public List<T> getCaptures() {
        return captures;
    }

    public T getLast(){
        if(this.captures.isEmpty()){
            return null;
        }
        return this.captures.get(this.captures.size()-1);
    }
    @Override
    public void describeTo(final Description description) {

    }

    @Override
    public boolean matches(final Object actual) {
        if (!forClass.isInstance(actual)) {
            return false;
        }
        captures.add((T) actual);
        return true;
    }
}
