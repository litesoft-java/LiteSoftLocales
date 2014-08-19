package org.litesoft.locales.server;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.console.Console;
import org.litesoft.commonfoundation.indent.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.server.tables.*;
import org.litesoft.locales.shared.*;
import org.litesoft.locales.shared.tables.*;
import org.litesoft.locales.shared.tables.support.*;
import org.litesoft.server.file.*;

import java.io.*;
import java.util.*;

public class LocaleGenerator extends LocaleProcessor {

    private final KeyValueStringsLineParser mKeyValueStringsLineParser;
    private final String[] mLines_en_US;

    public LocaleGenerator( Console pConsole, KeyValueStringsLineParser pKeyValueStringsLineParser, LocalePaths pLocalePaths ) {
        super( pConsole, pKeyValueStringsLineParser, pLocalePaths );
        mKeyValueStringsLineParser = pKeyValueStringsLineParser;
        Map<String, KeyValueStrings> zCollector = Maps.newHashMap();
        mLines_en_US = mLocaleFileUtils.from( mLocalePaths.master() ).loadAndValidate_en_US( zCollector );
    }

    public void process() {
        for ( AbstractLocale zLocale : Locales.getSupported() ) {
            if ( Locale_en_US.INSTANCE != zLocale ) {
                new Generator( new LocaleTranslationsPersister( mDB_Persister, zLocale ).get() ).generate( zLocale );
            }
        }
    }

    private class Generator {
        private final LocaleTranslations mTranslations;
        private final String mOutputPath;
        private final LocaleIssues mIssueCollector = new LocaleIssues( new ConsoleIndentableWriter( mConsole ) );

        public Generator( LocaleTranslations pTranslations ) {
            mOutputPath = mLocalePaths.master().locale( (mTranslations = pTranslations).getLocale() );
        }

        public void generate( AbstractLocale pLocale ) {
            mConsole.println( "Generating (" + pLocale + "): " + mOutputPath );
            String[] zLines = transformLines( mLines_en_US );
            FileUtils.storeTextFile( new File( mOutputPath ), zLines );
            mIssueCollector.report();
        }

        private String[] transformLines( String[] pLines_en_US ) {
            String[] zLocaleLines = new String[pLines_en_US.length];
            for ( int i = 0; i < pLines_en_US.length; i++ ) {
                zLocaleLines[i] = transformLine( pLines_en_US[i] );
            }
            return zLocaleLines;
        }

        private String transformLine( String pLine_en_US ) {
            KeyValueStrings zEnglishEntry = mKeyValueStringsLineParser.parse( pLine_en_US );
            if ( zEnglishEntry == null ) {
                return pLine_en_US; // Comment or something!
            }
            String zCurValue = zEnglishEntry.getValue();
            String zNewValue = mTranslations.getTranslationFor( zCurValue, mIssueCollector );
            return mKeyValueStringsLineParser.create( zEnglishEntry.getKey(), zNewValue ).toString();
        }
    }
}
