package de.vsc.coi;

import static de.vsc.coi.utils.FileReaderUtils.readLinesOfResource;

import java.util.List;
import java.util.function.Supplier;

/**
 * A class which content is initialised at the first usage, via {@link AutoInit#init()}
 *
 * @param <T>
 *         the type of the content
 */
public abstract class AutoInit<T> {

    protected T content = null;

    private AutoInit() {
    }

    /**
     * Simplified generation of a now class.
     *
     * @param supplier
     *         the init method
     * @param <T>
     *         the type of the content
     *
     * @return the new instance
     */
    public static <T> AutoInit<T> newAutoInit(final Supplier<T> supplier) {
        return new AutoInit<>() {
            @Override
            protected void init() {
                this.content = supplier.get();
            }
        };
    }

    /**
     * Reads the resource, given by its name if it is needed.
     *
     * @param resourceName
     *         the name of the resource
     *
     * @return the new instance.
     */
    public static AutoInit<List<String>> newResourceReader(final String resourceName) {
        return newAutoInit(() -> readLinesOfResource(resourceName));
    }

    /**
     * Initialises the contend of this class.
     */
    protected abstract void init();

    /**
     * Returns the contend of this class,
     * initialises it, if this was not done until now.
     *
     * @return the contend
     */
    public T get() {
        if (content == null) {
            init();
        }
        return content;
    }
}
