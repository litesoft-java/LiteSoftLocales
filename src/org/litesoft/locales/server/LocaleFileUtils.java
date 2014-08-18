package org.litesoft.locales.server;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.console.Console;
import org.litesoft.locales.shared.*;
import org.litesoft.server.file.*;

import java.io.*;
import java.util.*;

public class LocaleFileUtils {
    private final Console mConsole;
    private final KeyValueStringsLineParser mKeyValueStringsLineParser;

    public LocaleFileUtils( Console pConsole, KeyValueStringsLineParser pKeyValueStringsLineParser ) {
        mConsole = pConsole;
        mKeyValueStringsLineParser = pKeyValueStringsLineParser;
    }

    public Paths from( LocalePaths.To pPathsTo ) {
        return new Paths( pPathsTo );
    }

    public class Paths {
        private final LocalePaths.To mPathsTo;
        private final LocaleExceptionHandler mExceptionHandler;

        private Paths( LocalePaths.To pPathsTo, LocaleExceptionHandler pExceptionHandler ) {
            mPathsTo = pPathsTo;
            mExceptionHandler = pExceptionHandler;
        }

        private Paths( LocalePaths.To pPathsTo ) {
            this( pPathsTo, null );
        }

        public Paths with( LocaleExceptionHandler pExceptionHandler ) {
            return new Paths( mPathsTo, pExceptionHandler );
        }

        public String[] loadAndValidate_en_US( Map<String, KeyValueStrings> pCollector ) {
            return loadAndValidateLocale( pCollector, Locale_en_US.INSTANCE );
        }

        public String[] loadAndValidateLocale( Map<String, KeyValueStrings> pCollector, AbstractLocale pLocale ) {
            return loadAndValidateKeyValueStrings( pCollector, mPathsTo.locale( pLocale ) );
        }

        private String[] loadAndValidateKeyValueStrings( Map<String, KeyValueStrings> pCollector, String pPath ) {
            mConsole.println( "Loading File: " + pPath );
            File zFile = new File( pPath );
            String[] zLines = FileUtils.loadTextFile( zFile );
            for ( int i = 0; i < zLines.length; i++ ) {
                KeyValueStrings zKeyValue = parse( zLines, i );
                if ( zKeyValue != null ) {
                    pCollector.put( zKeyValue.getKey(), zKeyValue );
                }
            }
            if ( mExceptionHandler != null ) {
                mExceptionHandler.completedProcessing( pPath );
            }
            return zLines;
        }

        private KeyValueStrings parse( String[] pLines, int pOffsetSet ) {
            String zLine = pLines[pOffsetSet];
            try {
                return mKeyValueStringsLineParser.parse( zLine );
            }
            catch ( RuntimeException rte ) {
                if ( (mExceptionHandler == null) || !mExceptionHandler.handled( pOffsetSet, zLine, rte ) ) {
                    throw rte;
                }
                return null;
            }
        }
    }
}
