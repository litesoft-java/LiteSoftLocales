package org.litesoft.locales.shared;

public abstract class AbstractLocale {
    public static final String BAD_LANGUAGE_OR_COUNTRY = "??";

    private static final String LOCALE_CLASS_NAME_PREFIX = ".Locale_";

    private final boolean mActive;
    private final String mLanguage, mCountry, mCode;
    /* friend( DerivedLocaleGraph ) */ int mDepth;

    protected AbstractLocale( boolean pActive ) {
        mActive = pActive;
        String zName = "." + this.getClass().getName();
        int zAt = zName.indexOf( LOCALE_CLASS_NAME_PREFIX );
        if ( zAt == -1 ) {
            throw new IllegalStateException( "'" + LOCALE_CLASS_NAME_PREFIX + "' not found in: " + zName );
        }
        String zLanguageCountry = zName.substring( zAt + LOCALE_CLASS_NAME_PREFIX.length() );
        if ( !isValidStructuredCode( zLanguageCountry ) ) {
            throw new IllegalStateException( "No Language & Country found in: " + zName );
        }
        mLanguage = assertLowerCase( "Language", codeToLanguage( zLanguageCountry ) );
        mCountry = assertUpperCase( "Country", codeToCountry( zLanguageCountry ) );
        mCode = toCode( mLanguage, mCountry );
    }

    private static String assertLowerCase( String pWhat, String pString ) {
        return assertCase( pWhat, pString, pString.toLowerCase(), "LowerCase" );
    }

    private static String assertUpperCase( String pWhat, String pString ) {
        return assertCase( pWhat, pString, pString.toUpperCase(), "UpperCase" );
    }

    private static String assertCase( String pWhat, String pString, String pAdjustedString, String pCase ) {
        if ( pString.equals( pAdjustedString ) ) {
            return pString;
        }
        throw new IllegalStateException( pWhat + " '" + pString + "' NOT " + pCase );
    }

    protected AbstractLocale() {
        this( true );
    }

    public boolean isActive() {
        return mActive;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getCountry() {
        return mCountry;
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public boolean equals( Object o ) {
        return (this == o) || ((o instanceof AbstractLocale) && equals( (AbstractLocale) o ));
    }

    public boolean equals( AbstractLocale them ) {
        return (this == them) || ((them != null)
                                  && this.mCode.equals( them.mCode ));
    }

    @Override
    public int hashCode() {
        return mCode.hashCode();
    }

    @Override
    public String toString() {
        return getCode();
    }

    public static String languageFrom( String pCode ) {
        return isValidStructuredCode( pCode ) ? codeToLanguage( pCode ) : BAD_LANGUAGE_OR_COUNTRY;
    }

    public static String countryFrom( String pCode ) {
        return isValidStructuredCode( pCode ) ? codeToCountry( pCode ) : BAD_LANGUAGE_OR_COUNTRY;
    }

    public static String toCode( String pLanguage, String pCountry ) {
        return ensure2Char( pLanguage ).toLowerCase() + "_" + ensure2Char( pCountry ).toUpperCase();
    }

    private static boolean isValidStructuredCode( String pCode ) {
        return (pCode != null) && (pCode.length() == 5) && (pCode.charAt( 2 ) == '_');
    }

    private static String codeToLanguage( String pLanguageCountry ) {
        return pLanguageCountry.substring( 0, 2 );
    }

    private static String codeToCountry( String pLanguageCountry ) {
        return pLanguageCountry.substring( 3 );
    }

    private static String ensure2Char( String pString ) {
        if ( pString != null ) {
            if ( (pString = pString.trim()).length() == 2 ) {
                return pString;
            }
        }
        return BAD_LANGUAGE_OR_COUNTRY;
    }
}
