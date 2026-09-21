package br.com.projetoteatro.test;

import br.com.projetoteatro.config.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JPAUtilTest {

    @Test
    @DisplayName("Deve obter EntityManager aberto e conseguir gerenciar transação")
    public void deveObterEntityManager() {
        EntityManager em = JPAUtil.getEntityManager();
        assertNotNull(em, "EntityManager não deve ser nulo");
        assertTrue(em.isOpen(), "EntityManager deve estar aberto");

        em.getTransaction().begin();
        assertTrue(em.getTransaction().isActive(), "Transação deve estar ativa");
        em.getTransaction().commit();

        em.close();
        assertFalse(em.isOpen(), "EntityManager deve ser fechado corretamente");
    }
}
