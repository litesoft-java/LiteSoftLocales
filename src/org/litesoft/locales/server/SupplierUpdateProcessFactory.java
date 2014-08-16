package org.litesoft.locales.server;

import org.litesoft.commonfoundation.console.*;
import org.litesoft.locales.shared.support.*;
import org.litesoft.locales.shared.tables.*;

import java.util.*;

public interface SupplierUpdateProcessFactory {
    SupplierUpdateProcessor create( Console pConsole, Map<String, SimpleJSLocaleEntryStatement> pEntries_en_US,
                                    LocalizationSupplier pSupplier, LocalePaths.To pPathsTo );
}
