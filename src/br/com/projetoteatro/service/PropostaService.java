package br.com.projetoteatro.service;

import br.com.projetoteatro.enums.StatusProposta;
import br.com.projetoteatro.exceptions.ConflitoHorarioException;
import br.com.projetoteatro.exceptions.PropostaInvalidaException;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.service.validators.ValidadorHorarios;

import java.util.ArrayList;

public class PropostaService {

    private ArrayList<PropostaAluguel> listaPropostas;
    private ValidadorHorarios validador;
    private RegrasService regrasService; // Mantive a sua variável

    public PropostaService() {
        listaPropostas = new ArrayList<>();
        validador = new ValidadorHorarios();
        regrasService = new RegrasService(); // Mantive a sua inicialização
    }

    // Usando o seu método de cadastro que calcula o valor corretamente
    public void cadastrarProposta(PropostaAluguel p) throws ConflitoHorarioException {
        validador.duracaoPecaPorPeriodo(p.getHorarioInicio(), p.getHorarioFim());
        validador.validarConflitoHorario(p.getDataInicio(), p.getDataFim(), p.getHorarioInicio(), p.getHorarioFim(), listaPropostas);

        Double valorCalculadoAluguel = regrasService.calcularAluguel(p);
        p.setValorAluguel(valorCalculadoAluguel);

        listaPropostas.add(p);
    }

    // ... (Mantenha todos os outros métodos que estavam no código dela) ...
    public PropostaAluguel buscarProposta(long id) throws PropostaInvalidaException {
        for(PropostaAluguel p: listaPropostas){
            if(p.getId()==id){
                return p;
            }
        }
        throw new PropostaInvalidaException("Proposta não encontrada....");
    }

    public void geradorPropostaPDF(long id) throws PropostaInvalidaException {
        PropostaAluguel proposta = buscarProposta(id);
        //PdfService.gerarProposta(proposta);
    }

    public boolean enviarPropostaPorEmail(long id) throws PropostaInvalidaException {
        PropostaAluguel proposta = buscarProposta(id);
        String arquivo = "Proposta_" + proposta.getId() + ".pdf";
        geradorPropostaPDF(id);
        return EmailService.enviarEmail(proposta.getContratante().getEmail(),"Proposta Teatro","Segue em anexo a proposta do teatro.",arquivo,arquivo);
    }

    public ArrayList<PropostaAluguel> getListaPropostas() {
        return listaPropostas;
    }

    public void contratarProposta(long id) throws PropostaInvalidaException {
        PropostaAluguel proposta = buscarProposta(id);
        proposta.setStatusProposta(StatusProposta.CONTRATADO);
    }

    public void estenderProposta(long id, int dias) throws PropostaInvalidaException {
        PropostaAluguel proposta = buscarProposta(id);
        if(proposta.getStatusProposta() != StatusProposta.CONTRATADO){
            throw new PropostaInvalidaException("Proposta não contratada, portanto não pode ser extendida!");
        }
        proposta.setDataFim(proposta.getDataFim().plusDays(dias));
    }

    public void encerrarProposta(long id) throws PropostaInvalidaException {
        PropostaAluguel proposta = buscarProposta(id);
        if(proposta.getStatusProposta() == StatusProposta.ENCERRADO){
            throw new PropostaInvalidaException("Essa proposta já foi encerrado.....");
        } else {
            proposta.setStatusProposta(StatusProposta.ENCERRADO);
        }
    }

    public ArrayList<PropostaAluguel> filtrarPropostaPorStatus(StatusProposta s) {
        ArrayList<PropostaAluguel> listagemResultado = new ArrayList<PropostaAluguel>();
        for(PropostaAluguel x : listaPropostas){
            if(x.getStatusProposta() == s){
                listagemResultado.add(x);
            }
        }
        return listagemResultado;
    }

    public ArrayList<PropostaAluguel> filtrarPropostaPorContratante(String n) {
        ArrayList<PropostaAluguel> listagemResultado = new ArrayList<PropostaAluguel>();
        for(PropostaAluguel x : listaPropostas){
            if(x.getContratante().getNome().toLowerCase().contains(n.toLowerCase())){
                listagemResultado.add(x);
            }
        }
        return listagemResultado;
    }

    public ArrayList<PropostaAluguel> filtrarPropostaPorNomePeca(String n) {
        ArrayList<PropostaAluguel> listagemResultado = new ArrayList<PropostaAluguel>();
        for(PropostaAluguel x : listaPropostas){
            if(x.getNomePeca().toLowerCase().contains(n.toLowerCase())){
                listagemResultado.add(x);
            }
        }
        return listagemResultado;
    }
}
