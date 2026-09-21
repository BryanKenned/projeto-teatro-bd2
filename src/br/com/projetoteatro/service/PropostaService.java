package br.com.projetoteatro.service;

import br.com.projetoteatro.enums.StatusContrato;
import br.com.projetoteatro.enums.StatusProposta;
import br.com.projetoteatro.exceptions.ConflitoHorarioException;
import br.com.projetoteatro.exceptions.PropostaInvalidaException;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.repository.PropostasRepository;
import br.com.projetoteatro.service.validators.ValidadorHorarios;

import java.util.ArrayList;
import java.util.List;

public class PropostaService {

    private ValidadorHorarios validador;
    private RegrasService regrasService;
    private PropostasRepository propostaRepo;
    private br.com.projetoteatro.repository.ArtistaRepository artistaRepo;
    private br.com.projetoteatro.repository.ContratoRepository contratoRepo;

    public PropostaService(
            RegrasService regrasService,
            PropostasRepository propostaRepo,
            br.com.projetoteatro.repository.ArtistaRepository artistaRepo) {

        this(regrasService, propostaRepo, artistaRepo, new br.com.projetoteatro.repository.ContratoRepository());
    }

    public PropostaService(
            RegrasService regrasService,
            PropostasRepository propostaRepo,
            br.com.projetoteatro.repository.ArtistaRepository artistaRepo,
            br.com.projetoteatro.repository.ContratoRepository contratoRepo) {

        this.validador = new ValidadorHorarios();
        this.regrasService = regrasService;
        this.propostaRepo = propostaRepo;
        this.artistaRepo = artistaRepo;
        this.contratoRepo = contratoRepo != null ? contratoRepo
                : new br.com.projetoteatro.repository.ContratoRepository();
    }

    public void cadastrarProposta(PropostaAluguel p)
            throws ConflitoHorarioException {

        validador.duracaoPecaPorPeriodo(
                p.getHorarioInicio(),
                p.getHorarioFim());

        validador.validarConflitoHorario(
                p.getDataInicio(),
                p.getDataFim(),
                p.getHorarioInicio(),
                p.getHorarioFim(),
                new ArrayList<>(propostaRepo.listarPropostas()));

        Double valorCalculadoAluguel = regrasService.calcularAluguel(p);

        p.setValorAluguel(valorCalculadoAluguel);

        if (p.getContratante() != null) {
            br.com.projetoteatro.model.Contratante existente = null;
            if (p.getContratante().getCpf() != null && !p.getContratante().getCpf().isBlank()) {
                existente = artistaRepo.buscarContratante(p.getContratante().getCpf());
            }
            if (existente == null && p.getContratante().getEmail() != null
                    && !p.getContratante().getEmail().isBlank()) {
                existente = artistaRepo.buscarContratanteEmail(p.getContratante().getEmail());
            }
            if (existente != null) {
                p.setContratante(existente);
            }
        }

        propostaRepo.adicionarProposta(p);
    }

    public PropostaAluguel buscarProposta(long id)
            throws PropostaInvalidaException {

        PropostaAluguel proposta = propostaRepo.buscarProposta(id);

        if (proposta == null) {
            throw new PropostaInvalidaException(
                    "Proposta não encontrada....");
        }

        return proposta;
    }

    public String geradorPropostaPDF(long id)
            throws PropostaInvalidaException {

        PropostaAluguel proposta = buscarProposta(id);

        return PdfService.gerarProposta(proposta);
    }

    public boolean enviarPropostaPorEmail(long id)
            throws PropostaInvalidaException {

        PropostaAluguel proposta = buscarProposta(id);

        String nomeArquivo = geradorPropostaPDF(id);

        return EmailService.enviarEmail(
                proposta.getContratante().getEmail(),
                "Proposta Teatro",
                "Segue em anexo a proposta do teatro.",
                nomeArquivo,
                nomeArquivo);
    }

    public ArrayList<PropostaAluguel> getListaPropostas() {
        return new ArrayList<>(propostaRepo.listarPropostas());
    }

    public void contratarProposta(long id)
            throws PropostaInvalidaException {

        PropostaAluguel proposta = buscarProposta(id);

        proposta.setStatusProposta(StatusProposta.CONTRATADO);

        propostaRepo.atualizar(proposta);
    }

    public void estenderProposta(long id, int dias)
            throws PropostaInvalidaException {

        PropostaAluguel proposta = buscarProposta(id);

        if (proposta.getStatusProposta() != StatusProposta.CONTRATADO) {

            throw new PropostaInvalidaException(
                    "Proposta não contratada, portanto não pode ser extendida!");
        }

        proposta.setDataFim(
                proposta.getDataFim().plusDays(dias));

        propostaRepo.atualizar(proposta);
    }

