package org.litesoft.locales.shared;

import org.litesoft.commonfoundation.typeutils.*;

import java.util.*;

public final class Locales {
    private static Locales sInstance;

    public static synchronized Locales getInstance() {
        if ( sInstance == null ) {
            throw new IllegalStateException( "Locales referenced before being initialized!" );
        }
        return sInstance;
    }

    private static synchronized void setInstance( Locales pInstance ) {
        if ( sInstance != null ) {
            throw new IllegalStateException( "Duplicate Locales created!" );
        }
        sInstance = pInstance;
    }

    private final Set<AbstractLocale> mSupported;
    private final List<String> mActiveCodes = Lists.newArrayList();

    public Locales( AbstractLocale... pSupportedLocales ) {
        mSupported = Collections.unmodifiableSet( toSet( pSupportedLocales ) );
        for ( AbstractLocale zLocale : pSupportedLocales ) {
            if ( zLocale.isActive() ) {
                mActiveCodes.add( zLocale.getCode() );
            }
        }
        setInstance( this ); // 'this' leakage, but is final class!
    }

    public static Set<AbstractLocale> getSupported() {
        return getInstance().mSupported;
    }

    public static String[] getActiveCodes() {
        List<String> zActiveCodes = getInstance().mActiveCodes;
        return zActiveCodes.toArray( new String[zActiveCodes.size()] );
    }

    public static Set<AbstractLocale> toSet( AbstractLocale... pSupportedLocales ) {
        Set<AbstractLocale> zLocales = Sets.newHashSet();
        Collections.addAll( zLocales, pSupportedLocales );
        return zLocales;
    }
}
