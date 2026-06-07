package br.com.projetoteatro.service.validators;

import br.com.projetoteatro.enums.StatusProposta;
import br.com.projetoteatro.exceptions.*;
import br.com.projetoteatro.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*
public class ServicoTeatro {
    private ClienteService clienteService;
    private ArtistaService artistaService;
    private AdministradorService administradorService;
    private LoginService loginService;
    private PropostaService propostaService;
    private RegrasService regrasService;
    private ArrayList<Peca> listaPecas;
     private ArrayList<Sessao> listaSessoes;
    private ArrayList<Ingresso> listaIngressos;

    public ServicoTeatro(){

        clienteService = new ClienteService();
        artistaService = new ArtistaService();
        administradorService = new AdministradorService();
        regrasService=new RegrasService();
        propostaService= new PropostaService();

        loginService = new LoginService(administradorService,clienteService,artistaService);
    }

    public ClienteService getClienteService() {
        return clienteService;
    }

    public ArtistaService getArtistaService() {
        return artistaService;
    }

    //excluir
    public boolean excluirRegra(long id) throws RegraInvalidaException {
        RegraAluguel regra=buscarRegra(id);
        return listaRegras.remove(regra);
    }
    //cadastrar proposta
    public void cadastrarProposta(PropostaAluguel p)throws ConflitoHorarioException{
        validador.validarConflitoHorario(p,listaPropostas);
        listaPropostas.add(p);
    }
    //gerar proposta pdf
    public void geradorProposta(long id){
        PropostaAluguel proposta=buscarProposta(id);
        //GeradorDePDF.gerarContrato(proposta);
    }
    //buscar proposta por id
    public PropostaAluguel buscarProposta(long id) throws PropostaInvalidaException {
        for(PropostaAluguel p: listaPropostas){
            if(p.getId()==id){
                return p;
            }
        }
        throw new PropostaInvalidaException("Proposta não encontrada....");

    }
    //lista Proposta
    public ArrayList<PropostaAluguel> getListaPropostas() {
        return listaPropostas;
    }
    public void contratarProposta(long id) throws PropostaInvalidaException{
        PropostaAluguel proposta=buscarProposta(id);
        //proposta.setStatusProposta(StatusProposta.CONTRATADO);
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public PropostaService getPropostaService() {
        return propostaService;
    }

    public RegrasService getRegrasService() {
        return regrasService;
    }

    public AdministradorService getAdministradorService() {
    }
}
*/