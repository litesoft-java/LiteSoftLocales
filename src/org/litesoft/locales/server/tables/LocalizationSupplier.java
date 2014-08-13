package org.litesoft.locales.server.tables;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.server.tables.support.*;

public final class LocalizationSupplier {
    /* package friendly */ transient LocalizationSuppliers mOwner;
    /* package friendly */ transient String mPreviousName;
    private String name;
    private int confidencePercentage;

    @Deprecated LocalizationSupplier() { // reconstitution
    }

    public LocalizationSupplier( String pName, int pConfidencePercentage ) {
        setName( pName );
        setConfidencePercentage( pConfidencePercentage );
    }

    StringChange keyChanged() {
        if ( mPreviousName == null ) {
            return null;
        }
        String zPreviousName = mPreviousName;
        mPreviousName = null;
        return new StringChange( zPreviousName, name );
    }

    DuplicateKeyException reject( StringChange pChange ) {
        String zMessage = toString();
        name = pChange.getFrom();
        return new DuplicateKeyException( zMessage );
    }

    DuplicateKeyException rejectClaim() {
        mOwner = null;
        return new DuplicateKeyException( toString() );
    }

    public String getName() {
        return name;
    }

    public void setName( String pName ) {
        String zPreviousName = name;
        name = Confirm.significant( "Name", pName );
        if ( (zPreviousName != null) && (mPreviousName == null) ) {
            mPreviousName = zPreviousName;
        }
    }

    public int getConfidencePercentage() {
        return confidencePercentage;
    }

    public void setConfidencePercentage( int pConfidencePercentage ) {
        confidencePercentage = Integers.assertFromThru( "ConfidencePercentage", pConfidencePercentage, 1, 100 );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( name );
        if ( mPreviousName != null ) {
            sb.append( " (was: " ).append( mPreviousName ).append( ')' );
        }
        sb.append( ", Confidence: " ).append( confidencePercentage ).append( '%' );
        return sb.toString();
    }
}
