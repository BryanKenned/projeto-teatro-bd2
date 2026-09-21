package br.com.projetoteatro.view;

import br.com.projetoteatro.enums.TipoSetor;
import br.com.projetoteatro.model.Assento;
import br.com.projetoteatro.model.Contrato;
import br.com.projetoteatro.model.Ingresso;
import br.com.projetoteatro.model.Sessao;
import br.com.projetoteatro.model.Usuario;
import br.com.projetoteatro.repository.AssentoRepository;
import br.com.projetoteatro.repository.ClienteRepository;
import br.com.projetoteatro.service.ClienteService;
import br.com.projetoteatro.service.ContratoService;
import br.com.projetoteatro.service.IngressoService;
import br.com.projetoteatro.service.SessaoService;
import br.com.projetoteatro.service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BilheteriaView extends JPanel {

    private final ContratoService contratoService;
    private final IngressoService ingressoService;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final SessaoService sessaoService;
    private final AssentoRepository assentoRepo;

    private Contrato contratoSelecionado;
    private Usuario usuarioEncontrado;
    private final List<Assento> assentosSelecionados = new ArrayList<>();

    private JTextField txtCpf;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JTextField txtQuantidade;
    private JLabel lblAssentosEscolhidos;

    private JButton btnBuscar;
    private JButton btnSelecionarAssentos;
    private JButton btnConfirmarVenda;

    private JComboBox<Contrato> cbxEspetaculo;
    private JComboBox<Sessao> cbxSessao;
    private JComboBox<TipoSetor> cbxSetor;

    public BilheteriaView(
            ContratoService contratoService,
            IngressoService ingressoService,
            UsuarioService usuarioService,
            SessaoService sessaoService) {

        this(contratoService, ingressoService, usuarioService, new ClienteService(new ClienteRepository()),
                sessaoService, new AssentoRepository());
    }

    public BilheteriaView(
            ContratoService contratoService,
            IngressoService ingressoService,
            UsuarioService usuarioService,
            SessaoService sessaoService,
            AssentoRepository assentoRepo) {

        this(contratoService, ingressoService, usuarioService, new ClienteService(new ClienteRepository()),
                sessaoService, assentoRepo);
    }

    public BilheteriaView(
            ContratoService contratoService,
            IngressoService ingressoService,
            UsuarioService usuarioService,
            ClienteService clienteService,
            SessaoService sessaoService,
            AssentoRepository assentoRepo) {

        this.contratoService = contratoService;
        this.ingressoService = ingressoService;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService != null ? clienteService : new ClienteService(new ClienteRepository());
        this.sessaoService = sessaoService;
        this.assentoRepo = assentoRepo != null ? assentoRepo : new AssentoRepository();

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inicializarComponentes();
        montarLayout();
        carregarContratos();
    }

    private void inicializarComponentes() {
        txtCpf = new JTextField();
        txtNome = new JTextField();
        txtEmail = new JTextField();
        txtTelefone = new JTextField();
        txtQuantidade = new JTextField("0");
        txtQuantidade.setEditable(false);

        txtNome.setEditable(false);
        txtEmail.setEditable(false);
        txtTelefone.setEditable(false);

        lblAssentosEscolhidos = new JLabel("Nenhum assento selecionado");
        lblAssentosEscolhidos.setFont(new Font("Arial", Font.BOLD, 12));
        lblAssentosEscolhidos.setForeground(new Color(41, 128, 185));

        btnBuscar = new JButton("Buscar cliente");
        btnConfirmarVenda = new JButton("Confirmar venda");
        btnConfirmarVenda.setBackground(new Color(46, 204, 113));
        btnConfirmarVenda.setForeground(Color.WHITE);
        btnConfirmarVenda.setFont(new Font("Arial", Font.BOLD, 14));

        btnSelecionarAssentos = new JButton("Escolher Assento(s) no Mapa");
        btnSelecionarAssentos.setBackground(new Color(52, 152, 219));
        btnSelecionarAssentos.setForeground(Color.WHITE);
        btnSelecionarAssentos.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cbxEspetaculo = new JComboBox<>();
        cbxSessao = new JComboBox<>();
        cbxSessao.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Sessao s) {
                    setText(String.format("%s - %s às %s (%s)",
                            s.getNomePeca(),
                            s.getData() != null ? s.getData().toString() : "Data n/d",
                            s.getHorarioInicio() != null ? s.getHorarioInicio().toString() : "",
                            s.getTurno() != null ? s.getTurno().toString() : ""));
                }
                return this;
            }
        });

        cbxSetor = new JComboBox<>(TipoSetor.values());

        btnBuscar.addActionListener(e -> buscarCliente());
        btnConfirmarVenda.addActionListener(e -> confirmarVenda());
        cbxEspetaculo.addActionListener(e -> atualizarContratoSelecionado());
        cbxSessao.addActionListener(e -> {
            assentosSelecionados.clear();
            if (lblAssentosEscolhidos != null) {
                lblAssentosEscolhidos.setText("Nenhum assento selecionado");
            }
            if (txtQuantidade != null) {
                txtQuantidade.setText("0");
            }
        });
        cbxSetor.addActionListener(e -> {
            assentosSelecionados.clear();
            if (lblAssentosEscolhidos != null) {
                lblAssentosEscolhidos.setText("Nenhum assento selecionado");
            }
            if (txtQuantidade != null) {
                txtQuantidade.setText("0");
            }
        });
        btnSelecionarAssentos.addActionListener(e -> abrirModalMapaAssentos());
    }

    private void montarLayout() {
        JPanel painelCliente = new JPanel(new GridLayout(5, 2, 10, 10));
        painelCliente.setBorder(BorderFactory.createTitledBorder("Dados do Comprador (Cliente)"));
        painelCliente.setBackground(Color.WHITE);

        painelCliente.add(new JLabel("CPF:"));
        painelCliente.add(txtCpf);

        painelCliente.add(new JLabel("Nome:"));
        painelCliente.add(txtNome);

        painelCliente.add(new JLabel("E-mail:"));
        painelCliente.add(txtEmail);

        painelCliente.add(new JLabel("Telefone:"));
        painelCliente.add(txtTelefone);

        painelCliente.add(new JLabel(""));
        painelCliente.add(btnBuscar);

        JPanel painelVenda = new JPanel(new GridLayout(6, 2, 10, 10));
        painelVenda.setBorder(BorderFactory.createTitledBorder("Dados da Apresentação e Poltronas"));
        painelVenda.setBackground(Color.WHITE);

        painelVenda.add(new JLabel("Espetáculo / Contrato:"));
        painelVenda.add(cbxEspetaculo);

        painelVenda.add(new JLabel("Sessão Disponível:"));
        painelVenda.add(cbxSessao);

        painelVenda.add(new JLabel("Setor Físico:"));
        painelVenda.add(cbxSetor);

        painelVenda.add(new JLabel("Mapa de Poltronas:"));
        painelVenda.add(btnSelecionarAssentos);

        painelVenda.add(new JLabel("Assentos Selecionados:"));
        painelVenda.add(lblAssentosEscolhidos);

        painelVenda.add(new JLabel("Quantidade Total:"));
        painelVenda.add(txtQuantidade);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBackground(Color.WHITE);
        painelPrincipal.add(painelCliente, BorderLayout.NORTH);
        painelPrincipal.add(painelVenda, BorderLayout.CENTER);

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelBotao.setBackground(Color.WHITE);
        painelBotao.add(btnConfirmarVenda);

        add(painelPrincipal, BorderLayout.CENTER);
        add(painelBotao, BorderLayout.SOUTH);
    }

    private void abrirModalMapaAssentos() {
        Sessao sessao = (Sessao) cbxSessao.getSelectedItem();
        TipoSetor setor = (TipoSetor) cbxSetor.getSelectedItem();

        if (sessao == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma sessão antes de escolher os assentos no mapa!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Seleção de Assentos - " + sessao.getNomePeca(), true);
        dialog.setSize(850, 620);
        dialog.setLocationRelativeTo(parent);

        MapaAssentosView mapa = new MapaAssentosView(sessaoService, assentoRepo, ingressoService);
        mapa.selecionarSessaoESetor(sessao, setor);

        mapa.setCallbackSelecao(assentos -> {
            assentosSelecionados.clear();
            assentosSelecionados.addAll(assentos);
            txtQuantidade.setText(String.valueOf(assentos.size()));

            List<String> codigos = assentos.stream().map(Assento::getCodigo).toList();
            System.out.println("[INFO] Assentos selecionados: " + codigos);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < assentos.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append(assentos.get(i).getCodigo());
            }
            lblAssentosEscolhidos.setText(sb.toString());
            dialog.dispose();
        });

        dialog.setContentPane(mapa);
        dialog.setVisible(true);
    }

    private void buscarCliente() {
        String cpf = txtCpf.getText().trim();
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o CPF do cliente.");
            return;
        }

        try {
            Usuario usuario = clienteService != null
                    ? clienteService.buscarPorCpf(cpf)
                    : usuarioService.buscarPorCpf(cpf);

            if (usuario == null) {
                System.out.println("[WARN] Cliente não encontrado para o CPF: " + cpf);
                int resposta = JOptionPane.showConfirmDialog(
                        this,
                        "Cliente não encontrado. Deseja cadastrar?",
                        "Cliente Não Encontrado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (resposta == JOptionPane.YES_OPTION) {
                    Window parent = SwingUtilities.getWindowAncestor(this);
                    Usuario novo = ClienteView.abrirDialogCadastro(parent, clienteService, cpf);
                    if (novo != null) {
                        usuarioEncontrado = novo;
                        txtCpf.setText(novo.getCpf());
                        txtNome.setText(novo.getNome());
                        txtEmail.setText(novo.getEmail());
                        txtTelefone.setText(novo.getTelefone() != null ? novo.getTelefone() : "");
                        System.out.println("[INFO] Cliente cadastrado e vinculado à bilheteria: " + novo.getNome()
                                + " (" + novo.getCpf() + ")");
                    }
                } else {
                    usuarioEncontrado = null;
                    limparDadosCliente();
                }
                return;
            }

            usuarioEncontrado = usuario;
            txtNome.setText(usuario.getNome());
            txtEmail.setText(usuario.getEmail());
            txtTelefone.setText(usuario.getTelefone() != null ? usuario.getTelefone() : "");
            System.out.println(
                    "[INFO] Cliente encontrado pelo CPF: " + usuario.getNome() + " (" + usuario.getCpf() + ")");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar cliente: " + e.getMessage());
        }
    }

    private void carregarContratos() {
        cbxEspetaculo.removeAllItems();
        List<Contrato> contratos = contratoService.listarContratosAtivos();
        for (Contrato contrato : contratos) {
            cbxEspetaculo.addItem(contrato);
        }
        atualizarContratoSelecionado();
    }

    public void atualizarDados() {
        carregarContratos();
    }

    private void atualizarContratoSelecionado() {
        contratoSelecionado = (Contrato) cbxEspetaculo.getSelectedItem();
        cbxSessao.removeAllItems();

        if (contratoSelecionado != null && contratoSelecionado.getNomePeca() != null) {
            System.out.println("[INFO] Espetáculo selecionado: " + contratoSelecionado.getNomePeca());
            List<Sessao> sessoesPeca = sessaoService.buscarPorPeca(contratoSelecionado.getNomePeca());
            for (Sessao s : sessoesPeca) {
                cbxSessao.addItem(s);
            }
        }

        assentosSelecionados.clear();
        if (lblAssentosEscolhidos != null) {
            lblAssentosEscolhidos.setText("Nenhum assento selecionado");
        }
        if (txtQuantidade != null) {
            txtQuantidade.setText("0");
        }
    }

    private void confirmarVenda() {
        if (usuarioEncontrado == null) {
            JOptionPane.showMessageDialog(this, "Busque e confirme um cliente antes da venda.");
            return;
        }

        if (contratoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um espetáculo.");
            return;
        }

        Sessao sessaoSelecionada = (Sessao) cbxSessao.getSelectedItem();
        if (sessaoSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma sessão.");
            return;
        }

        TipoSetor setorSelecionado = (TipoSetor) cbxSetor.getSelectedItem();
        if (setorSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um setor.");
            return;
        }

        if (assentosSelecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum assento foi selecionado!\n"
                            + "Por favor, clique em 'Escolher Assento(s) no Mapa' para definir as poltronas da venda.",
                    "Aviso de Assento Obrigatório", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            System.out.println("[INFO] Iniciando venda...");
            System.out.println("[INFO] Espetáculo selecionado: " + contratoSelecionado.getNomePeca());
            System.out.println("[INFO] Sessão selecionada: " + sessaoSelecionada.getNomePeca() + " ("
                    + sessaoSelecionada.getData() + " " + sessaoSelecionada.getHorarioInicio() + ")");
            List<String> codigosAssentos = assentosSelecionados.stream().map(Assento::getCodigo).toList();
            System.out.println("[INFO] Assentos selecionados: " + codigosAssentos);

            List<Ingresso> emitidos = ingressoService.venderIngressosComAssentos(
                    usuarioEncontrado,
                    sessaoSelecionada,
                    assentosSelecionados,
                    setorSelecionado,
                    contratoSelecionado);

            for (Ingresso ing : emitidos) {
                System.out.println("[INFO] Ingresso persistido com sucesso: ID " + ing.getId() + " - Código "
                        + ing.getCodigo() + " - Assento " + ing.getAssento().getCodigo() + " - Comprador "
                        + ing.getCliente().getNome());
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Venda concluída e persistida com sucesso!\n\n");
            sb.append("Cliente: ").append(usuarioEncontrado.getNome()).append("\n");
            sb.append("Espetáculo: ").append(contratoSelecionado.getNomePeca()).append("\n");
            sb.append("Sessão: ").append(sessaoSelecionada.getData()).append(" às ")
                    .append(sessaoSelecionada.getHorarioInicio()).append("\n");
            sb.append("Assento(s): ");
            for (int i = 0; i < emitidos.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append(emitidos.get(i).getAssento().getCodigo());
            }
            sb.append("\nTotal de Ingressos: ").append(emitidos.size());
            sb.append("\nValor Total: R$ ")
                    .append(String.format("%.2f", contratoSelecionado.getValorIngresso() * emitidos.size()));

            JOptionPane.showMessageDialog(this, sb.toString(), "Bilheteria - Venda Realizada",
                    JOptionPane.INFORMATION_MESSAGE);

            assentosSelecionados.clear();
            lblAssentosEscolhidos.setText("Nenhum assento selecionado");
            txtQuantidade.setText("0");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao realizar venda: " + e.getMessage(),
                    "Falha na Venda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparDadosCliente() {
        txtNome.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
    }
}