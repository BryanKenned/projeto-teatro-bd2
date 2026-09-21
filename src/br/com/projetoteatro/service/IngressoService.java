package br.com.projetoteatro.service;

import br.com.projetoteatro.enums.TipoSetor;
import br.com.projetoteatro.exceptions.EmailInvalidoException;
import br.com.projetoteatro.exceptions.IngressoInvalidoException;
import br.com.projetoteatro.model.*;
import br.com.projetoteatro.repository.IngressoRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IngressoService {

    private IngressoRepository listaIngressos;

    public IngressoRepository getListaIngressos() {
        return listaIngressos;
    }

    public void setListaIngressos(IngressoRepository listaIngressos) {
        this.listaIngressos = listaIngressos;
    }

    public IngressoService(IngressoRepository repo) {
        this.listaIngressos = repo;
    }

    public Ingresso buscarIngresso(long id)
            throws IngressoInvalidoException, EmailInvalidoException {

        Ingresso ingresso = listaIngressos.buscarIngresso(id);

        if (ingresso == null) {
            throw new IngressoInvalidoException(
                    "Ingresso não encontrado....");
        }

        return ingresso;
    }

    public void cadastrarIngresso(Ingresso ingresso) {
        listaIngressos.adicionarIngresso(ingresso);
    }

    public Ingresso venderIngresso(
            Usuario cliente,
            Sessao sessao,
            Assento assento,
            Setor setor,
            Contrato contrato)
            throws IngressoInvalidoException, EmailInvalidoException {

        return venderIngresso(
                cliente,
                sessao,
                assento,
                setor != null ? setor.getSetor() : null,
                contrato);
    }

    public Ingresso venderIngresso(
            Usuario cliente,
            Sessao sessao,
            Assento assento,
            TipoSetor setor,
            Contrato contrato)
            throws IngressoInvalidoException, EmailInvalidoException {

        if (cliente == null) {
            throw new IngressoInvalidoException("Cliente comprador não informado.");
        }
        if (sessao == null) {
            throw new IngressoInvalidoException("Sessão não informada.");
        }
        if (assento == null || assento.getCodigo() == null) {
            throw new IngressoInvalidoException("Assento obrigatório não informado.");
        }
        if (contrato == null) {
            throw new IngressoInvalidoException("Contrato/Espetáculo não informado.");
        }

        if (listaIngressos.assentoOcupadoNaSessao(sessao.getId(), assento.getCodigo())) {
            throw new IllegalStateException("O assento " + assento.getCodigo() + " já está ocupado para esta sessão!");
        }

        double valor = contrato.getValorIngresso() > 0 ? contrato.getValorIngresso() : 50.0;
        if (setor == null && assento.getSetor() != null) {
            setor = assento.getSetor().getSetor();
        }
        if (setor == null) {
            setor = TipoSetor.PLATEIA;
        }

        Ingresso ingresso = new Ingresso(
                cliente,
                sessao,
                assento,
                setor,
                true,
                valor,
                contrato);

        listaIngressos.adicionarIngresso(ingresso);

        try {
            enviarIngressoPorEmail(ingresso.getId());
        } catch (Exception e) {
            System.err.println("Aviso: Ingresso emitido, mas envio de e-mail falhou: " + e.getMessage());
        }

        return ingresso;
    }

    public List<Ingresso> venderIngressosComAssentos(
            Usuario cliente,
            Sessao sessao,
            List<Assento> assentos,
            TipoSetor setor,
            Contrato contrato)
            throws IngressoInvalidoException, EmailInvalidoException {

        if (assentos == null || assentos.isEmpty()) {
            throw new IngressoInvalidoException("Nenhum assento foi selecionado para a venda.");
        }

        for (Assento a : assentos) {
            if (listaIngressos.assentoOcupadoNaSessao(sessao.getId(), a.getCodigo())) {
                throw new IllegalStateException("O assento " + a.getCodigo() + " já está ocupado para esta sessão!");
            }
        }

        List<Ingresso> ingressosEmitidos = new ArrayList<>();
        for (Assento a : assentos) {
            Ingresso ing = venderIngresso(cliente, sessao, a, setor, contrato);
            ingressosEmitidos.add(ing);
        }

        return ingressosEmitidos;
    }

    public List<String> buscarAssentosOcupadosNaSessao(Long sessaoId) {
        return listaIngressos.buscarCodigosAssentosOcupadosPorSessao(sessaoId);
    }

    public String geradorIngressoPDF(long id)
            throws IngressoInvalidoException, EmailInvalidoException {

        Ingresso ingresso = buscarIngresso(id);

        String pdf = PdfService.gerarIngresso(ingresso);

        return pdf;
    }

    public boolean enviarIngressoPorEmail(long id)
            throws IngressoInvalidoException, EmailInvalidoException {

        Ingresso ingresso = buscarIngresso(id);

        String arquivo = "ingresso_" + ingresso.getId() + ".pdf";

        geradorIngressoPDF(id);

        return EmailService.enviarEmail(
                ingresso.getCliente().getEmail(),
                "Ingresso",
                "Segue em anexo o ingresso do espetáculo ."
                        + ingresso.getContrato().getNomePeca()
                        + " em pdf.",
                arquivo,
                arquivo);
    }

    public ArrayList<Ingresso> listarIngressosContrato(long idContrato) {
        return new ArrayList<>(listaIngressos.buscarPorContrato(idContrato));
    }

    public ArrayList<Ingresso> listarIngressosPorDia(LocalDate data) {
        return new ArrayList<>(listaIngressos.buscarPorData(data));
    }

    public void venderIngressos(
            Usuario cliente,
            Sessao sessao,
            TipoSetor setor,
            Contrato contrato,
            int quantidade)
            throws IngressoInvalidoException, EmailInvalidoException {

        for (int i = 0; i < quantidade; i++) {

            Ingresso ingresso = new Ingresso(
                    cliente,
                    sessao,
                    null,
                    setor,
                    true,
                    contrato.getValorIngresso(),
                    contrato);

            listaIngressos.adicionarIngresso(ingresso);

            enviarIngressoPorEmail(ingresso.getId());
        }
    }

    public double calcularFaturamentoMesAtual() {
        return listaIngressos.calcularFaturamentoMesAtual();
    }

    public long contarIngressosVendidosHoje() {
        return listaIngressos.contarIngressosVendidosHoje();
    }

    public IngressoRepository getIngressoRepo() {
        return listaIngressos;
    }
}