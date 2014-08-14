package org.litesoft.locales.server.tables;

import org.litesoft.json.server.*;
import org.litesoft.locales.shared.tables.*;
import org.litesoft.server.util.*;

public class LocalizationSuppliersPersister extends PersisterBackedGsonPersister<LocalizationSuppliers> {
    public static final String SUPPLIERS = "Suppliers";

    public LocalizationSuppliersPersister( Persister pPersister ) {
        super( LocalizationSuppliers.class, pPersister, "", SUPPLIERS );
    }
}
