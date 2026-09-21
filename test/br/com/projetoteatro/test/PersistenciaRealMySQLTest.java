package br.com.projetoteatro.test;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.enums.*;
import br.com.projetoteatro.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenciaRealMySQLTest {

    @Test
    @DisplayName("PROVA DE PERSISTÊNCIA REAL: Dados devem sobreviver ao fechamento do EntityManager 1 e serem lidos pelo EntityManager 2")
    public void devePersistirEntreContextosDistintosProvandoGravacaoNoMySQL() {
        String sufixo = String.valueOf(System.currentTimeMillis() % 1000000);

        Long usuarioId;
        Long admId;
        Long artistaId;
        Long pecaId;
        Long sessaoId;
        Long propostaId;
        Long contratoId;
        Long ingressoId;

        EntityManager em1 = JPAUtil.getEntityManager();
        em1.getTransaction().begin();

        Usuario usuario = new Usuario("Maria Teste " + sufixo, "maria_" + sufixo + "@mysql.com", "81988887777", "cpf_" + sufixo, "senha123");
        em1.persist(usuario);

        Administrador adm = new Administrador("Adm Real " + sufixo, "adm_" + sufixo + "@mysql.com", "senhaAdm");
        adm.setCpf("adm_cpf_" + sufixo);
        adm.setTelefone("81999990000");
        em1.persist(adm);

        Contratante artista = new Contratante("Grupo Teatral " + sufixo, "artista_" + sufixo + "@mysql.com", "81977776666", "art_cpf_" + sufixo, "senhaArt");
        em1.persist(artista);

        Peca peca = new Peca();
        peca.setNome("A Comédia dos Erros " + sufixo);
        peca.setArtistaResponsavel(artista);
        peca.setDataInicio(LocalDate.now());
        peca.setDataFim(LocalDate.now().plusDays(7));
        peca.setPrecoIngresso(60.0);
        peca.setStatus(StatusProposta.CONTRATADO);
        em1.persist(peca);

        Sessao sessao = new Sessao(peca.getNome(), LocalTime.of(19, 0));
        sessao.setData(LocalDate.now());
        sessao.setHorarioFim(LocalTime.of(21, 30));
        sessao.setTurno(Turno.NOITE);
        sessao.setPeca(peca);
        em1.persist(sessao);

        PropostaAluguel proposta = new PropostaAluguel(
                artista,
                peca.getNome(),
                3500.0,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                LocalTime.of(19, 0),
                LocalTime.of(21, 30),
                60.0
        );
        em1.persist(proposta);

        Contrato contrato = new Contrato(proposta);
        contrato.setStatusContrato(StatusContrato.ATIVO);
        em1.persist(contrato);

        Ingresso ingresso = new Ingresso(
                usuario,
                sessao,
                null,
                TipoSetor.PLATEIA,
                true,
                60.0,
                contrato
        );
        em1.persist(ingresso);

        em1.getTransaction().commit();

        usuarioId = usuario.getId();
        admId = adm.getId();
        artistaId = artista.getId();
        pecaId = peca.getId();
        sessaoId = sessao.getId();
        propostaId = proposta.getId();
        contratoId = contrato.getId();
        ingressoId = ingresso.getId();

        em1.close();
        assertFalse(em1.isOpen(), "O primeiro EntityManager deve ter sido encerrado");

        EntityManager em2 = JPAUtil.getEntityManager();

        Usuario usuarioRecuperado = em2.find(Usuario.class, usuarioId);
        assertNotNull(usuarioRecuperado, "Usuário deve ser encontrado no H2 em novo EntityManager");
        assertEquals("Maria Teste " + sufixo, usuarioRecuperado.getNome());

        Administrador admRecuperado = em2.find(Administrador.class, admId);
        assertNotNull(admRecuperado, "Administrador deve ser encontrado no H2 em novo EntityManager");
        assertEquals("Adm Real " + sufixo, admRecuperado.getNome());

        Contratante artistaRecuperado = em2.find(Contratante.class, artistaId);
        assertNotNull(artistaRecuperado, "Artista/Contratante deve ser encontrado no H2 em novo EntityManager");
        assertEquals("Grupo Teatral " + sufixo, artistaRecuperado.getNome());

        Peca pecaRecuperada = em2.find(Peca.class, pecaId);
        assertNotNull(pecaRecuperada, "Peça deve ser encontrada no H2 em novo EntityManager");
        assertEquals("A Comédia dos Erros " + sufixo, pecaRecuperada.getNome());

        Sessao sessaoRecuperada = em2.find(Sessao.class, sessaoId);
        assertNotNull(sessaoRecuperada, "Sessão deve ser encontrada no H2 em novo EntityManager");
        assertEquals(Turno.NOITE, sessaoRecuperada.getTurno());

        PropostaAluguel propostaRecuperada = em2.find(PropostaAluguel.class, propostaId);
        assertNotNull(propostaRecuperada, "Proposta deve ser encontrada no H2 em novo EntityManager");
        assertEquals(3500.0, propostaRecuperada.getValorAluguel());

        Contrato contratoRecuperado = em2.find(Contrato.class, contratoId);
        assertNotNull(contratoRecuperado, "Contrato deve ser encontrado no H2 em novo EntityManager");
        assertEquals(StatusContrato.ATIVO, contratoRecuperado.getStatusContrato());

        Ingresso ingressoRecuperado = em2.find(Ingresso.class, ingressoId);
        assertNotNull(ingressoRecuperado, "Ingresso deve ser encontrado no H2 em novo EntityManager");
        assertEquals(60.0, ingressoRecuperado.getValor());
        assertEquals(TipoSetor.PLATEIA, ingressoRecuperado.getSetor());

        em2.close();

        EntityManager em3 = JPAUtil.getEntityManager();
        em3.getTransaction().begin();

        Ingresso ingRemover = em3.find(Ingresso.class, ingressoId);
        if (ingRemover != null) em3.remove(ingRemover);

        Contrato contRemover = em3.find(Contrato.class, contratoId);
        if (contRemover != null) em3.remove(contRemover);

        PropostaAluguel propRemover = em3.find(PropostaAluguel.class, propostaId);
        if (propRemover != null) em3.remove(propRemover);

        Sessao sessRemover = em3.find(Sessao.class, sessaoId);
        if (sessRemover != null) em3.remove(sessRemover);

        Peca pecaRemover = em3.find(Peca.class, pecaId);
        if (pecaRemover != null) em3.remove(pecaRemover);

        Contratante artRemover = em3.find(Contratante.class, artistaId);
        if (artRemover != null) em3.remove(artRemover);

        Administrador admRemover = em3.find(Administrador.class, admId);
        if (admRemover != null) em3.remove(admRemover);

        Usuario usrRemover = em3.find(Usuario.class, usuarioId);
        if (usrRemover != null) em3.remove(usrRemover);

        em3.getTransaction().commit();
        em3.close();
    }

    @Test
    @DisplayName("TESTE DE RELACIONAMENTOS: Deve verificar relacionamentos entre contextos (Setor->Assento, Peca->Sessao, Ingresso->Contrato/Sessao/Cliente)")
    public void devePreservarRelacionamentosEntreContextosDistintos() {
        String sufixo = String.valueOf(System.currentTimeMillis() % 1000000);

        Long setorId;
        String codAssento = "A1_" + sufixo;
        Long pecaId;
        Long sessaoId;
        Long contratoId;
        Long ingressoId;
        Long usuarioId;

        EntityManager em1 = JPAUtil.getEntityManager();
        em1.getTransaction().begin();

        Setor setor = new Setor(TipoSetor.PLATEIA, 75.0, 100);
        Assento assento = new Assento(codAssento, StatusAssento.DISPONIVEL, setor);
        setor.adicionarAssento(assento);
        em1.persist(setor);

        Contratante artista = new Contratante("Artista Rel " + sufixo, "art_rel_" + sufixo + "@mysql.com", "81999991111", "cpf_ar_" + sufixo, "123");
        em1.persist(artista);

        Peca peca = new Peca();
        peca.setNome("Auto da Compadecida " + sufixo);
        peca.setArtistaResponsavel(artista);
        peca.setDataInicio(LocalDate.now());
        peca.setDataFim(LocalDate.now().plusDays(5));
        peca.setPrecoIngresso(75.0);
        peca.setStatus(StatusProposta.CONTRATADO);

        Sessao sessao = new Sessao(peca.getNome(), LocalTime.of(20, 0));
        sessao.setData(LocalDate.now());
        sessao.setTurno(Turno.NOITE);
        sessao.setHorarioFim(LocalTime.of(22, 0));
        peca.adicionarSessao(sessao);
        em1.persist(peca);

        Usuario cliente = new Usuario("Cliente Rel " + sufixo, "cli_rel_" + sufixo + "@mysql.com", "81988882222", "cpf_cr_" + sufixo, "123");
        em1.persist(cliente);

        Contrato contrato = new Contrato();
        contrato.setNomePeca(peca.getNome());
        contrato.setValorIngresso(75.0);
        contrato.setStatusContrato(StatusContrato.ATIVO);
        em1.persist(contrato);

        Ingresso ingresso = new Ingresso(
                cliente,
                sessao,
                assento,
                TipoSetor.PLATEIA,
                true,
                75.0,
                contrato
        );
        em1.persist(ingresso);

        em1.getTransaction().commit();

        setorId = setor.getId();
        pecaId = peca.getId();
        sessaoId = sessao.getId();
        contratoId = contrato.getId();
        ingressoId = ingresso.getId();
        usuarioId = cliente.getId();

        em1.close();

        EntityManager em2 = JPAUtil.getEntityManager();

        Setor setorRec = em2.find(Setor.class, setorId);
        assertNotNull(setorRec);
        assertFalse(setorRec.getAssentos().isEmpty(), "Setor deve carregar seus assentos associados");
        assertEquals(codAssento, setorRec.getAssentos().get(0).getCodigo());
        assertEquals(setorId, setorRec.getAssentos().get(0).getSetor().getId());

        Peca pecaRec = em2.find(Peca.class, pecaId);
        assertNotNull(pecaRec);
        assertFalse(pecaRec.getSessoes().isEmpty(), "Peça deve carregar suas sessões associadas");
        assertEquals(sessaoId, pecaRec.getSessoes().get(0).getId());
        assertEquals("Auto da Compadecida " + sufixo, pecaRec.getSessoes().get(0).getPeca().getNome());

        Ingresso ingressoRec = em2.find(Ingresso.class, ingressoId);
        assertNotNull(ingressoRec);
        assertNotNull(ingressoRec.getCliente(), "Ingresso deve manter vínculo com Cliente");
        assertEquals(usuarioId, ingressoRec.getCliente().getId());
        assertNotNull(ingressoRec.getSessao(), "Ingresso deve manter vínculo com Sessão");
        assertEquals(sessaoId, ingressoRec.getSessao().getId());
        assertNotNull(ingressoRec.getContrato(), "Ingresso deve manter vínculo com Contrato");
        assertEquals(contratoId, ingressoRec.getContrato().getId());
        assertNotNull(ingressoRec.getAssento(), "Ingresso deve manter vínculo com Assento");
        assertEquals(codAssento, ingressoRec.getAssento().getCodigo());

        em2.close();

        EntityManager em3 = JPAUtil.getEntityManager();
        em3.getTransaction().begin();

        Ingresso iRem = em3.find(Ingresso.class, ingressoId);
        if (iRem != null) em3.remove(iRem);

        Contrato cRem = em3.find(Contrato.class, contratoId);
        if (cRem != null) em3.remove(cRem);

        Usuario uRem = em3.find(Usuario.class, usuarioId);
        if (uRem != null) em3.remove(uRem);

        Peca pRem = em3.find(Peca.class, pecaId);
        if (pRem != null) em3.remove(pRem);

        Contratante artRem = em3.createQuery("SELECT c FROM Contratante c WHERE c.cpf = :cpf", Contratante.class)
                .setParameter("cpf", "cpf_ar_" + sufixo).getResultStream().findFirst().orElse(null);
        if (artRem != null) em3.remove(artRem);

        Setor sRem = em3.find(Setor.class, setorId);
        if (sRem != null) em3.remove(sRem);

        em3.getTransaction().commit();
        em3.close();
    }

    @Test
    @DisplayName("TRANSAÇÕES E ROLLBACK: Operação abortada com rollback não deve persistir dados no H2")
    public void deveGarantirRollbackTransacionalEmExcecao() {
        String sufixo = String.valueOf(System.currentTimeMillis() % 1000000);
        String cpf = "cpf_rb_" + sufixo;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuario = new Usuario("Teste Rollback", "rb_" + sufixo + "@email.com", "81900000000", cpf, "123");
            em.persist(usuario);

            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        EntityManager emCheck = JPAUtil.getEntityManager();
        try {
            List<Usuario> resultados = emCheck.createQuery("SELECT u FROM Usuario u WHERE u.cpf = :cpf", Usuario.class)
                    .setParameter("cpf", cpf)
                    .getResultList();

            assertTrue(resultados.isEmpty(), "Registro não deve ter sido persistido após rollback");
        } finally {
            emCheck.close();
        }
    }
}
