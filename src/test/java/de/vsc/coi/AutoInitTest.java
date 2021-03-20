package de.vsc.coi;

import static de.vsc.coi.AutoInit.newAutoInit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.jupiter.api.Test;

class AutoInitTest {

    AutoInit<String> autoInit = newAutoInit(() -> "ABC");

    @Test
    void should_not_init_at_startup() throws NoSuchFieldException, IllegalAccessException {
        final String contend = (String) AutoInit.class.getDeclaredField("content").get(autoInit);
        assertThat(contend, nullValue());
    }

    @Test
    void should_init_at_get() {
        final String contend = autoInit.get();
        assertThat(contend, is("ABC"));
    }

}