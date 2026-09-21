package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.model.Usuario;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ClienteRepository {

    public ClienteRepository() {
    }

    public void adicionarCliente(Usuario cliente) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(cliente);

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

    public Usuario buscarCliente(String cpf) {
        if (cpf == null) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            Usuario usuario = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.cpf = :cpf",
                    Usuario.class)
                    .setParameter("cpf", cpf)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                String cpfLimpo = cpf.replaceAll("[^0-9]", "");
                if (!cpfLimpo.isEmpty() && !cpfLimpo.equals(cpf)) {
                    usuario = em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.cpf = :cpfLimpo",
                            Usuario.class)
                            .setParameter("cpfLimpo", cpfLimpo)
                            .getResultStream()
                            .findFirst()
                            .orElse(null);
                }
            }

            return usuario;

        } finally {
            em.close();
        }
    }

    public Usuario buscarClienteEmail(String email) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.email = :email",
                    Usuario.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }

    public List<Usuario> listarCliente() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT u FROM Usuario u",
                    Usuario.class).getResultList();

        } finally {
            em.close();
        }
    }

    public void removerCliente(Usuario cliente) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Usuario clienteGerenciado = em.find(Usuario.class, cliente.getId());

            if (clienteGerenciado != null) {
                em.remove(clienteGerenciado);
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

    public void atualizar(Usuario cliente) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(cliente);
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

    public void salvarOuAtualizar(Usuario cliente) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            if (cliente.getId() == null) {
                em.persist(cliente);
            } else {
                em.merge(cliente);
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