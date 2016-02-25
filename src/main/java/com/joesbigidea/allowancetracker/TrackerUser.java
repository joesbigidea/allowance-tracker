package com.joesbigidea.allowancetracker;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.Optional;

/**
 * Created by joe on 2/21/16.
 */
@Entity
public class TrackerUser {
    private long id;
    private String name;
    private String fbUserId;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFbUserId() {
        return fbUserId;
    }

    public void setFbUserId(String fbUserId) {
        this.fbUserId = fbUserId;
    }

    public static Optional<TrackerUser> findByFbUserId(String fbUserId) {
        return PersistenceService.apply(em -> {
            List<TrackerUser> users = em.createQuery("FROM TrackerUser WHERE fbUserId = :id", TrackerUser.class).setParameter("id", fbUserId).getResultList();
            return users.stream().findFirst();
        });
    }
}
