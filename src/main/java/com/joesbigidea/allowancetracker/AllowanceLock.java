package com.joesbigidea.allowancetracker;

import javax.persistence.*;

/**
 * Created by joe on 3/5/16.
 */
@Entity
public class AllowanceLock {
    public static AllowanceLock getLock(EntityManager em) {
        AllowanceLock allowanceLock = em.find(AllowanceLock.class, "LOCK");
        em.lock(allowanceLock, LockModeType.PESSIMISTIC_WRITE);
        return allowanceLock;
    }

    private String name;
    private long version;

    //hide it
    protected AllowanceLock() {}

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Version
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
