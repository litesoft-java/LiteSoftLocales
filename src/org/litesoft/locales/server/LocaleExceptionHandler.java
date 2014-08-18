package org.litesoft.locales.server;

public interface LocaleExceptionHandler {
    boolean handled( int pOffset, String pLine, RuntimeException pRTE );

    void completedProcessing( String pPath );
}
