package org.litesoft.locales.server;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.console.*;
import org.litesoft.locales.shared.*;
import org.litesoft.server.file.*;

import java.util.*;

public abstract class LocaleProcessor {

    protected final Console mConsole;
    protected final LocaleFileUtils mLocaleFileUtils;
    protected final LocalePaths mLocalePaths;
    protected final FilePersister mDB_Persister;

    public LocaleProcessor( Console pConsole, KeyValueStringsLineParser pKeyValueStringsLineParser, LocalePaths pLocalePaths ) {
        mLocaleFileUtils = new LocaleFileUtils( mConsole = pConsole, pKeyValueStringsLineParser );
        mDB_Persister = new FilePersister( (mLocalePaths = pLocalePaths).toDataBases() );
    }

    abstract public void process();

    public abstract static class Factory implements Runnable {
        private final String[] mArgs;

        public Factory( String[] pArgs, Set<AbstractLocale> pSupportedLocales ) {
            mArgs = pArgs;
            DerivedLocaleGraph.select( pSupportedLocales ); // Register Instances
        }

        @Override
        public void run() {
            Console zConsole = createConsole( mArgs );
            try {
                create( zConsole ).process();
            }
            finally {
                zConsole.close();
            }
        }

        abstract protected LocaleProcessor create( Console pConsole );

        protected Console createConsole( String[] pArgs ) {
            return ((pArgs == null) || (pArgs.length == 0)) ? new ConsoleSOUT() : new ConsoleTextFile( ConstrainTo.significantOrNull( pArgs[0] ) );
        }
    }
}
