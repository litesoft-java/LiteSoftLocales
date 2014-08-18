package org.litesoft.locales.shared.support;

public interface SimpleJSLocaleEntryConstants {
    static final String LINE_PREFIX = "LNG['";
    static final String KEY_TERMINATOR = "']";

    static final String KEY_VALUE_SEP = "=";

    static final String VALUE_PREFIX = "\"";
    static final String VALUE_SUFFIX = "\";";

    static final String[] SEPARATORS = {
            // 0 = White Space!
            LINE_PREFIX,
            // 1 = Key
            KEY_TERMINATOR,
            // 2 = White Space!
            KEY_VALUE_SEP,
            // 3 = White Space!
            VALUE_PREFIX,
            // 4 = Value
            VALUE_SUFFIX,
            // 5 = White Space!
    };

    static final int[] WHITE_SPACES_AT = {0, 2, 3, 5};
    static final int KEY_AT = 1;
    static final int VALUE_AT = 4;
}
