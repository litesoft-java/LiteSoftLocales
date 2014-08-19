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
        mWriter = new ConsoleIndentableWriter( mConsole = pConsole );
        mLocaleFiles = pLocaleFiles.with( new OurLocaleExceptionHandler() );
    }

    protected void dumpAdditionalIssues() {
    }

    protected void dump( ListIndentableWriter pCollection, String pLabel ) {
        pCollection.close();
        List<String> zLines = pCollection.getLines();
        if ( !zLines.isEmpty() ) {
            mWriter.printLn( pLabel + ":" );
            mWriter.indent();
            for ( String zLine : zLines ) {
                mWriter.printLn( zLine );
            }
            mWriter.outdent();
        }
    }

    private class OurLocaleExceptionHandler implements LocaleExceptionHandler {
        private final ListIndentableWriter mMalformed = new ListIndentableWriter();
        private final ListIndentableWriter mDupEntries = new ListIndentableWriter();
        private final ListIndentableWriter mErrored = new ListIndentableWriter();

        @Override
        public boolean handled( int pOffset, String pLine, RuntimeException pRTE ) {
            String zReport = "Line[" + pOffset + "]: " + pLine;
            return
                    add( zReport, pRTE instanceof MalformedException, mMalformed, null ) ||
                    add( zReport, pRTE instanceof DupEntryException, mDupEntries, null ) ||
                    add( zReport, true, mErrored, pRTE );
        }

        private boolean add( String pReport, boolean pAdd, ListIndentableWriter pCollector, RuntimeException pRTE ) {
            if ( pAdd ) {
                if ( pRTE != null ) {
                    pReport += " | " + pRTE.getMessage();
                }
                pCollector.printLn( pReport );
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
            dumpAdditionalIssues();
            mWriter.outdent();
            mWriter.close();
        }
    }
}
