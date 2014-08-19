package org.litesoft.locales.shared.tables.support;

import org.litesoft.commonfoundation.indent.*;

import java.util.*;

/**
 * For this class to work "Owned" list must be manipulated in only two ways:
 * By this class, and
 * By the JSON deserializer *1*!
 * <p/>
 * *1* - The JSON deserializer will leave the "OwnedByKey" map as null.  This means that every "Owned" entry managed by this class will either have
 * no Current ownership but be inaccessable except thru this class (where we can give them Ownership), OR
 * be Owned by us as part of the "add" process!
 * <p/>
 * Lock management will use a directed Locking approach: Owner 1st, then Owned!
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public abstract class AbstractKeyedOwner<Owned extends AbstractKeyedOwned<Owned>> implements Indentable {
    protected transient KeyedOwnedManager<Owned> mManager;

    public void clear() {
        mManager.clear();
    }

    public int size() {
        return mManager.size();
    }

    public Owned get( String pKey ) {
        return mManager.get( pKey );
    }

    public void addAll( Owned... pOwned ) {
        mManager.addAll( pOwned );
    }

    public void addAll( List<Owned> pOwned ) {
        mManager.addAll( pOwned );
    }

    public Owned add( Owned pOwned ) {
        return mManager.add( pOwned );
    }

    public void removeAll( Owned... pOwned ) {
        mManager.removeAll( pOwned );
    }

    public void removeAll( List<Owned> pOwned ) {
        mManager.removeAll( pOwned );
    }

    public boolean remove( Owned pOwned ) {
        return mManager.remove( pOwned );
    }

    @Override
    public String toString() {
        return StringIndentableWriter.formatWith( this );
    }
}
