package org.litesoft.locales.shared.tables;

import org.litesoft.commonfoundation.annotations.*;
import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.indent.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.shared.*;
import org.litesoft.locales.shared.tables.support.*;

import java.util.*;

public class LocaleTranslations extends AbstractKeyedOwner<LocaleTranslation> {
    private String locale;
    private final List<LocaleTranslation> translations = Lists.newArrayList();
    private static DerivedLocaleGraph sLocaleValidator;

    public LocaleTranslations() {
        mManager = new KeyedOwnedManager<LocaleTranslation>( "Translations" ) {
            @Override
            protected List<LocaleTranslation> getOwnedList() {
                return translations;
            }
        };
    }

    public LocaleTranslations( @NotNull String pLocale, @NotNull DerivedLocaleGraph pGraph ) {
        this();
        if ( pGraph != null ) {
            sLocaleValidator = pGraph;
        }
        if ( !setLocale( pLocale ) ) {
            throw new IllegalArgumentException( "Unknown / Unsupported Locale: " + pLocale );
        }
    }

    public LocaleTranslations( @NotNull String pLocale ) {
        this( pLocale, null );
    }

    public LocaleTranslations( @NotNull AbstractLocale pLocale ) {
        this();
        setLocale( pLocale );
    }

    public static void setLocaleValidator( DerivedLocaleGraph pLocaleValidator ) {
        sLocaleValidator = pLocaleValidator;
    }

    private static DerivedLocaleGraph getLocaleValidator() {
        DerivedLocaleGraph zLocaleValidator = sLocaleValidator;
        if ( zLocaleValidator == null ) {
            zLocaleValidator = DerivedLocaleGraph.SUPPLIER.get();
        }
        return Confirm.isNotNull( "LocaleValidator not set", zLocaleValidator );
    }

    public AbstractLocale getLocale() {
        return getLocaleValidator().from( locale );
    }

    /**
     * Sets the underlying Local Code using 'the' DerivedLocaleGraph.
     * <p/>
     * If the resulting local does not match the one requested then this method return false!
     */
    public boolean setLocale( @NotNull String pLocale ) {
        return setLocale( getLocaleValidator().from( pLocale = Confirm.significant( "Locale", pLocale ) ) ).equals( pLocale );
    }

    public String setLocale( AbstractLocale pLocale ) {
        return locale = Confirm.isNotNull( "Locale", pLocale ).getCode();
    }

    public synchronized LocaleTranslation[] getTranslations() {
        return translations.toArray( new LocaleTranslation[translations.size()] );
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void appendTo( @NotNull IndentableWriter pWriter ) {
        pWriter.printLn( "Locale: ", locale );
        mManager.appendTo( pWriter );
    }

    public void set( String pText_en_US, String pTranslatedText, LocalizationSupplier pSupplier ) {
        LocaleTranslation zTranslation = get( pText_en_US );
        if ( zTranslation == null ) { // Happy, but a bit unusual!
            add( new LocaleTranslation( pText_en_US, new TranslatedSet( pTranslatedText, pSupplier ) ) );
        } else {
            zTranslation.set( pTranslatedText, pSupplier );
        }
    }

    public String getTranslationFor( String pText_en_US, LocaleIssues pIssueCollector ) {
        LocaleTranslation zTranslation = get( pText_en_US );
        if ( zTranslation != null ) {
            return zTranslation.getTranslationFor( pIssueCollector );
        }
        pIssueCollector.noTranslation( pText_en_US );
        return pText_en_US;
    }
}
