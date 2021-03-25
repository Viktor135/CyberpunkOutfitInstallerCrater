package de.vsc.coi.gui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class GuiWrapper {

    private static final Logger LOGGER = LogManager.getLogger(GuiWrapper.class);
    private static final GuiWrapper INSTANCE = new GuiWrapper();
    private Gui gui;

    private GuiWrapper() {
    }

    public static void createGui() {
        INSTANCE.gui = new Gui();
    }

    public static GuiWrapper getInstance(){
        return INSTANCE;
    }

    public Result<Void> invoke(final ErrorableConsumer<Gui> consumer) {
        return call(gui -> {
            consumer.apply(gui);
            return null;
        });
    }

    public <T> Result<T> call(final ErrorableFunction<Gui, T> function) {
        if (gui == null) {
            throw new IllegalStateException("The gui wrapper must be initialised before usage.");
        }
        final ErrorableFunctionWrapper<Gui, T> wrapper = ErrorableFunctionWrapper.of(function);
        try {
            SwingUtilities.invokeAndWait(() -> wrapper.apply(gui));
        } catch (final InterruptedException | InvocationTargetException e) {
            return new Result<>(null, e);
        }
        return wrapper.getResult();
    }

    public void setGuiStatus(final String message) {
        final Result<Void> result = invoke(gui -> gui.setStatus(message));
        if (result.hadError()) {
            LOGGER.error("On gui set status!", result.exception);
        }
    }

    public interface ErrorableFunction<I, O> {

        O apply(final I input) throws Exception;
    }

    public interface ErrorableConsumer<I> {

        void apply(final I input) throws Exception;
    }

    private static abstract class ErrorableFunctionWrapper<I, O> {

        private Exception exception;
        private O result;

        private static <I, O> ErrorableFunctionWrapper<I, O> of(final ErrorableFunction<I, O> function) {
            return new ErrorableFunctionWrapper<>() {
                @Override
                protected O applyInt(final I input) throws Exception {
                    return function.apply(input);
                }
            };
        }

        void apply(final I input) {
            try {
                this.result = this.applyInt(input);
            } catch (final Exception e) {
                exception = e;
            }
        }

        public Result<O> getResult() {
            return new Result<>(result, exception);
        }

        protected abstract O applyInt(final I input) throws Exception;
    }

    @AllArgsConstructor
    @Getter
    public static class Result<T> {

        private final T value;
        private final Exception exception;

        public boolean wasSuccessful() {
            return exception == null;
        }

        public boolean hadError() {
            return !wasSuccessful();
        }

        public T getValue() {
            if (hadError()) {
                throw new RuntimeException(exception);
            }
            return value;
        }
    }

}
