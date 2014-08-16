package org.litesoft.locales.shared.tables.support;

import org.litesoft.commonfoundation.annotations.*;
import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.indent.*;
import org.litesoft.locales.shared.tables.*;

/**
 * Implementation of this class should use a synchronization approach of:
 * <p/>
 * "Key" accessors should be synchronized, AND
 * all other variables (except Owner) should be volatile.
 * <p/>
 * This will allow for directed Locking: OwnedManager 1st, Owner's List 2nd, then Owned!
 */
public abstract class AbstractKeyedOwned<Owned extends AbstractKeyedOwned<Owned>> implements Indentable {
    /**
     * "Key" access should be synchronized!
     */
    abstract protected String getRawKey();

    /**
     * "Key" access should be synchronized!
     */
    abstract protected void setRawKey( String pKey );

    private transient String mKeyName;
    private transient KeyedOwnedManager<Owned> mOwner;

    protected AbstractKeyedOwned( String pKeyName ) {
        mKeyName = pKeyName;
    }

    /**
     * Must be called under a "lock"!
     */
    boolean claimFor( @NotNull KeyedOwnedManager<Owned> pNewOwner ) {
        pNewOwner = Confirm.isNotNull( "NewOwner", pNewOwner );
        if ( pNewOwner == mOwner ) {
            return false;
        }
        if ( mOwner != null ) {
            throw new OwnershipChangeException( "Can't change Ownership from '" + reference( mOwner ) + "' to: " + reference( pNewOwner ) );
        }
        mOwner = pNewOwner;
        return true;
    }

    /**
     * Must be called under a "lock"!
     */
    void releaseClaimFor( @NotNull KeyedOwnedManager<Owned> pExistingOwner ) { // Stupid Generics - Unable to properly identify w/o recursive reference - hence Object!
        if ( pExistingOwner != mOwner ) {
            throw new DontOwnException( "Can't releaseClaimFor(" + reference( pExistingOwner ) + "), Owned by: " + reference( mOwner ) );
        }
        mOwner = null;
    }

    private String reference( Object pObject ) {
        return (pObject == null) ? "null" : (ClassName.simple( pObject ) + ":" + System.identityHashCode( pObject ));
    }

    protected void updateKey( String pNewKey ) {
        pNewKey = validateNewKey( "New " + mKeyName, pNewKey );
        if ( mOwner == null ) {
            setRawKey( pNewKey );
        } else {
            mOwner.reIndex( this, mKeyName, pNewKey );
        }
    }

    protected String validateNewKey( String pWhat, String pKey ) {
        return Confirm.significant( pWhat, pKey );
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
