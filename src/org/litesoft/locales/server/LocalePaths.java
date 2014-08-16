package org.litesoft.locales.server;

import org.litesoft.locales.shared.*;

public interface LocalePaths {
    interface To {
        String locale( AbstractLocale pLocale );
    }

    String toDataBases();

    To master();

    To updated( String pSupplier );

    String toUpdaterFactoryFor( String pSupplier );
}
