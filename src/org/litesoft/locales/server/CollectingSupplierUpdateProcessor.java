package org.litesoft.locales.server;

import org.litesoft.commonfoundation.console.*;
import org.litesoft.commonfoundation.exceptions.*;
import org.litesoft.commonfoundation.indent.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.shared.tables.*;

import java.util.*;

public abstract class CollectingSupplierUpdateProcessor implements SupplierUpdateProcessor {
    protected final LocalizationSupplier mSupplier;
    protected final Console mConsole;
    protected final ConsoleIndentableWriter mWriter;
    protected final LocaleFileUtils.Paths mLocaleFiles;

    public CollectingSupplierUpdateProcessor( LocalizationSupplier pSupplier, Console pConsole, LocaleFileUtils.Paths pLocaleFiles ) {
        mSupplier = pSupplier;
        mWriter = new ConsoleIndentableWriter( "    ", mConsole = pConsole );
        mLocaleFiles = pLocaleFiles.with( new OurLocaleExceptionHandler() );
    }

    private class OurLocaleExceptionHandler implements LocaleExceptionHandler {
        private final List<String> mMalformed = Lists.newArrayList();
        private final List<String> mDupEntries = Lists.newArrayList();
        private final List<String> mErrored = Lists.newArrayList();

        @Override
        public boolean handled( int pOffset, String pLine, RuntimeException pRTE ) {
            String zReport = "Line[" + pOffset + "]: " + pLine;
            return
                    add( zReport, pRTE instanceof MalformedException, mMalformed, null ) ||
                    add( zReport, pRTE instanceof DupEntryException, mDupEntries, null ) ||
                    add( zReport, true, mErrored, pRTE );
        }

        private boolean add( String pReport, boolean pAdd, List<String> pCollector, RuntimeException pRTE ) {
            if ( pAdd ) {
                if ( pRTE != null ) {
                    pReport += " | " + pRTE.getMessage();
                }
                pCollector.add( pReport );
                return true;
            }
            return false;
        }

        @Override
        public void completedProcessing( String pPath ) {
            mWriter.indent();
            dump( mMalformed, "Malformed" );
            dump( mDupEntries, "Dup Entries" );
            dump( mErrored, "Other Errors" );
            mWriter.outdent();
            mWriter.close();
        }

        private void dump( List<String> pCollection, String pLabel ) {
            if ( !pCollection.isEmpty() ) {
                mWriter.printLn( pLabel + ":" );
                mWriter.indent();
                for ( String zLine : pCollection ) {
                    mWriter.printLn( zLine );
                }
                mWriter.outdent();
            }
        }
    }
}
