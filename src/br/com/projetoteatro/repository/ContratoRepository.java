package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.model.Contrato;

import jakarta.persistence.EntityManager;

import java.util.List;

public class ContratoRepository {

    public ContratoRepository() {
    }

    public void adicionarContrato(Contrato contrato) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            if (contrato.getProposta() != null && contrato.getProposta().getId() != null) {
                br.com.projetoteatro.model.PropostaAluguel propManaged = em
                        .find(br.com.projetoteatro.model.PropostaAluguel.class, contrato.getProposta().getId());

                if (propManaged != null) {
                    contrato.setProposta(propManaged);
                }
            }

            em.persist(contrato);

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

    public List<Contrato> listarContrato() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Contrato c",
                    Contrato.class).getResultList();

        } finally {
            em.close();
        }
    }

    public void removerContrato(Contrato contrato) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Contrato contratoGerenciado = em.find(Contrato.class, contrato.getId());

            if (contratoGerenciado != null) {
                em.remove(contratoGerenciado);
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

    public Contrato buscarContrato(long id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Contrato.class, id);

        } finally {
            em.close();
        }
    }

    public void atualizar(Contrato contrato) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(contrato);

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

    public List<Contrato> listarContratosAtivos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Contrato c WHERE c.statusContrato = :status",
                    Contrato.class)
                    .setParameter(
                            "status",
                            br.com.projetoteatro.enums.StatusContrato.ATIVO)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    public List<Contrato> buscarPorNomePeca(String nomePeca) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Contrato c WHERE LOWER(c.nomePeca) LIKE LOWER(:nome)",
                    Contrato.class)
                    .setParameter("nome", "%" + nomePeca + "%")
                    .getResultList();

        } finally {
            em.close();
        }
    }

    public Contrato buscarPorProposta(Long propostaId) {
        if (propostaId == null) {
            return null;
        }
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Contrato c WHERE c.proposta.id = :propostaId",
                    Contrato.class)
                    .setParameter("propostaId", propostaId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }
}