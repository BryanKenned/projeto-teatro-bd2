package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.model.RegraAluguel;
import jakarta.persistence.EntityManager;
import java.util.List;

public class RegrasPrecoRepository {

    public RegrasPrecoRepository() {
    }

    public void adicionarRegra(RegraAluguel regra) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(regra);

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    public List<RegraAluguel> listarRegra() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM RegraAluguel r",
                    RegraAluguel.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    public void removerRegra(RegraAluguel regra) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            RegraAluguel regraGerenciada =
                    em.find(RegraAluguel.class, regra.getId());

            if (regraGerenciada != null) {
                em.remove(regraGerenciada);
            }

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    public RegraAluguel buscarRegra(long id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(RegraAluguel.class, id);

        } finally {
            em.close();
        }
    }

    public void atualizar(RegraAluguel regra) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(regra);

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    public List<RegraAluguel> listarRegras() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM RegraAluguel r",
                    RegraAluguel.class
            ).getResultList();

        } finally {
            em.close();
        }
    }
}