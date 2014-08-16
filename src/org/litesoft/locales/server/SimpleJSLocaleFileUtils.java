package org.litesoft.locales.server;

import org.litesoft.commonfoundation.console.Console;
import org.litesoft.locales.shared.*;
import org.litesoft.locales.shared.support.*;
import org.litesoft.server.file.*;

import java.io.*;
import java.util.*;

public class SimpleJSLocaleFileUtils {
    private final Console mConsole;

    public SimpleJSLocaleFileUtils( Console pConsole ) {
        mConsole = pConsole;
    }

    public String[] loadAndValidateLocale( Map<String, SimpleJSLocaleEntryStatement> pCollector, LocalePaths.To pPathsTo, AbstractLocale pLocale ) {
        String zPath = pPathsTo.locale( pLocale );
        mConsole.println( "Loading File: " + zPath );
        File zFile = new File( zPath );
        String[] zLines = FileUtils.loadTextFile( zFile );
        for ( String zLine : zLines ) {
            SimpleJSLocaleEntryStatement.checkFromAdd( zLine, pCollector );
        }
        return zLines;
    }

    public String[] loadAndValidate_en_US( Map<String, SimpleJSLocaleEntryStatement> pCollector, LocalePaths.To pPathsTo ) {
        return loadAndValidateLocale( pCollector, pPathsTo, Locale_en_US.INSTANCE );
    }
}
