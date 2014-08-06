package org.litesoft.locales.shared.support;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.typeutils.*;

import java.util.*;

public final class SimpleJSLocaleEntryStatement {
    public static class MalformedException extends RuntimeException {
        public MalformedException( String message ) {
            super( message );
        }
    }

    public static class DupEntryException extends RuntimeException {
        public DupEntryException( String message ) {
            super( message );
        }
    }

    private static final String LINE_PREFIX = "LNG['";
    private static final String KEY_TERMINATOR = "']";

    private static final String KEY_VALUE_SEP = "=";

    private static final String VALUE_PREFIX = "\"";
    private static final String VALUE_SUFFIX = "\";";

    private static final String[] OK_TWO_CHAR_ESCAPE_SEQUENCES = {"\\n", "\\\\", "\\\""};

    private final String mKey, mValue;

    public SimpleJSLocaleEntryStatement( String pKey, String pValue ) {
        mKey = validateKey( pKey );
        mValue = validateValue( pValue );
    }

    public static SimpleJSLocaleEntryStatement checkFromAdd( String pLine, Map<String, SimpleJSLocaleEntryStatement> pCurrentEntries ) {
        if ( null != (pLine = ConstrainTo.significantOrNull( pLine )) ) {
            if ( !pLine.startsWith( "//" ) ) {
                SimpleJSLocaleEntryStatement zEntry = from( pLine );
                if ( zEntry == null ) {
                    throw new MalformedException( pLine );
                }
                if ( null != pCurrentEntries.put( zEntry.getKey(), zEntry ) ) {
                    throw new DupEntryException( pLine );
                }
                return zEntry;
            }
        }
        return null;
    }

