package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.model.PropostaAluguel;

import jakarta.persistence.EntityManager;

import java.util.List;

public class PropostasRepository {

    public PropostasRepository() {
    }

    public void adicionarProposta(PropostaAluguel proposta) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            if (proposta.getContratante() != null && proposta.getContratante().getId() != null) {
                br.com.projetoteatro.model.Contratante contratanteGerenciado = em
                        .find(br.com.projetoteatro.model.Contratante.class, proposta.getContratante().getId());
                if (contratanteGerenciado != null) {
                    proposta.setContratante(contratanteGerenciado);
                }
            }

            em.persist(proposta);

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

    public List<PropostaAluguel> listarProposta() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM PropostaAluguel p",
                    PropostaAluguel.class).getResultList();

        } finally {
            em.close();
        }
    }

    public void removerProposta(PropostaAluguel proposta) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            PropostaAluguel propostaGerenciada = em.find(PropostaAluguel.class, proposta.getId());

            if (propostaGerenciada != null) {
                em.remove(propostaGerenciada);
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

    public PropostaAluguel buscarProposta(long id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(PropostaAluguel.class, id);

        } finally {
            em.close();
        }
    }

    public void atualizar(PropostaAluguel proposta) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(proposta);

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

    public List<PropostaAluguel> listarPropostas() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM PropostaAluguel p",
                    PropostaAluguel.class).getResultList();

        } finally {
            em.close();
        }
    }
}