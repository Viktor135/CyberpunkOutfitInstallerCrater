package de.vsc.coi.builder;

import fomod.ModuleConfiguration;

public abstract class SubBuilder<T,O> {

    private final T parent;

    protected SubBuilder(final T parent) {
        this.parent = parent;
    }

    public T parent() {
        return parent;
    }

    protected abstract O getEntity();

    public O build() {
        return getEntity();
    }
}
