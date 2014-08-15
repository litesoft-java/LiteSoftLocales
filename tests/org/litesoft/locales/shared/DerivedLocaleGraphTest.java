package org.litesoft.locales.shared;

import org.junit.Test;
import org.litesoft.commonfoundation.typeutils.*;

import junit.framework.*;

import java.util.*;

public class DerivedLocaleGraphTest {
    private static final List<String> FRENCH_CANADIAN = toCodes( Locale.CANADA_FRENCH, Locale.FRENCH, Locale.CANADA, Locale.ENGLISH );
    private static final List<String> ENGLISH_CANADIAN = toCodes( Locale.CANADA, Locale.ENGLISH );
    private static final List<String> ENGLISH_US = toCodes( Locale.US, Locale.ENGLISH );
    private static final List<String> MEXICAN_US = toCodes( new Locale( "es", "US" ), new Locale( "es", "MX" ), new Locale( "es" ), Locale.US, Locale.ENGLISH );
    private static final List<String> BRITAIN = toCodes( Locale.UK, Locale.ENGLISH );
    private static final List<String> GERMAN = toCodes( Locale.GERMANY, Locale.GERMAN );
    private static final List<String> FRANCE = toCodes( Locale.FRANCE, Locale.FRENCH );
    private static final List<String> ITALY = toCodes( Locale.ITALY, Locale.ITALIAN );
    private static final List<String> JAPAN = toCodes( Locale.JAPAN, Locale.JAPANESE, Locale.ENGLISH );
    private static final List<String> KOREA = toCodes( Locale.KOREA, Locale.KOREAN );
    private static final List<String> CHINA = toCodes( Locale.CHINA, Locale.PRC, Locale.CHINESE, Locale.SIMPLIFIED_CHINESE );
    private static final List<String> TAIWAN = toCodes( Locale.TAIWAN, Locale.TRADITIONAL_CHINESE, Locale.CHINESE, Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH );

    private AbstractLocale[] SUPPORTED = { // Alphabetical
                                           Locale_de_DE.INSTANCE,
                                           Locale_en_US.INSTANCE, // Locale_en_CA.INSTANCE, Locale_en_GB.INSTANCE,
                                           Locale_es_ES.INSTANCE, // Locale_es_MX.INSTANCE,
                                           Locale_fr_FR.INSTANCE, // Locale_fr_CA.INSTANCE,
                                           Locale_it_IT.INSTANCE,
                                           Locale_ja_JP.INSTANCE,
                                           Locale_ko_KR.INSTANCE,
                                           Locale_zh_CN.INSTANCE, // Locale_zh_TW.INSTANCE,
    };

    @Test
    public void testSelect()
            throws Exception {
        expectSelected( Locale_fr_FR.INSTANCE, FRENCH_CANADIAN, null, SUPPORTED );
        expectSelected( Locale_fr_CA.INSTANCE, FRENCH_CANADIAN, Locale_fr_CA.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_CA.INSTANCE, FRENCH_CANADIAN, Locale_en_CA.INSTANCE, Locale_en_US.INSTANCE );
        expectSelected( Locale_en_US.INSTANCE, FRENCH_CANADIAN, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );

        expectSelected( Locale_en_US.INSTANCE, ENGLISH_CANADIAN, null, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, ENGLISH_CANADIAN, Locale_fr_CA.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_CA.INSTANCE, ENGLISH_CANADIAN, Locale_en_CA.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, ENGLISH_CANADIAN, Locale_en_GB.INSTANCE, SUPPORTED );

        expectSelected( Locale_en_US.INSTANCE, ENGLISH_US, Locale_en_US.INSTANCE );
        expectSelected( Locale_en_US.INSTANCE, ENGLISH_US, null, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, ENGLISH_US, Locale_en_GB.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, ENGLISH_US, Locale_en_CA.INSTANCE, SUPPORTED );

        expectSelected( Locale_es_ES.INSTANCE, MEXICAN_US, null, SUPPORTED );
        expectSelected( Locale_es_MX.INSTANCE, MEXICAN_US, Locale_es_MX.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, MEXICAN_US, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );

        expectSelected( Locale_en_US.INSTANCE, BRITAIN, null, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, BRITAIN, Locale_en_CA.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_GB.INSTANCE, BRITAIN, Locale_en_GB.INSTANCE, SUPPORTED );

        expectSelected( Locale_de_DE.INSTANCE, GERMAN, null, SUPPORTED );
        expectSelected( Locale_de_DE.INSTANCE, GERMAN, Locale_zh_TW.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, GERMAN, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );

        expectSelected( Locale_fr_FR.INSTANCE, FRANCE, null, SUPPORTED );
        expectSelected( Locale_fr_FR.INSTANCE, FRANCE, Locale_zh_TW.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, FRANCE, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );

        expectSelected( Locale_it_IT.INSTANCE, ITALY, null, SUPPORTED );
        expectSelected( Locale_it_IT.INSTANCE, ITALY, Locale_zh_TW.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, ITALY, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );

        expectSelected( Locale_ja_JP.INSTANCE, JAPAN, null, SUPPORTED );
        expectSelected( Locale_ja_JP.INSTANCE, JAPAN, Locale_zh_TW.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, JAPAN, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );

        expectSelected( Locale_ko_KR.INSTANCE, KOREA, null, SUPPORTED );
        expectSelected( Locale_ko_KR.INSTANCE, KOREA, Locale_zh_TW.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, KOREA, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );

        expectSelected( Locale_zh_CN.INSTANCE, CHINA, null, SUPPORTED );
        expectSelected( Locale_zh_CN.INSTANCE, CHINA, Locale_zh_TW.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, CHINA, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );

        expectSelected( Locale_zh_CN.INSTANCE, TAIWAN, null, SUPPORTED );
        expectSelected( Locale_zh_TW.INSTANCE, TAIWAN, Locale_zh_TW.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, TAIWAN, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );

        List<String> zTonga = toCodes( new Locale( "to", "TO" ), new Locale( "en", "AU" ), Locale.ENGLISH );
        expectSelected( Locale_en_US.INSTANCE, zTonga, null, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, zTonga, Locale_zh_TW.INSTANCE, SUPPORTED );
        expectSelected( Locale_en_US.INSTANCE, zTonga, Locale_en_GB.INSTANCE, Locale_en_US.INSTANCE );
    }

    private void expectSelected( AbstractLocale pExpected, List<String> pLocaleCodesToTest, AbstractLocale pAdditionalSupported, AbstractLocale... pSupported ) {
        Set<AbstractLocale> zSupported = Locales.toSet( pSupported );
        if ( pAdditionalSupported != null ) {
            zSupported.add( pAdditionalSupported );
        }
        AbstractLocale zSelected = DerivedLocaleGraph.select( zSupported ).from( pLocaleCodesToTest );
        if ( !pExpected.equals( zSelected ) ) {
            throw new AssertionFailedError( "\n"
                                            + "Expected: " + pExpected + "\n"
                                            + "  Actual: " + zSelected + "\n"
                                            + "Supported: " + Arrays.asList( pSupported ) + "\n"
                                            + "Acceptable: " + pLocaleCodesToTest
            );
        }
    }

    private static List<String> toCodes( Locale... pLocales ) {
        List<String> zCodes = Lists.newArrayList( pLocales.length );
        for ( Locale zLocale : pLocales ) {
            zCodes.add( AbstractLocale.toCode( zLocale.getLanguage(), zLocale.getCountry() ) );
        }
        return Collections.unmodifiableList( zCodes );
    }
}
