package org.litesoft.locales.shared.tables;

import org.litesoft.commonfoundation.annotations.*;
import org.litesoft.commonfoundation.indent.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.shared.tables.support.*;

public final class LocalizationSupplier extends AbstractKeyedOwned<LocalizationSuppliers> {
    @Override
    protected synchronized String getRawKey() {
        return name;
    }

    @Override
    protected synchronized void setRawKey( String pKey ) {
        name = pKey;
    }

    private String name;
    private volatile int confidencePercentage;

    @Deprecated LocalizationSupplier() { // reconstitution
    }

    public LocalizationSupplier( String pName, int pConfidencePercentage ) {
        setName( pName );
        setConfidencePercentage( pConfidencePercentage );
    }

    public String getName() {
        return getRawKey();
    }

    public void setName( String pName ) {
        updateKey( "Name", pName );
    }

    public int getConfidencePercentage() {
        return confidencePercentage;
    }

    public void setConfidencePercentage( int pConfidencePercentage ) {
        confidencePercentage = Integers.assertFromThru( "ConfidencePercentage", pConfidencePercentage, 1, 100 );
    }

    @Override
    protected void appendNonKeys( @NotNull IndentableWriter pWriter ) {
        pWriter.printLn( ", Confidence: ", confidencePercentage, "%" );
    }
}
