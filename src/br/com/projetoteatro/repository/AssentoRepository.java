package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.model.Assento;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AssentoRepository {

    public AssentoRepository() {
    }

    public void adicionarAssento(Assento assento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(assento);
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

    public List<Assento> listarAssentos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM Assento a", Assento.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Assento buscarPorCodigo(String codigo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Assento.class, codigo);
        } finally {
            em.close();
        }
    }

    public void atualizar(Assento assento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(assento);
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

    public void remover(Assento assento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Assento gerenciado = em.find(Assento.class, assento.getCodigo());
            if (gerenciado != null) {
                em.remove(gerenciado);
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

    public List<Assento> listarPorSetor(br.com.projetoteatro.enums.TipoSetor tipoSetor) {
        if (tipoSetor == null) {
            return List.of();
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT a FROM Assento a WHERE a.setor.tipoSetor = :tipo ORDER BY a.codigo ASC",
                    Assento.class)
                    .setParameter("tipo", tipoSetor)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public synchronized void inicializarAssentosPadraoSeNecessario() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long countAssentos = em.createQuery("SELECT COUNT(a) FROM Assento a", Long.class).getSingleResult();
            if (countAssentos != null && countAssentos > 0) {
                return;
            }

            em.getTransaction().begin();

            SetorRepository setorRepo = new SetorRepository();

            br.com.projetoteatro.model.Setor plateia = setorRepo
                    .buscarSetor(br.com.projetoteatro.enums.TipoSetor.PLATEIA);
            if (plateia == null) {
                plateia = new br.com.projetoteatro.model.Setor(br.com.projetoteatro.enums.TipoSetor.PLATEIA, 80.0, 100);
                em.persist(plateia);
            } else {
                plateia = em.find(br.com.projetoteatro.model.Setor.class, plateia.getId());
            }

            br.com.projetoteatro.model.Setor balcao = setorRepo
                    .buscarSetor(br.com.projetoteatro.enums.TipoSetor.BALCAO);
            if (balcao == null) {
                balcao = new br.com.projetoteatro.model.Setor(br.com.projetoteatro.enums.TipoSetor.BALCAO, 120.0, 40);
                em.persist(balcao);
            } else {
                balcao = em.find(br.com.projetoteatro.model.Setor.class, balcao.getId());
            }

            br.com.projetoteatro.model.Setor camarote = setorRepo
                    .buscarSetor(br.com.projetoteatro.enums.TipoSetor.CAMAROTE);
            if (camarote == null) {
                camarote = new br.com.projetoteatro.model.Setor(br.com.projetoteatro.enums.TipoSetor.CAMAROTE, 150.0,
                        20);
                em.persist(camarote);
            } else {
                camarote = em.find(br.com.projetoteatro.model.Setor.class, camarote.getId());
            }

            for (char fila = 'A'; fila <= 'B'; fila++) {
                for (int num = 1; num <= 8; num++) {
                    String cod = fila + String.valueOf(num);
                    em.persist(new Assento(cod, br.com.projetoteatro.enums.StatusAssento.DISPONIVEL, plateia));
                }
            }

            for (int num = 1; num <= 8; num++) {
                em.persist(new Assento("BL" + num, br.com.projetoteatro.enums.StatusAssento.DISPONIVEL, balcao));
            }

            for (int num = 1; num <= 6; num++) {
                em.persist(new Assento("CM" + num, br.com.projetoteatro.enums.StatusAssento.DISPONIVEL, camarote));
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