package org.litesoft.locales.server;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.console.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.server.tables.*;
import org.litesoft.locales.shared.*;
import org.litesoft.locales.shared.tables.*;
import org.litesoft.server.dynamicload.*;
import org.litesoft.server.file.*;

import java.util.*;

public class LocaleUpdater {

    protected final Console mConsole;
    protected final LocaleFileUtils mLocaleFileUtils;
    protected final LocalePaths mLocalePaths;
    protected FilePersister mDB_Persister;

    public LocaleUpdater( Console pConsole, KeyValueStringsLineParser pKeyValueStringsLineParser, LocalePaths pLocalePaths ) {
        mLocaleFileUtils = new LocaleFileUtils( mConsole = pConsole, pKeyValueStringsLineParser );
        mDB_Persister = new FilePersister( (mLocalePaths = pLocalePaths).toDataBases() );
    }

    public void process() {
        LocalizationSuppliersPersister zSuppliersPersister = new LocalizationSuppliersPersister( mDB_Persister );

        // new SuppliersTest( zSuppliersPersister ).process();

        process( zSuppliersPersister.get() );
    }

    protected void process( LocalizationSuppliers pSuppliers ) {
        new Updater().process( pSuppliers.getSuppliers() );
    }

    private class Updater {
        private final Map<String, KeyValueStrings> mEntries_en_US = Maps.newLinkedHashMap(); // Preserve Order

        public Updater() {
            mLocaleFileUtils.from( mLocalePaths.master() ).loadAndValidate_en_US( mEntries_en_US );
        }

        public void process( LocalizationSupplier... pSuppliers ) {
            for ( LocalizationSupplier zSupplier : pSuppliers ) {
                update( zSupplier );
            }
        }

        private void update( LocalizationSupplier pSupplier ) {
            String zClassName = mLocalePaths.toUpdaterFactoryFor( pSupplier.getName() );
            if ( zClassName == null ) {
                return;
            }
            SupplierUpdateProcessFactory zFactory = ClassForName.newInstance( SupplierUpdateProcessFactory.class, zClassName );
            SupplierUpdateProcessor zUpdateProcessor = zFactory.create( mConsole, mEntries_en_US, pSupplier, mLocalePaths.updated( pSupplier.getName() ) );

            for ( AbstractLocale zLocale : Locales.getSupported() ) {
                if ( Locale_en_US.INSTANCE != zLocale ) {
                    update( zLocale, zUpdateProcessor );
                }
            }
        }

        private void update( AbstractLocale pLocale, SupplierUpdateProcessor pUpdateProcessor ) {
            LocaleTranslationsPersister zPersister = new LocaleTranslationsPersister( mDB_Persister, pLocale );
            LocaleTranslations zTranslations = zPersister.get();
            pUpdateProcessor.update( zTranslations );
            zPersister.save( zTranslations );
        }
    }

    //    private class SuppliersTest {
    //        private final LocalizationSuppliersPersister mSuppliersPersister;
    //
    //        public SuppliersTest( LocalizationSuppliersPersister pSuppliersPersister ) {
    //            mSuppliersPersister = pSuppliersPersister;
    //        }
    //
    //        public void process() {
    //            LocalizationSuppliers zSuppliers = new LocalizationSuppliers();
    //
    //            zSuppliers.add( new LocalizationSupplier( "LocalizationTeam", 90 ) );
    //
    //            zSuppliers = saveNget( zSuppliers );
    //
    //            zSuppliers.add( new LocalizationSupplier( "GoogleSearch", 10 ) );
    //
    //            zSuppliers = saveNget( zSuppliers );
    //
    //            zSuppliers.remove( zSuppliers.get( "GoogleSearch" ) );
    //
    //            zSuppliers = saveNget( zSuppliers );
    //
    //            zSuppliers.get( "LocalizationTeam" ).setConfidencePercentage( 100 );
    //
    //            saveNget( zSuppliers );
    //        }
    //
    //        private LocalizationSuppliers saveNget( LocalizationSuppliers pSuppliers ) {
    //            mConsole.println( "" + pSuppliers );
    //            mSuppliersPersister.save( pSuppliers );
    //            mConsole.println( "---- Save | Get ---- " + LocalizationSuppliers.calcConfidencePercentage( pSuppliers.getSuppliers() ) );
    //            pSuppliers = mSuppliersPersister.get();
    //            mConsole.println( "" + pSuppliers );
    //            return pSuppliers;
    //        }
    //    }
}
