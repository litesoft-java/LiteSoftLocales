package org.litesoft.locales.shared.support;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.exceptions.*;
import org.litesoft.commonfoundation.typeutils.*;

import java.util.*;

@SuppressWarnings("DuplicateThrows")
public final class SimpleJSLocaleEntryStatementLineParser implements KeyValueStringsLineParser,
                                                                     SimpleJSLocaleEntryConstants {
    public static final SimpleJSLocaleEntryStatementLineParser INSTANCE = new SimpleJSLocaleEntryStatementLineParser();

    public static SimpleJSLocaleEntryStatement checkFromAdd( String pLine, Map<String, SimpleJSLocaleEntryStatement> pCurrentEntries ) {
        if ( null != (pLine = significantOrNullLine( pLine )) ) {
            SimpleJSLocaleEntryStatement zEntry = INSTANCE.parse( pLine );
            if ( null != pCurrentEntries.put( zEntry.getKey(), zEntry ) ) {
                throw new DupEntryException( pLine );
            }
            return zEntry;
        }
        return null;
    }

    @Override
    public SimpleJSLocaleEntryStatement create( String pKey, String pValue )
            throws IllegalArgumentException {
        return new SimpleJSLocaleEntryStatement( pKey, pValue );
    }

    @Override
    public SimpleJSLocaleEntryStatement parse( String pLine )
            throws MalformedException, IllegalArgumentException {
        if ( null == (pLine = significantOrNullLine( pLine )) ) {
            return null;
        }
        List<String> zChunks = Strings.chunk( pLine, SEPARATORS );
        if ( Strings.areWhiteSpaceEntries( zChunks, WHITE_SPACES_AT ) ) {
            return create( zChunks.get( KEY_AT ), zChunks.get( VALUE_AT ) );
        }
        throw new MalformedException( pLine );
    }

    public static String significantOrNullLine( String pLine ) {
        if ( (pLine = ConstrainTo.significantOrNull( pLine, "//" )).startsWith( "//" ) ) {
            return null;
        }
        if ( pLine.startsWith( "/*" ) || pLine.endsWith( "*/" ) ) {
            String zLine = pLine.toLowerCase();
            if ( zLine.contains( "todo: " ) && zLine.contains( " translate " ) ) {
                return null;
            }
        }
        return pLine;
    }
}