    public static SimpleJSLocaleEntryStatement from( String pLine ) {
        pLine = ConstrainTo.significantOrNull( pLine, "" );
        if ( pLine.startsWith( LINE_PREFIX ) && pLine.endsWith( VALUE_SUFFIX ) ) {
            int zKeyEnd = pLine.indexOf( KEY_TERMINATOR );
            int zKeyValueSep = pLine.indexOf( KEY_VALUE_SEP );
            int zValuePrefix = pLine.indexOf( VALUE_PREFIX );
            if ( (zKeyEnd != -1) && (zKeyEnd < zKeyValueSep) && (zKeyValueSep < zValuePrefix) ) {
                String zKey = pLine.substring( LINE_PREFIX.length(), zKeyEnd );
                String zPreSep = pLine.substring( zKeyEnd + KEY_TERMINATOR.length(), zKeyValueSep ).trim();
                String zPostSep = pLine.substring( zKeyValueSep + KEY_VALUE_SEP.length(), zValuePrefix ).trim();
                String zValue = pLine.substring( zValuePrefix + VALUE_PREFIX.length(), pLine.length() - VALUE_SUFFIX.length() );
                if ( (zPreSep.length() == 0) && (zPostSep.length() == 0) ) {
                    return new SimpleJSLocaleEntryStatement( zKey, zValue );
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return LINE_PREFIX + mKey + KEY_TERMINATOR + " " + KEY_VALUE_SEP + " " + VALUE_PREFIX + mValue + VALUE_SUFFIX;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    @Override
    public boolean equals( Object o ) {
        return (this == o) || ((o instanceof SimpleJSLocaleEntryStatement) && equals( (SimpleJSLocaleEntryStatement) o ));
    }

    public boolean equals( SimpleJSLocaleEntryStatement them ) {
        return (this == them) || ((them != null) &&
                                  this.mKey.equals( them.mKey ) &&
                                  this.mValue.equals( them.mValue ));
    }

    @Override
    public int hashCode() {
        return (31 * mKey.hashCode()) + mValue.hashCode();
    }

    private static String validateKey( String pKey ) {
        if ( pKey.length() == 0 ) {
            throw new IllegalArgumentException( "No Key Specified" );
        }
        if ( isHistoricFormKey( pKey ) ) {
            return pKey;
        }
        int from = 0;

        for ( int at; -1 != (at = pKey.indexOf( LocaleFileConstants.COMPOUND_KEY_SEP, from )); from = at + 1 ) {
            validateKeyPart( pKey, from, at );
        }
        validateKeyPart( pKey, from, pKey.length() );
        return pKey;
    }

    /**
     * A Historic Form Key is one that looks like a "static final" constant, e.g. Starts w/ an uppercase Letter and then consists of nothing but uppercase letters, numbers, and underscores.
     */
    private static boolean isHistoricFormKey( String pKey ) {
        if ( !Characters.isUpperCaseAsciiAlpha( pKey.charAt( 0 ) ) ) {
            return false;
        }
        for ( String zPart : Strings.parseChar( pKey, '_' ) ) {
            if ( !isHistoricFormKeyPart( zPart ) ) {
                return false;
            }
        }
        return true;
    }

    private static boolean isHistoricFormKeyPart( String pPart ) {
        if ( pPart.length() == 0 ) {
            return false;
        }
        for ( int i = 0; i < pPart.length(); i++ ) {
            char c = pPart.charAt( i );
            if ( !Characters.isUpperCaseAsciiAlpha( c ) && !Characters.isNumeric( c ) ) {
                return false;
            }
        }
        return true;
    }

    private static void validateKeyPart( String pKey, int pFrom, int pUpTo ) {
        check1stCharOfKey( pKey, pFrom++ );
        while ( pFrom < pUpTo ) {
            checkNthCharOfKey( pKey, pFrom++ );
        }
    }

    private static void check1stCharOfKey( String pKey, int pOffset ) {
        char c = pKey.charAt( pOffset );
        if ( !Characters.isAsciiAlpha( c ) ) {
            throw new IllegalArgumentException( "Character  '" + c + "' at offset " + pOffset + " unacceptable: not alpha; in Key: '" + pKey + "'" );
        }
    }

    private static void checkNthCharOfKey( String pKey, int pOffset ) {
        char c = pKey.charAt( pOffset );
        if ( (c != '_') && (c != '-') && !Characters.isAsciiAlpha( c ) && !Characters.isNumeric( c ) ) {
            throw new IllegalArgumentException(
                    "Character '" + c + "' at offset " + pOffset + " unacceptable: not alpha, numeric, dash, or underscore; in Key: '" + pKey + "'" );
        }
    }

    private static String validateValue( String pValue ) {
        StringBuilder zSB = new StringBuilder();
        int from = 0;
        for ( int at; -1 != (at = find( pValue, from, OK_TWO_CHAR_ESCAPE_SEQUENCES )); from = at + 2 ) {
            zSB.append( validateChunk( pValue, from, at ) );
            zSB.append( pValue.substring( at, at + 2 ) );
        }
        zSB.append( validateChunk( pValue, from, pValue.length() ) );
        return zSB.toString();
    }

    private static String validateChunk( String pValue, int pFrom, int pUpto ) {
        for ( int i = pFrom; i < pUpto; i++ ) {
            char c = pValue.charAt( i );
            if ( (c == '"') || (c == '\\') ) {
                throw new IllegalArgumentException( "Unescaped '" + c + "' at offset " + i + " in Value: '" + pValue + "'" );
            }
        }
        return pValue.substring( pFrom, pUpto );
    }

    private static int find( String pValue, int pFrom, String... pOkTwoCharEscapeSequences ) {
        int zBest = pValue.indexOf( pOkTwoCharEscapeSequences[0], pFrom );
        for ( int i = 1; i < pOkTwoCharEscapeSequences.length; i++ ) {
            int zCur = pValue.indexOf( pOkTwoCharEscapeSequences[i], pFrom );
            if ( (zBest == -1) || ((zCur != -1) && (zCur < zBest)) ) {
                zBest = zCur;
            }
        }
        return zBest;
    }
}
