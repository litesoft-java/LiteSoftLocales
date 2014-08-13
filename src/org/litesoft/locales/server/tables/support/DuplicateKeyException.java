package org.litesoft.locales.server.tables.support;

public class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException( String message ) {
        super( message );
    }
}
