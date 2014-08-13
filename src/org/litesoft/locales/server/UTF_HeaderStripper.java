package org.litesoft.locales.server;

import org.litesoft.commonfoundation.base.*;

public class UTF_HeaderStripper {
    public static String[] fixFirstLine( String[] pLines ) {
        if ( Currently.isNotNullOrEmpty( pLines ) ) {
            String zLine = pLines[0];
            if ( (zLine != null) && (zLine.length() > 0) && (zLine.charAt( 0 ) == 65279) ) {
                String[] zLines = new String[pLines.length];
                zLines[0] = zLine.substring( 1 );
                System.arraycopy( pLines, 1, zLines, 1, pLines.length - 1 );
                return zLines;
            }
        }
        return pLines;
    }
}
