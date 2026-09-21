package br.com.projetoteatro.repository;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.dto.EstatisticaVendasDTO;
import br.com.projetoteatro.dto.IngressoResumoDTO;
import br.com.projetoteatro.model.Ingresso;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class IngressoRepository {

    public IngressoRepository() {
    }

    public void adicionarIngresso(Ingresso ingresso) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            if (ingresso.getCliente() != null && ingresso.getCliente().getId() != null) {
                br.com.projetoteatro.model.Usuario usrManaged = em.find(br.com.projetoteatro.model.Usuario.class,
                        ingresso.getCliente().getId());
                if (usrManaged != null) {
                    ingresso.setCliente(usrManaged);
                }
            }

            if (ingresso.getSessao() != null && ingresso.getSessao().getId() != null) {
                br.com.projetoteatro.model.Sessao sessManaged = em.find(br.com.projetoteatro.model.Sessao.class,
                        ingresso.getSessao().getId());
                if (sessManaged != null) {
                    ingresso.setSessao(sessManaged);
                }
            }

            if (ingresso.getContrato() != null && ingresso.getContrato().getId() != null) {
                br.com.projetoteatro.model.Contrato contManaged = em.find(br.com.projetoteatro.model.Contrato.class,
                        ingresso.getContrato().getId());
                if (contManaged != null) {
                    ingresso.setContrato(contManaged);
                }
            }

            if (ingresso.getAssento() != null && ingresso.getAssento().getCodigo() != null) {
                br.com.projetoteatro.model.Assento assManaged = em.find(br.com.projetoteatro.model.Assento.class,
                        ingresso.getAssento().getCodigo());
                if (assManaged != null) {
                    ingresso.setAssento(assManaged);
                }
            }

            em.persist(ingresso);

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

    public List<Ingresso> listarIngresso() {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT i FROM Ingresso i",
                    Ingresso.class).getResultList();

        } finally {
            em.close();
        }
    }

    public void removerIngresso(Ingresso ingresso) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Ingresso ingressoGerenciado = em.find(Ingresso.class, ingresso.getId());

            if (ingressoGerenciado != null) {
                em.remove(ingressoGerenciado);
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

    public void atualizar(Ingresso ingresso) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(ingresso);

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

    public Ingresso buscarIngresso(long id) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Ingresso.class, id);

        } finally {
            em.close();
        }
    }

    public double calcularFaturamentoMesAtual() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();

            LocalDateTime fimMes = LocalDate.now()
                    .withDayOfMonth(LocalDate.now().lengthOfMonth())
                    .atTime(23, 59, 59);

            Double total = em.createQuery(
                    "SELECT SUM(i.valor) FROM Ingresso i " +
                            "WHERE i.dataCompra >= :inicio AND i.dataCompra <= :fim",
                    Double.class)
                    .setParameter("inicio", inicioMes)
                    .setParameter("fim", fimMes)
                    .getSingleResult();

            return total != null ? total : 0.0;

        } finally {
            em.close();
        }
    }

    public long contarIngressosVendidosHoje() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            LocalDateTime inicioHoje = LocalDate.now().atStartOfDay();

            LocalDateTime fimHoje = LocalDate.now().atTime(23, 59, 59);

            Long total = em.createQuery(
                    "SELECT COUNT(i) FROM Ingresso i " +
                            "WHERE i.dataCompra >= :inicio AND i.dataCompra <= :fim",
                    Long.class)
                    .setParameter("inicio", inicioHoje)
                    .setParameter("fim", fimHoje)
                    .getSingleResult();

            return total != null ? total : 0L;

        } finally {
            em.close();
        }
    }

    public List<Ingresso> buscarPorContrato(long contratoId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT i FROM Ingresso i " +
                            "WHERE i.contrato.id = :contratoId",
                    Ingresso.class)
                    .setParameter("contratoId", contratoId)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    public List<Ingresso> buscarPorData(LocalDate data) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT i FROM Ingresso i " +
                            "WHERE i.sessao.data = :data",
                    Ingresso.class)
                    .setParameter("data", data)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    /**
     * Calcula o faturamento total acumulado de todos os ingressos vendidos.
     *
     * @return soma monetária total dos ingressos ou 0.0 se não houver registros
     */
    public Double somarFaturamentoTotal() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double total = em.createQuery(
                    "SELECT COALESCE(SUM(i.valor), 0.0) FROM Ingresso i",
                    Double.class).getSingleResult();
            return total != null ? total : 0.0;
        } finally {
            em.close();
        }
    }

    /**
     * Calcula o preço médio dos ingressos vendidos.
     *
     * @return média de valor dos ingressos ou 0.0 se vazio
     */
    public Double calcularPrecoMedioIngresso() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double media = em.createQuery(
                    "SELECT COALESCE(AVG(i.valor), 0.0) FROM Ingresso i",
                    Double.class).getSingleResult();
            return media != null ? media : 0.0;
        } finally {
            em.close();
        }
    }

    /**
     * Retorna a quantidade total de ingressos registrados.
     *
     * @return total absoluto de ingressos vendidos
     */
    public Long contarTotalIngressosVendidos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(i) FROM Ingresso i",
                    Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Localiza todos os ingressos de um cliente filtrando pelo CPF do comprador.
     *
     * @param cpf CPF do cliente
     * @return lista de ingressos vinculados ao cliente
     */
    public List<Ingresso> buscarIngressosPorCpfClienteComJoin(String cpf) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT i FROM Ingresso i " +
                            "JOIN i.cliente c " +
                            "WHERE c.cpf = :cpf",
                    Ingresso.class)
                    .setParameter("cpf", cpf)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Localiza ingressos associados a uma peça.
     *
     * @param nomePeca nome ou trecho do nome da peça
     * @return lista de ingressos vinculados à peça
     */
    public List<Ingresso> buscarIngressosPorNomePecaComJoin(String nomePeca) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT i FROM Ingresso i " +
                            "JOIN i.sessao s " +
                            "WHERE LOWER(s.nomePeca) LIKE LOWER(:nomePeca)",
                    Ingresso.class)
                    .setParameter("nomePeca", "%" + nomePeca + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Recupera uma lista de DTOs {@link IngressoResumoDTO} instanciados diretamente
     * na projeção JPQL.
     *
     * @return lista de resumos de ingressos em objetos DTO
     */
    public List<IngressoResumoDTO> listarResumoIngressosDTO() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT new br.com.projetoteatro.dto.IngressoResumoDTO(" +
                            "i.codigo, c.nome, c.email, s.nomePeca, s.data, s.horarioInicio, " +
                            "a.codigo, i.setor, i.valor) " +
                            "FROM Ingresso i " +
                            "JOIN i.cliente c " +
                            "JOIN i.sessao s " +
                            "LEFT JOIN i.assento a",
                    IngressoResumoDTO.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Projeta estatísticas consolidadas de vendas diretamente no DTO
     * {@link EstatisticaVendasDTO}.
     *
     * @return DTO contendo totais e médias calculados no banco de dados
     */
    public EstatisticaVendasDTO obterEstatisticaVendasDTO() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT new br.com.projetoteatro.dto.EstatisticaVendasDTO(" +
                            "COUNT(i), COALESCE(SUM(i.valor), 0.0), COALESCE(AVG(i.valor), 0.0)) " +
                            "FROM Ingresso i",
                    EstatisticaVendasDTO.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Busca pontual de ingresso por código projetando diretamente para DTO.
     *
     * @param codigo UUID do ingresso
     * @return DTO com os detalhes do ingresso ou null se não encontrado
     */
    public IngressoResumoDTO buscarResumoIngressoPorCodigoDTO(String codigo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT new br.com.projetoteatro.dto.IngressoResumoDTO(" +
                            "i.codigo, c.nome, c.email, s.nomePeca, s.data, s.horarioInicio, " +
                            "a.codigo, i.setor, i.valor) " +
                            "FROM Ingresso i " +
                            "JOIN i.cliente c " +
                            "JOIN i.sessao s " +
                            "LEFT JOIN i.assento a " +
                            "WHERE i.codigo = :codigo",
                    IngressoResumoDTO.class)
                    .setParameter("codigo", codigo)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    public List<String> buscarCodigosAssentosOcupadosPorSessao(Long sessaoId) {
        if (sessaoId == null) {
            return List.of();
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT i.assento.codigo FROM Ingresso i " +
                            "WHERE i.sessao.id = :sessaoId AND i.assento IS NOT NULL",
                    String.class)
                    .setParameter("sessaoId", sessaoId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean assentoOcupadoNaSessao(Long sessaoId, String codigoAssento) {
        if (sessaoId == null || codigoAssento == null) {
            return false;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(i) FROM Ingresso i " +
                            "WHERE i.sessao.id = :sessaoId AND i.assento.codigo = :codigoAssento",
                    Long.class)
                    .setParameter("sessaoId", sessaoId)
                    .setParameter("codigoAssento", codigoAssento)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    public List<Ingresso> buscarIngressosPorSessao(Long sessaoId) {
        if (sessaoId == null) {
            return List.of();
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT i FROM Ingresso i " +
                            "LEFT JOIN FETCH i.cliente " +
                            "LEFT JOIN FETCH i.assento " +
                            "WHERE i.sessao.id = :sessaoId",
                    Ingresso.class)
                    .setParameter("sessaoId", sessaoId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}