    public void encerrarProposta(long id)
            throws PropostaInvalidaException {

        PropostaAluguel proposta = buscarProposta(id);

        if (proposta.getStatusProposta() == StatusProposta.ENCERRADO) {

            throw new PropostaInvalidaException(
                    "Essa proposta já foi encerrado.....");

        } else {

            proposta.setStatusProposta(
                    StatusProposta.ENCERRADO);

            propostaRepo.atualizar(proposta);
        }
    }

    public ArrayList<PropostaAluguel> filtrarPropostaPorStatus(
            StatusProposta s) {

        ArrayList<PropostaAluguel> resultado = new ArrayList<>();

        for (PropostaAluguel proposta : propostaRepo.listarPropostas()) {

            if (proposta.getStatusProposta() == s) {
                resultado.add(proposta);
            }
        }

        return resultado;
    }

    public ArrayList<PropostaAluguel> filtrarPropostaPorContratante(
            String n) {

        ArrayList<PropostaAluguel> resultado = new ArrayList<>();

        for (PropostaAluguel proposta : propostaRepo.listarPropostas()) {

            if (proposta.getContratante()
                    .getNome()
                    .toLowerCase()
                    .contains(n.toLowerCase())) {

                resultado.add(proposta);
            }
        }

        return resultado;
    }

    public ArrayList<PropostaAluguel> filtrarPropostaPorNomePeca(
            String n) {

        ArrayList<PropostaAluguel> resultado = new ArrayList<>();

        for (PropostaAluguel proposta : propostaRepo.listarPropostas()) {

            if (proposta.getNomePeca()
                    .toLowerCase()
                    .contains(n.toLowerCase())) {

                resultado.add(proposta);
            }
        }

        return resultado;
    }

    public br.com.projetoteatro.model.Contrato efetivarContrato(PropostaAluguel p)
            throws Exception {

        if (p == null || p.getId() == null) {
            throw new IllegalArgumentException("Proposta inválida para efetivação.");
        }

        br.com.projetoteatro.model.Contrato contratoExistente = contratoRepo.buscarPorProposta(p.getId());
        if (contratoExistente != null) {
            contratoExistente.setStatusContrato(StatusContrato.ATIVO);
            contratoRepo.atualizar(contratoExistente);
            p.setStatusProposta(StatusProposta.CONTRATADO);
            p.setStatusContrato(StatusContrato.ATIVO);
            propostaRepo.atualizar(p);
            return contratoExistente;
        }

        br.com.projetoteatro.model.Contrato novoContrato = new br.com.projetoteatro.model.Contrato(p);
        novoContrato.setStatusContrato(StatusContrato.ATIVO);

        contratoRepo.adicionarContrato(novoContrato);

        p.setStatusProposta(StatusProposta.CONTRATADO);
        p.setStatusContrato(StatusContrato.ATIVO);
        propostaRepo.atualizar(p);

        try {
            enviarPropostaPorEmail(p.getId());
        } catch (Exception e) {
            System.err.println(
                    "Aviso: Proposta efetivada no banco, mas o e-mail não pôde ser enviado: " + e.getMessage());
        }

        return novoContrato;
    }

    public void atualizarProposta(PropostaAluguel p) throws Exception {
        if (p == null || p.getId() == null) {
            throw new IllegalArgumentException("Proposta não pode ser nula para atualização.");
        }

        PropostaAluguel existente = propostaRepo.buscarProposta(p.getId());
        if (existente == null) {
            throw new PropostaInvalidaException("Proposta não encontrada no banco de dados.");
        }

        if (existente.getStatusProposta() == StatusProposta.ENCERRADO) {
            throw new IllegalStateException("Propostas encerradas não podem ser alteradas.");
        }

        validador.duracaoPecaPorPeriodo(p.getHorarioInicio(), p.getHorarioFim());

        List<PropostaAluguel> outrasPropostas = new ArrayList<>();
        for (PropostaAluguel prop : propostaRepo.listarPropostas()) {
            if (!prop.getId().equals(p.getId())) {
                outrasPropostas.add(prop);
            }
        }

        validador.validarConflitoHorario(
                p.getDataInicio(),
                p.getDataFim(),
                p.getHorarioInicio(),
                p.getHorarioFim(),
                new ArrayList<>(outrasPropostas));

        Double valorCalculado = regrasService.calcularAluguel(p);
        p.setValorAluguel(valorCalculado);

        propostaRepo.atualizar(p);

        br.com.projetoteatro.model.Contrato contrato = contratoRepo.buscarPorProposta(p.getId());
        if (contrato != null) {
            contrato.setNomePeca(p.getNomePeca());
            contrato.setDataInicio(p.getDataInicio());
            contrato.setDataFim(p.getDataFim());
            contrato.setHorarioInicio(p.getHorarioInicio());
            contrato.setHorarioFim(p.getHorarioFim());
            contrato.setValorAluguel(p.getValorAluguel());
            contrato.setValorIngresso(p.getValorIngresso());
            contratoRepo.atualizar(contrato);
        }
    }
}