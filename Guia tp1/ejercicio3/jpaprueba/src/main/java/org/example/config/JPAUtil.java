package org.example.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    // El nombre debe coincidir exactamente con el <persistence-unit name="..."> del XML
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("PersistenceUnit");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}