package org.litesoft.locales.shared;

import org.litesoft.commonfoundation.iterators.*;
import org.litesoft.commonfoundation.typeutils.*;

import java.util.*;

@SuppressWarnings("Convert2Diamond")
public class DerivedLocaleGraph {
    private static final AbstractLocale BASE = Locale_en_US.INSTANCE;
    private static final Map<String, AbstractLocale> DEFAULT_LOCALE_BY_LANGUAGE = new HashMap<String, AbstractLocale>();
    private static final Map<String, AbstractLocale> LOCALE_BY_CODE = new HashMap<String, AbstractLocale>();
    private static final Map<AbstractLocale, AbstractLocale> DERIVED_FROM_LOCALE_BY_LOCALE = new HashMap<AbstractLocale, AbstractLocale>();

    static {
        addToDirectMaps( BASE );
        add( Locale_en_US.INSTANCE, Locale_en_GB.INSTANCE, Locale_en_CA.INSTANCE );
        add( Locale_es_ES.INSTANCE, Locale_es_MX.INSTANCE );
        add( Locale_fr_FR.INSTANCE, Locale_fr_CA.INSTANCE ); // Locale_fr_CH.INSTANCE );
        add( Locale_pt_PT.INSTANCE, Locale_pt_BR.INSTANCE );
        add( Locale_zh_CN.INSTANCE, Locale_zh_TW.INSTANCE );
        add( Locale_de_DE.INSTANCE ); // , Locale_de_AT.INSTANCE, Locale_de_CH.INSTANCE );
        add( Locale_it_IT.INSTANCE ); // , Locale_it_CH.INSTANCE );
        add( Locale_ja_JP.INSTANCE );
        add( Locale_ko_KR.INSTANCE );
    }

    public static DerivedLocaleGraph select( Set<AbstractLocale> pSupported ) {
        return new DerivedLocaleGraph( pSupported );
    }

    public static void add( AbstractLocale pBaseOrDerivedFrom, AbstractLocale... pDerived ) {
        if ( addToDirectMaps( pBaseOrDerivedFrom ) ) {
            setDerived( BASE, pBaseOrDerivedFrom );
        }
        for ( AbstractLocale zLocale : pDerived ) {
            addToDirectMaps( zLocale );
            setDerived( pBaseOrDerivedFrom, zLocale );
        }
    }

    private static void setDerived( AbstractLocale pDerivedFrom, AbstractLocale pDerived ) {
        pDerived.mDepth = pDerivedFrom.mDepth + 1;
        DERIVED_FROM_LOCALE_BY_LOCALE.put( pDerived, pDerivedFrom );
    }

    /**
     * @param pLocale !null
     *
     * @return true - If not already in the data structures
     */
    private static boolean addToDirectMaps( AbstractLocale pLocale ) {
        if ( null != LOCALE_BY_CODE.put( pLocale.getCode(), pLocale ) ) {
            return false; // Already There!
        }
        // New, so might be the first of this language
        if ( !DEFAULT_LOCALE_BY_LANGUAGE.containsKey( pLocale.getLanguage() ) ) {
            DEFAULT_LOCALE_BY_LANGUAGE.put( pLocale.getLanguage(), pLocale );
        }
        return true; // Added
    }

    private DerivedLocaleGraph( Set<AbstractLocale> pSupported ) {
        mSupported = pSupported;
    }

    private final Set<AbstractLocale> mSupported;

    public AbstractLocale from( String... pOrderedCodes ) {
        return from( new ArrayIterator<String>( pOrderedCodes ) );
    }

    public AbstractLocale from( List<String> pOrderedCodes ) {
        return from( Lists.deNullImmutable( pOrderedCodes ).iterator() );
    }

    public AbstractLocale from( Iterator<String> pOrderedCodes ) {
        AbstractLocale zBest = BASE;
        if ( pOrderedCodes != null ) {
            while ( pOrderedCodes.hasNext() ) {
                AbstractLocale zCandidate = candidateForFrom( pOrderedCodes.next() );
                if ( zCandidate.mDepth > zBest.mDepth ) {
                    zBest = zCandidate;
                }
            }
        }
        return zBest;
    }

    private AbstractLocale candidateForFrom( String pCode ) {
        AbstractLocale zLocale = LOCALE_BY_CODE.get( pCode );
        if ( null == zLocale ) {
            if ( null == (zLocale = DEFAULT_LOCALE_BY_LANGUAGE.get( AbstractLocale.languageFrom( pCode ) )) ) {
                return BASE;
            }
        }
        while ( !mSupported.contains( zLocale ) ) {
            if ( null == (zLocale = DERIVED_FROM_LOCALE_BY_LOCALE.get( zLocale )) ) {
                return BASE;
            }
        }
        return zLocale;
    }
}
