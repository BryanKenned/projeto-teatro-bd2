package br.com.projetoteatro.service.validators;

import br.com.projetoteatro.repository.*;
import br.com.projetoteatro.service.*;

public class ServicoTeatro {

    private final AdministradorService administradorService;
    private final ClienteService clienteService;
    private final ArtistaService artistaService;
    private final LoginService loginService;
    private final PropostaService propostaService;
    private final RegrasService regrasService;
    private final ContratoService contratoService;
    private final IngressoService ingressoService;
    private final UsuarioService usuarioService;
    private final SessaoService sessaoService;
    private final PecaService pecaService;

    public ServicoTeatro() {

        AdministradorRepository admRepository = new AdministradorRepository();
        ClienteRepository clienteRepository = new ClienteRepository();
        ArtistaRepository artistaRepository = new ArtistaRepository();
        PropostasRepository propostasRepository = new PropostasRepository();
        RegrasPrecoRepository regrasRepository = new RegrasPrecoRepository();
        ContratoRepository contratoRepository = new ContratoRepository();
        IngressoRepository ingressoRepository = new IngressoRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        SessaoRepository sessaoRepository = new SessaoRepository();
        PecaRepository pecaRepository = new PecaRepository();

        this.administradorService = new AdministradorService(admRepository);
        this.clienteService = new ClienteService(clienteRepository);
        this.artistaService = new ArtistaService(artistaRepository);

        this.loginService = new LoginService(
                admRepository,
                clienteRepository,
                artistaRepository);

        this.regrasService = new RegrasService(regrasRepository);

        this.propostaService = new PropostaService(
                regrasService,
                propostasRepository,
                artistaRepository,
                contratoRepository);

        this.contratoService = new ContratoService(contratoRepository);
        this.ingressoService = new IngressoService(ingressoRepository);
        this.usuarioService = new UsuarioService(usuarioRepository);
        this.sessaoService = new SessaoService(sessaoRepository);
        this.pecaService = new PecaService(pecaRepository);
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
        return administradorService;
    }

    public ClienteService getClienteService() {
        return clienteService;
    }

    public ArtistaService getArtistaService() {
        return artistaService;
    }

    public ContratoService getContratoService() {
        return contratoService;
    }

    public IngressoService getIngressoService() {
        return ingressoService;
    }

    public UsuarioService getUsuarioService() {
        return usuarioService;
    }

    public SessaoService getSessaoService() {
        return sessaoService;
    }

    public PecaService getPecaService() {
        return pecaService;
    }
}