package br.com.projetoteatro.repository;

import br.com.projetoteatro.enums.TipoSetor;
import br.com.projetoteatro.model.Setor;
import br.com.projetoteatro.config.JPAUtil;

import jakarta.persistence.EntityManager;
import java.util.List;

public class SetorRepository {

    public void adicionarSetor(Setor setor) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(setor);

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

    public List<Setor> listarSetores() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT s FROM Setor s",
                    Setor.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    public void removerSetor(Setor setor) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Setor setorGerenciado = em.find(Setor.class, setor.getId());

            if (setorGerenciado != null) {
                em.remove(setorGerenciado);
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

    public void atualizar(Setor setor) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(setor);

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

    public Setor buscarSetor(TipoSetor tipoSetor) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT s FROM Setor s WHERE s.tipoSetor = :tipo",
                            Setor.class
                    )
                    .setParameter("tipo", tipoSetor)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }
}