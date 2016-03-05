package com.joesbigidea.allowancetracker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by joe on 2/14/16.
 */
public class PersistenceService {
    private static EntityManagerFactory entityManagerFactory;

    public static void init(Properties props) throws Exception {
        entityManagerFactory = javax.persistence.Persistence.createEntityManagerFactory( "com.joesbigidea.allowancetracker", props);
    }

    public static <T> T apply(Function<EntityManager, T> funk) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return funk.apply(entityManager);
        }
        finally {
            entityManager.close();
        }
    }

    public static <T> T applyTransacted(Function<EntityManager, T> funk) {
       return apply(em -> {
            em.getTransaction().begin();
            try {
                T result = funk.apply(em);
                em.getTransaction().commit();
                return result;
            }
            catch (RuntimeException e) {
                em.getTransaction().rollback();
                throw e;
            }
        });
    }

    public static <T> T persist(T toPersist) {
        return applyTransacted(em -> em.merge(toPersist));
    }

    public static <T> List<T> persist(T... toPersist) {
       return applyTransacted(em -> Arrays.stream(toPersist).map(em::merge).collect(Collectors.toList()));
    }
}
