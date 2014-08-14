package org.litesoft.locales.shared.tables.support;

import org.litesoft.commonfoundation.annotations.*;
import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.indent.*;

/**
 * Implementation of this class should use a synchronization approach of:
 * <p/>
 * "Key" accessors should be synchronized, AND
 * all other variables should variables (except Owner) should be volatile.
 * <p/>
 * This will allow for a directed Locking: Owner 1st, then Owned!
 */
public abstract class AbstractKeyedOwned<Owner extends AbstractKeyedOwner<?>> implements Indentable {
    /**
     * "Key" access should be synchronized!
     */
    abstract protected String getRawKey();

    /**
     * "Key" access should be synchronized!
     */
    abstract protected void setRawKey( String pKey );

    private transient Owner mOwner;

    /**
     * Must be called under a "lock"!
     */
    boolean claimFor( @NotNull Object pNewOwner ) { // Stupid Generics - Unable to properly identify w/o recursive reference - hence Object!
        Owner zNewOwner = Cast.it( Confirm.isNotNull( "NewOwner", pNewOwner ) );
        if ( zNewOwner == mOwner ) {
            return false;
        }
        if ( mOwner != null ) {
            throw new OwnershipChangeException( "Can't change Ownership from '" + reference( mOwner ) + "' to: " + reference( zNewOwner ) );
        }
        mOwner = zNewOwner;
        return true;
    }

    /**
     * Must be called under a "lock"!
     */
    void releaseClaimFor( @NotNull Object pExistingOwner ) { // Stupid Generics - Unable to properly identify w/o recursive reference - hence Object!
        if ( pExistingOwner != mOwner ) {
            throw new DontOwnException( "Can't releaseClaimFor(" + reference( pExistingOwner ) + "), Owned by: " + reference( mOwner ) );
        }
        mOwner = null;
    }

    private String reference( Object pObject ) {
        return (pObject == null) ? "null" : (ClassName.simple( pObject ) + ":" + System.identityHashCode( pObject ));
    }

    protected void updateKey( @NotNull String pWhat, String pNewKey ) {
        pNewKey = Confirm.significant( pWhat, pNewKey );
        if ( mOwner == null ) {
            setRawKey( pNewKey );
        } else {
            mOwner.reIndex( this, pWhat, pNewKey );
        }
    }

    @Override
    public String toString() {
        StringIndentableWriter zWriter = new StringIndentableWriter( "    " );
        appendTo( zWriter );
        zWriter.close();
        return zWriter.toString();
    }

    @Override
    public void appendTo( @NotNull IndentableWriter pWriter ) {
        pWriter.print( getRawKey() );
        appendNonKeys( pWriter );
    }

    protected abstract void appendNonKeys( @NotNull IndentableWriter pWriter );
}
