package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.model.Peca;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PecaRepository {

    public PecaRepository() {
    }

    public void adicionarPeca(Peca peca) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(peca);
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

    public List<Peca> listarPecas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Peca p", Peca.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Peca buscarPeca(long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Peca.class, id);
        } finally {
            em.close();
        }
    }

    public List<Peca> buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Peca p WHERE LOWER(p.nome) LIKE LOWER(:nome)",
                    Peca.class)
                    .setParameter("nome", "%" + nome + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(Peca peca) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(peca);
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

    public void removerPeca(Peca peca) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Peca gerenciada = em.find(Peca.class, peca.getId());
            if (gerenciada != null) {
                em.remove(gerenciada);
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

    public List<Peca> buscarPecasPorArtistaComJoin(String nomeArtista) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Peca p " +
                            "JOIN p.artistaResponsavel a " +
                            "WHERE LOWER(a.nome) LIKE LOWER(:nomeArtista)",
                    Peca.class)
                    .setParameter("nomeArtista", "%" + nomeArtista + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Long contarTotalPecas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Peca p", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}