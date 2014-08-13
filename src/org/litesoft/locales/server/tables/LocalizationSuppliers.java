package org.litesoft.locales.server.tables;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.locales.server.tables.support.*;

import java.util.*;

public class LocalizationSuppliers {
    private List<LocalizationSupplier> suppliers = Lists.newArrayList();
    private transient Map<String, LocalizationSupplier> mSuppliersByName;

    public static int calcConfidencePercentage( List<LocalizationSupplier> pSuppliers ) {
        pSuppliers = ConstrainTo.notNull( pSuppliers );
        return calcConfidencePercentage( pSuppliers.toArray( new LocalizationSupplier[pSuppliers.size()] ) );
    }

    private synchronized Map<String, LocalizationSupplier> getSuppliersByName() {
        if ( mSuppliersByName == null ) {
            mSuppliersByName = Maps.newLinkedHashMap( suppliers.size() );
            if ( !suppliers.isEmpty() ) {
                LocalizationSupplier[] zSuppliers = suppliers.toArray( new LocalizationSupplier[suppliers.size()] );
                suppliers.clear();
                addAll( zSuppliers );
            }
        }
        return mSuppliersByName;
    }

    public synchronized LocalizationSupplier get( String pName ) {
        return getSuppliersByName().get( pName );
    }

    public synchronized LocalizationSuppliers clear() {
        removeAll( suppliers.toArray( new LocalizationSupplier[suppliers.size()] ) );
        return this;
    }

    public synchronized void addAll( LocalizationSupplier... pSuppliers ) {
        for ( LocalizationSupplier zSupplier : ConstrainTo.notNullImmutableList( pSuppliers ) ) {
            add( zSupplier );
        }
    }

    public synchronized void updateAll( LocalizationSupplier... pSuppliers ) {
        for ( LocalizationSupplier zSupplier : ConstrainTo.notNullImmutableList( pSuppliers ) ) {
            update( zSupplier );
        }
    }

    public synchronized void removeAll( LocalizationSupplier... pSuppliers ) {
        for ( LocalizationSupplier zSupplier : ConstrainTo.notNullImmutableList( pSuppliers ) ) {
            remove( zSupplier );
        }
    }

    public synchronized LocalizationSupplier add( LocalizationSupplier pSupplier ) {
        if ( claim( pSupplier ) ) {
            if ( mSuppliersByName.containsKey( pSupplier.getName() ) ) {
                throw pSupplier.rejectClaim();
            }
            pSupplier.mPreviousName = null;
            mSuppliersByName.put( pSupplier.getName(), pSupplier );
            suppliers.add( pSupplier );
        }
        return pSupplier;
    }

    public synchronized void update( LocalizationSupplier pSupplier ) {
        if ( assertOwned( pSupplier ) ) {
            StringChange zChange = pSupplier.keyChanged();
            if ( zChange != null ) {
                if ( mSuppliersByName.containsKey( pSupplier.getName() ) ) {
                    throw pSupplier.reject( zChange );
                }
                mSuppliersByName.remove( zChange.getFrom() );
                mSuppliersByName.put( pSupplier.getName(), pSupplier );
            }
        }
    }

    public synchronized boolean remove( LocalizationSupplier pSupplier ) {
        if ( assertOwned( pSupplier ) ) {
            StringChange zChange = pSupplier.keyChanged();
            mSuppliersByName.remove( (zChange != null) ? zChange.getFrom() : pSupplier.getName() );
            return listRemoveByIdentity( pSupplier );
        }
        return false;
    }

    private boolean listRemoveByIdentity( LocalizationSupplier pSupplier ) {
        for ( int i = 0; i < suppliers.size(); i++ ) {
            if ( pSupplier == suppliers.get( i ) ) {
                suppliers.remove( i );
                pSupplier.mOwner = null;
                return true;
            }
        }
        return false;
    }

    private boolean assertOwned( LocalizationSupplier pSupplier ) {
        if ( pSupplier == null ) {
            return false;
        }
        if ( pSupplier.mOwner == this ) {
            return true;
        }
        throw new DontOwnException( pSupplier.toString() );
    }

    private boolean claim( LocalizationSupplier pSupplier ) {
        if ( pSupplier == null ) {
            return false;
        }

        if ( pSupplier.mOwner != null ) {
            throw new OwnershipChangeException( toString() );
        }
        pSupplier.mOwner = this;
        return true;
    }

    public static int calcConfidencePercentage( LocalizationSupplier... pSuppliers ) {
        Confirm.isNotNullOrEmptyAndHasNoNullEntries( "Suppliers", pSuppliers );
        int zPercentage = pSuppliers[0].getConfidencePercentage();
        if ( (zPercentage == 100) || (pSuppliers.length == 1) ) {
            return zPercentage;
        }
        double zDoubtFactor = toDoubtFactor( zPercentage );
        for ( int i = 1; i < pSuppliers.length; i++ ) {
            if ( 100 == (zPercentage = pSuppliers[i].getConfidencePercentage()) ) {
                return zPercentage;
            }
            zDoubtFactor *= toDoubtFactor( zPercentage );
        }
        return 100 - (int) (100 * zDoubtFactor);
    }

    private static double toDoubtFactor( int pPercentage ) {
        return ((double) (100 - pPercentage)) / 100.0;
    }
}
