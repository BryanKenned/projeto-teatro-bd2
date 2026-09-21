package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.model.Administrador;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AdministradorRepository {

    public AdministradorRepository() {
    }

    public void adicionarAdm(Administrador adm) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(adm);

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

    public Administrador buscarAdm(String cpf) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT a FROM Administrador a WHERE a.cpf = :cpf",
                            Administrador.class
                    )
                    .setParameter("cpf", cpf)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }

    public Administrador buscarAdmEmail(String email) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT a FROM Administrador a WHERE a.email = :email",
                            Administrador.class
                    )
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }

    public void excluirAdm(String cpf) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Administrador adm = em.createQuery(
                            "SELECT a FROM Administrador a WHERE a.cpf = :cpf",
                            Administrador.class
                    )
                    .setParameter("cpf", cpf)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (adm != null) {
                em.remove(adm);
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

    public boolean verSeAdmEstaPreenchido() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            Long quantidade = em.createQuery(
                    "SELECT COUNT(a) FROM Administrador a",
                    Long.class
            ).getSingleResult();

            return quantidade > 0;

        } finally {
            em.close();
        }
    }

    public List<Administrador> listarTodos() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT a FROM Administrador a",
                    Administrador.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    public void salvarOuAtualizar(Administrador adm) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Administrador existente = em.createQuery(
                            "SELECT a FROM Administrador a WHERE a.email = :email",
                            Administrador.class
                    )
                    .setParameter("email", adm.getEmail())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existente != null) {

                existente.setNome(adm.getNome());
                existente.setCpf(adm.getCpf());
                existente.setEmail(adm.getEmail());
                existente.setTelefone(adm.getTelefone());
                existente.setDataNascimento(adm.getDataNascimento());
                existente.setSexo(adm.getSexo());
                existente.setSenha(adm.getSenha());

                em.merge(existente);

            } else {

                em.persist(adm);
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
}