package br.com.projetoteatro.service;

import br.com.projetoteatro.enums.StatusContrato;
import br.com.projetoteatro.exceptions.ContratanteInvalidoException;
import br.com.projetoteatro.model.Contrato;
import br.com.projetoteatro.repository.ContratoRepository;

import java.time.LocalDate;
import java.util.List;

public class ContratoService {

    private EnviarEmailService emailService;
    private ContratoRepository listaContratos;

    public ContratoService(ContratoRepository listaContratos) {
        this(listaContratos, new EnviarEmailService());
    }

    public ContratoService(ContratoRepository listaContratos, EnviarEmailService emailService) {
        this.listaContratos = listaContratos;
        this.emailService = emailService != null ? emailService : new EnviarEmailService();
    }

    public void setEmailService(EnviarEmailService emailService) {
        this.emailService = emailService;
    }

    public void cadastrarContrato(Contrato c) {
        listaContratos.adicionarContrato(c);
    }

    public Contrato buscarContrato(long id) throws Exception {

        Contrato contrato = listaContratos.buscarContrato(id);

        if (contrato == null) {
            throw new Exception("Contrato não encontrado....");
        }

        return contrato;
    }

    public List<Contrato> listarContratos() {
        return listaContratos.listarContrato();
    }

    public List<Contrato> listarContratosAtivos() {
        return listaContratos.listarContratosAtivos();
    }

    public List<Contrato> buscarPorNomePeca(String nomePeca) {
        return listaContratos.buscarPorNomePeca(nomePeca);
    }

    public void geradorContratoPDF(long id) throws Exception {

        Contrato contrato = buscarContrato(id);

        PdfService.gerarContrato(contrato);
    }

    public void enviarContratoPorEmail(long id) throws Exception {

        Contrato contrato = buscarContrato(id);

        String pdf = PdfService.gerarContrato(contrato);

        if (emailService == null) {
            throw new IllegalStateException(
                    "Serviço de e-mail não foi configurado."
            );
        }

        emailService.enviarArquivoPdf(
                contrato.getEmail(),
                "Contrato de aluguel",
                "Segue contrato em anexo.",
                pdf
        );
    }

    public void estenderContrato(
            long id,
            LocalDate novaDataFim) throws Exception {

        Contrato contrato = buscarContrato(id);

        contrato.estender(novaDataFim);

        listaContratos.atualizar(contrato);
    }

    public void encerrarContrato(long id)
            throws ContratanteInvalidoException {

        Contrato contrato = listaContratos.buscarContrato(id);

        if (contrato == null) {
            throw new ContratanteInvalidoException(
                    "Contrato não encontrado...."
            );
        }

        contrato.setStatusContrato(StatusContrato.INATIVO);

        listaContratos.atualizar(contrato);
    }

    public ContratoRepository getContratoRepository() {
        return listaContratos;
    }
}