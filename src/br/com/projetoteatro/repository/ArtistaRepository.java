package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.exceptions.CPFInvalidoException;
import br.com.projetoteatro.exceptions.ContratanteInvalidoException;
import br.com.projetoteatro.model.Contratante;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ArtistaRepository {

    public ArtistaRepository() {
    }

    public void adicionarArtista(Contratante artista) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(artista);

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

    public Contratante buscarContratante(String cpf) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT c FROM Contratante c WHERE c.cpf = :cpf",
                            Contratante.class
                    )
                    .setParameter("cpf", cpf)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }

    public Contratante buscarContratanteEmail(String email) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT c FROM Contratante c WHERE c.email = :email",
                            Contratante.class
                    )
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }

    public List<Contratante> listarArtista() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Contratante c",
                    Contratante.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    public void excluirArtista(Contratante artista)
            throws CPFInvalidoException, ContratanteInvalidoException {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Contratante artistaGerenciado =
                    em.find(Contratante.class, artista.getId());

            if (artistaGerenciado != null) {
                em.remove(artistaGerenciado);
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

    public void atualizar(Contratante artista) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(artista);
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

    public void salvarOuAtualizar(Contratante artista) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            if (artista.getId() == null) {
                em.persist(artista);
            } else {
                em.merge(artista);
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