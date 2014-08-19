package org.litesoft.locales.shared.tables.support;

import org.litesoft.commonfoundation.annotations.*;
import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.indent.*;
import org.litesoft.commonfoundation.typeutils.*;

import java.util.*;

/**
 * For this class to work, the "Owned" list must be manipulated in only two ways:
 * By this class, and
 * By the JSON deserializer *1*!
 * <p/>
 * *1* - The JSON deserializer will leave the "OwnedByKey" map as null.  This means that every "Owned" entry managed by this class will either have
 * no Current ownership but be inaccessable except thru this class (where we can give them Ownership), OR
 * be Owned by us as part of the "add" process!
 * <p/>
 * Lock management will use a directed Locking approach: OwnedManager 1st, Owner's List 2nd, then Owned!
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public abstract class KeyedOwnedManager<Owned extends AbstractKeyedOwned<Owned>> implements Indentable {
    private transient String mWhat;
    private transient Map<String, Owned> mOwnedByKey;

    protected KeyedOwnedManager( String pWhat ) {
        mWhat = pWhat;
    }

    synchronized void reIndex( @NotNull Object pOwned, @NotNull String pWhat, @NotNull String pNewKey ) { // Lock "us" 1st
        Owned zOwned = Cast.it( pOwned ); // Stupid Generics - Unable to properly identify w/o recursive reference - hence Object above!
        Map<String, Owned> zMap = getMap();
        synchronized ( getOwnedList() ) { // Lock "Owner's List" 2nd
            synchronized ( zOwned ) { // Lock "Owned" 3rd
                String zRawKey = zOwned.getRawKey();
                if ( pNewKey.equals( zRawKey ) ) {
                    return; // No Change!
                }
                Owned zCurrentNewKeyOwned = zMap.get( pNewKey );
                if ( zCurrentNewKeyOwned != null ) {
                    throw new DuplicateKeyException( "Can't change '" + pWhat + "' to '" + pNewKey + "' on (" + zOwned + "), " +
                                                     "because the '" + pWhat + "' is already used by: " + zCurrentNewKeyOwned );
                }
                if ( zOwned != zMap.get( zRawKey ) ) {
                    throw new DontOwnException( "Can't change '" + pWhat + "' to '" + pNewKey + "', " +
                                                "because we (" + mWhat + ") don't seem to contain: " + zOwned );
                }
                zMap.remove( zRawKey );
                zMap.put( pNewKey, zOwned );
                zOwned.setRawKey( pNewKey );
            }
        }
    }

    abstract protected List<Owned> getOwnedList();

    protected synchronized Map<String, Owned> getMap() { // Lock "us" 1st
        if ( mOwnedByKey == null ) {
            List<Owned> zCurrentOwned = getOwnedList(); // Note: These "should" all be unowned!
            synchronized ( zCurrentOwned ) { // Lock "Owner's List" 2nd
                mOwnedByKey = Maps.newLinkedHashMap( zCurrentOwned.size() );
                if ( !zCurrentOwned.isEmpty() ) {
                    for ( Owned zOwned : zCurrentOwned ) {
                        claimAndAddToMap( zOwned, mOwnedByKey );
                    }
                }
            }
        }
        return mOwnedByKey;
    }

    @Override
    public String toString() {
        return StringIndentableWriter.formatWith( this );
    }

    @Override
    public IndentableWriter appendTo( @NotNull IndentableWriter pWriter ) {
        List<Owned> zAllOwned = getOwnedList();
        int zSize = zAllOwned.size();
        if ( zSize == 0 ) {
            pWriter.print( "No ", mWhat );
        } else {
            pWriter.printLn( mWhat, " (", zSize, "):" );
            pWriter.indent();
            for ( Owned zOwned : zAllOwned ) {
                zOwned.appendTo( pWriter );
            }
            pWriter.outdent();
        }
        return pWriter;
    }

    public synchronized void clear() { // Lock us 1st
        List<Owned> zOwneds = getOwnedList();
        synchronized ( zOwneds ) { // Lock Owned List 2nd
            for ( Owned zOwned : Lists.newArrayList( zOwneds ) ) {
                remove( zOwned );
            }
        }
    }

    public synchronized int size() {
        return getOwnedList().size();
    }

    public synchronized Owned get( String pKey ) {
        return getMap().get( pKey );
    }

    public synchronized void addAll( Owned... pOwned ) {
        for ( Owned zOwned : ConstrainTo.notNullImmutableList( pOwned ) ) {
            add( zOwned );
        }
    }

    public synchronized void addAll( List<Owned> pOwned ) {
        for ( Owned zOwned : ConstrainTo.notNull( pOwned ) ) {
            add( zOwned );
        }
    }

    public synchronized Owned add( Owned pOwned ) { // Lock us 1st!
        List<Owned> zOwnedList = getOwnedList();
        synchronized ( zOwnedList ) { // Lock "Owned list" 2nd
            if ( claimAndAddToMap( pOwned, getMap() ) ) {
                zOwnedList.add( pOwned );
            }
        }
        return pOwned;
    }

    public synchronized void removeAll( Owned... pOwned ) {
        for ( Owned zOwned : ConstrainTo.notNullImmutableList( pOwned ) ) {
            remove( zOwned );
        }
    }

    public synchronized void removeAll( List<Owned> pOwned ) {
        for ( Owned zOwned : ConstrainTo.notNull( pOwned ) ) {
            remove( zOwned );
        }
    }

    public synchronized boolean remove( Owned pOwned ) { // Lock us 1st!
        if ( pOwned == null ) {
            return false;
        }
        Map<String, Owned> zMap = getMap();
        List<Owned> zOwnedList = getOwnedList();
        synchronized ( zOwnedList ) { // Lock 2nd
            synchronized ( pOwned ) { // Lock 3nd
                String zRawKey = pOwned.getRawKey();
                pOwned.releaseClaimFor( this ); // asserts we Own it!
                removeFromMap( zMap, zRawKey, pOwned );
                removeFromList( zOwnedList, pOwned );
            }
        }
        return true;
    }

    private void removeFromMap( Map<String, Owned> pMap, String pRawKey, Owned pOwned ) {
        Owned zRemoved = pMap.remove( pRawKey );
        if ( pOwned != zRemoved ) {
            throw new IllegalStateException( "Owned! (" + pOwned + "), but Map said: " + zRemoved );
        }
    }

    private void removeFromList( List<Owned> pOwnedList, Owned pOwned ) {
        int zIndexOf = Lists.identityIndexOfIn( pOwned, pOwnedList );
        if ( zIndexOf == -1 ) {
            throw new IllegalStateException( "Owned! (" + pOwned + "), but Not in List!" );
        }
        pOwnedList.remove( zIndexOf );
    }

    /**
     * Must only be called under the double "lock" of this Owner & the Owned List!
     */
    private boolean claimAndAddToMap( Owned pOwned, Map<String, Owned> pOwnedByKey ) {
        synchronized ( pOwned ) { // Lock 3rd
            if ( !pOwned.claimFor( this ) ) {
                return false;
            }
            pOwnedByKey.put( pOwned.getRawKey(), pOwned );
            return true;
        }
    }
}
