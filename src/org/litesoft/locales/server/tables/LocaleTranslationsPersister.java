package org.litesoft.locales.server.tables;

import org.litesoft.json.server.*;
import org.litesoft.locales.shared.*;
import org.litesoft.locales.shared.tables.*;
import org.litesoft.server.util.*;

public class LocaleTranslationsPersister extends PersisterBackedGsonPersister<LocaleTranslations> {
    public LocaleTranslationsPersister( Persister pPersister, AbstractLocale pLocale ) {
        super( LocaleTranslations.class, pPersister, "", pLocale.getCode() );
    }
}
