package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.model.Sessao;

import jakarta.persistence.EntityManager;
import java.util.List;

public class SessaoRepository {

    public SessaoRepository() {
    }

    public void adicionarSessao(Sessao sessao) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            if (sessao.getPeca() != null && sessao.getPeca().getId() > 0) {
                br.com.projetoteatro.model.Peca pecaManaged = em.find(br.com.projetoteatro.model.Peca.class,
                        sessao.getPeca().getId());
                if (pecaManaged != null) {
                    sessao.setPeca(pecaManaged);
                }
            }

            em.persist(sessao);

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

    public List<Sessao> listarSessao() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT s FROM Sessao s",
                    Sessao.class).getResultList();

        } finally {
            em.close();
        }
    }

    public void removerSessao(Sessao sessao) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Sessao sessaoGerenciada = em.find(Sessao.class, sessao.getId());

            if (sessaoGerenciada != null) {
                em.remove(sessaoGerenciada);
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

    public Sessao buscarSessao(long id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Sessao.class, id);

        } finally {
            em.close();
        }
    }

    public void atualizar(Sessao sessao) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(sessao);

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

    public List<Sessao> buscarPorPeca(String nomePeca) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT s FROM Sessao s WHERE LOWER(s.nomePeca) LIKE LOWER(:nome)",
                    Sessao.class)
                    .setParameter("nome", "%" + nomePeca + "%")
                    .getResultList();

        } finally {
            em.close();
        }
    }
}