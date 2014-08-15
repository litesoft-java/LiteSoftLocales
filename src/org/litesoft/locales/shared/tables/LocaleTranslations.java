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
        if ( !setLocaleUsing( pLocale, pGraph ) ) {
            throw new IllegalArgumentException( "Unknown / Unsupported Locale: " + pLocale );
        }
    }

    public AbstractLocale getLocale( @NotNull DerivedLocaleGraph pGraph ) {
        return pGraph.from( locale );
    }

    /**
     * Sets the underlying Local Code using the passed in DerivedLocaleGraph.
     * <p/>
     * If the resulting local does not match the one requested then this method return false!
     */
    public boolean setLocaleUsing( @NotNull String pLocale, @NotNull DerivedLocaleGraph pGraph ) {
        AbstractLocale zLocale = pGraph.from( pLocale = Confirm.significant( "Locale", pLocale ) );
        locale = zLocale.getCode();
        return locale.equals( pLocale );
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
}
