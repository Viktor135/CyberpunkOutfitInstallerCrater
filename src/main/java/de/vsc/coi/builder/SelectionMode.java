package de.vsc.coi.builder;

import static de.vsc.coi.utils.Utils.toUniqueOptional;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

import java.util.Arrays;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SelectionMode {
    SelectAtLeastOne("SelectAtLeastOne"),
    SelectAtMostOne("SelectAtMostOne"),
    SelectExactlyOne("SelectExactlyOne"),
    SelectAll("SelectAll"),
    SelectAny("SelectAny");

    public static final SelectionMode DEFAULT = SelectAtMostOne;
    private final String value;

    public static Optional<SelectionMode> fromName(final String name) {
        return Arrays.stream(values())
                .filter(x -> equalsAnyIgnoreCase(x.value, name))
                .collect(toUniqueOptional("The " + "SelectionModes must have unique values."));
    }
}
