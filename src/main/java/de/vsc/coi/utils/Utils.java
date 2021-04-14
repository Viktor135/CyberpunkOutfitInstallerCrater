package de.vsc.coi.utils;

import static de.vsc.coi.config.ProjectConfig.project;
import static java.net.http.HttpClient.Redirect;
import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.vsc.coi.config.Workspace;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class Utils {

    /**
     * Returns the object returned by the getter if not null,
     * otherwise creates a new object, sets it in to the object via the setter and returns the new object.
     *
     * <p>
     * E.g.:
     * <pre>
     * {@code
     * class Item{
     *     String value = null;
     * }
     *
     * String value = getOrNew(Item::getValue,Item::setValue,()->"");
     * }
     * </pre>
     *
     * @param getter
     *         how the object is retrieved
     * @param setter
     *         how the object is set
     * @param newObj
     *         how to create a new object
     * @param <O>
     *         the type
     *
     * @return the retrieved object
     */
    public static <O> O getOrNew(final Supplier<O> getter, final Consumer<O> setter, final Supplier<O> newObj) {
        O obj = getter.get();
        if (obj == null) {
            obj = newObj.get();
            setter.accept(obj);
        }
        return obj;
    }

    public static <T> Collector<T, ?, T> toSingleton() {
        return toSingleton("There has to be exactly one item in the stream!");
    }

    public static <T> Collector<T, ?, T> toSingleton(final String errorMessage) {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            if (list.size() != 1) {
                throw new IllegalStateException(errorMessage);
            }
            return list.get(0);
        });
    }

    /**
     * Collects the {@link Stream} in to an {@link Optional}.
     * In contrast to {@link Stream#findFirst()} it throws an hadError if more than one element is present.
     *
     * @param what
     *         which type of item is collected?
     * @param where
     *         where does the collection take place
     * @param <T>
     *         the type
     *
     * @return an optional
     *
     * @throws IllegalStateException
     *         if more than one element is present
     */
    public static <T> Collector<T, ?, Optional<T>> toUniqueOptional(final String what, final File where) {
        return toUniqueOptional("There should be at most one " + what + ", in: " + Workspace.relativize(where));
    }

    /**
     * Collects the {@link Stream} in to an {@link Optional}.
     * In contrast to {@link Stream#findFirst()} it throws an hadError if more than one element is present.
     *
     * @param errorMessage
     *         a message to use if more than one elements are present
     * @param <T>
     *         the type
     *
     * @return an optional
     *
     * @throws IllegalStateException
     *         if more than one element is present
     */
    public static <T> Collector<T, ?, Optional<T>> toUniqueOptional(final String errorMessage) {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            if (list.size() > 1) {
                throw new IllegalStateException(errorMessage);
            }
            if (list.size() == 1) {
                return Optional.of(list.get(0));
            }
            return Optional.empty();
        });
    }

}
