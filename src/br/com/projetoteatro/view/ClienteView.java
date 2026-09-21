package br.com.projetoteatro.view;

import br.com.projetoteatro.enums.Genero;
import br.com.projetoteatro.model.Usuario;
import br.com.projetoteatro.service.ClienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ClienteView extends JPanel {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ClienteService clienteService;
    private final Runnable onVoltar;
    private final boolean modoDialog;

    private Consumer<Usuario> onClienteCadastrado;

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JTextField txtDataNascimento;
    private JComboBox<Genero> cbxSexo;
    private JPasswordField txtSenha;

    private JButton btnCadastrar;
    private JButton btnLimpar;
    private JButton btnVoltar;

    private JTextField txtBuscarCpf;
    private JButton btnBuscarCpf;
    private JButton btnListarTodos;

    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;

    public ClienteView(ClienteService clienteService) {
        this(clienteService, null, false);
    }

    public ClienteView(ClienteService clienteService, Runnable onVoltar) {
        this(clienteService, onVoltar, false);
    }

    public ClienteView(ClienteService clienteService, Runnable onVoltar, boolean modoDialog) {
        this.clienteService = clienteService;
        this.onVoltar = onVoltar;
        this.modoDialog = modoDialog;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        inicializarComponentes();
        montarLayout();

        if (!modoDialog) {
            atualizarTabela();
        }
    }

    public void setOnClienteCadastrado(Consumer<Usuario> callback) {
        this.onClienteCadastrado = callback;
    }

    public void setCpf(String cpf) {
        if (txtCpf != null && cpf != null) {
            txtCpf.setText(cpf.trim());
        }
    }

    private void inicializarComponentes() {
        txtNome = new JTextField();
        txtCpf = new JTextField();
        txtEmail = new JTextField();
        txtTelefone = new JTextField();
        txtDataNascimento = new JTextField();
        cbxSexo = new JComboBox<>(Genero.values());
        txtSenha = new JPasswordField();

        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBackground(new Color(46, 204, 113));
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBackground(new Color(243, 156, 18));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFont(new Font("Arial", Font.BOLD, 13));
        btnLimpar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVoltar = new JButton("Voltar");
        btnVoltar.setBackground(new Color(149, 165, 166));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 13));
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        txtBuscarCpf = new JTextField(15);
        btnBuscarCpf = new JButton("Buscar por CPF");
        btnBuscarCpf.setBackground(new Color(52, 152, 219));
        btnBuscarCpf.setForeground(Color.WHITE);
        btnBuscarCpf.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnListarTodos = new JButton("Listar Todos");
        btnListarTodos.setBackground(new Color(52, 73, 94));
        btnListarTodos.setForeground(Color.WHITE);
        btnListarTodos.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String[] colunas = { "ID", "Nome", "CPF", "E-mail", "Telefone", "Data Nascimento", "Sexo" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaClientes = new JTable(modeloTabela);
        tabelaClientes.setRowHeight(24);
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaClientes.getSelectedRow() != -1) {
                preencherCamposDaLinhaSelecionada();
            }
        });

        btnCadastrar.addActionListener(e -> cadastrarCliente());
        btnLimpar.addActionListener(e -> limparCampos());
        btnVoltar.addActionListener(e -> {
            if (onVoltar != null) {
                onVoltar.run();
            }
        });

        btnBuscarCpf.addActionListener(e -> buscarClientePorCpf());
        btnListarTodos.addActionListener(e -> atualizarTabela());
    }

    private void montarLayout() {
        JPanel painelFormulario = new JPanel(new GridLayout(7, 2, 10, 8));
        painelFormulario.setBackground(Color.WHITE);
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados do Comprador / Cliente"));

        painelFormulario.add(new JLabel("Nome Completo:"));
        painelFormulario.add(txtNome);

        painelFormulario.add(new JLabel("CPF (somente números ou formatado):"));
        painelFormulario.add(txtCpf);

        painelFormulario.add(new JLabel("E-mail:"));
        painelFormulario.add(txtEmail);

        painelFormulario.add(new JLabel("Telefone:"));
        painelFormulario.add(txtTelefone);

        painelFormulario.add(new JLabel("Data de Nascimento (dd/MM/yyyy):"));
        painelFormulario.add(txtDataNascimento);

        painelFormulario.add(new JLabel("Sexo:"));
        painelFormulario.add(cbxSexo);

        painelFormulario.add(new JLabel("Senha de Acesso (opcional):"));
        painelFormulario.add(txtSenha);

        JPanel painelBotoesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        painelBotoesForm.setBackground(Color.WHITE);
        painelBotoesForm.add(btnCadastrar);
        painelBotoesForm.add(btnLimpar);
        painelBotoesForm.add(btnVoltar);

        JPanel painelEsquerdo = new JPanel(new BorderLayout(10, 10));
        painelEsquerdo.setBackground(Color.WHITE);
        painelEsquerdo.add(painelFormulario, BorderLayout.CENTER);
        painelEsquerdo.add(painelBotoesForm, BorderLayout.SOUTH);

        if (modoDialog) {
            add(painelEsquerdo, BorderLayout.CENTER);
            return;
        }

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelBusca.setBackground(Color.WHITE);
        painelBusca.add(new JLabel("Buscar CPF:"));
        painelBusca.add(txtBuscarCpf);
        painelBusca.add(btnBuscarCpf);
        painelBusca.add(btnListarTodos);

        JScrollPane scrollTabela = new JScrollPane(tabelaClientes);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados"));

        JPanel painelDireito = new JPanel(new BorderLayout(10, 10));
        painelDireito.setBackground(Color.WHITE);
        painelDireito.add(painelBusca, BorderLayout.NORTH);
        painelDireito.add(scrollTabela, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelEsquerdo, painelDireito);
        split.setDividerLocation(420);
        split.setResizeWeight(0.4);
        split.setBackground(Color.WHITE);

        add(split, BorderLayout.CENTER);
    }

    private void cadastrarCliente() {
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String strDataNasc = txtDataNascimento.getText().trim();
        Genero sexo = (Genero) cbxSexo.getSelectedItem();
        String senha = new String(txtSenha.getPassword()).trim();

        if (senha.isEmpty()) {
            senha = "123456";
        }

        LocalDate dataNascimento = null;
        if (!strDataNasc.isEmpty()) {
            try {
                dataNascimento = LocalDate.parse(strDataNasc, DATA_FORMATTER);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Data de nascimento inválida! Use o formato dd/MM/yyyy.",
                        "Formato Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            Usuario cliente = new Usuario(nome, cpf, email, sexo, dataNascimento, telefone, senha);
            clienteService.cadastrarCliente(cliente);

            System.out.println(
                    "[INFO] Cliente cadastrado com sucesso: " + cliente.getNome() + " (CPF: " + cliente.getCpf() + ")");
            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            limparCampos();

            if (!modoDialog) {
                atualizarTabela();
            }

            if (onClienteCadastrado != null) {
                onClienteCadastrado.accept(cliente);
            }

        } catch (Exception ex) {
            System.err.println("[WARN] Falha ao cadastrar cliente: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarClientePorCpf() {
        String cpfBusca = txtBuscarCpf.getText().trim();
        if (cpfBusca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o CPF a ser pesquisado.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario cliente = clienteService.buscarPorCpf(cpfBusca);
            modeloTabela.setRowCount(0);

            if (cliente != null) {
                System.out.println(
                        "[INFO] Cliente encontrado pelo CPF: " + cliente.getNome() + " (" + cliente.getCpf() + ")");
                adicionarLinhaTabela(cliente);
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado com o CPF informado.", "Não Encontrado",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar cliente: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void atualizarDados() {
        atualizarTabela();
    }

    private void atualizarTabela() {
        if (modeloTabela == null)
            return;
        modeloTabela.setRowCount(0);

        try {
            List<Usuario> clientes = clienteService.listarClientes();
            for (Usuario u : clientes) {
                adicionarLinhaTabela(u);
            }
        } catch (Exception ex) {
            System.err.println("Erro ao listar clientes: " + ex.getMessage());
        }
    }

    private void adicionarLinhaTabela(Usuario u) {
        String dataStr = u.getDataNascimento() != null ? u.getDataNascimento().format(DATA_FORMATTER) : "-";
        String sexoStr = u.getSexo() != null ? u.getSexo().name() : "-";
        modeloTabela.addRow(new Object[] {
                u.getId(),
                u.getNome(),
                u.getCpf(),
                u.getEmail(),
                u.getTelefone() != null ? u.getTelefone() : "-",
                dataStr,
                sexoStr
        });
    }

    private void preencherCamposDaLinhaSelecionada() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha != -1) {
            txtNome.setText(String.valueOf(modeloTabela.getValueAt(linha, 1)));
            txtCpf.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));
            txtEmail.setText(String.valueOf(modeloTabela.getValueAt(linha, 3)));
            Object tel = modeloTabela.getValueAt(linha, 4);
            txtTelefone.setText(tel != null && !"-".equals(tel) ? tel.toString() : "");
            Object data = modeloTabela.getValueAt(linha, 5);
            txtDataNascimento.setText(data != null && !"-".equals(data) ? data.toString() : "");
            Object sexo = modeloTabela.getValueAt(linha, 6);
            if (sexo != null && !"-".equals(sexo)) {
                try {
                    cbxSexo.setSelectedItem(Genero.valueOf(sexo.toString()));
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void limparCampos() {
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        txtDataNascimento.setText("");
        txtSenha.setText("");
        if (cbxSexo.getItemCount() > 0) {
            cbxSexo.setSelectedIndex(0);
        }
        if (tabelaClientes != null) {
            tabelaClientes.clearSelection();
        }
    }

    public static Usuario abrirDialogCadastro(Window parent, ClienteService clienteService, String cpfInicial) {
        final Usuario[] resultado = new Usuario[1];

        JDialog dialog = new JDialog(parent, "Cadastro de Cliente", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(parent);

        ClienteView view = new ClienteView(clienteService, () -> dialog.dispose(), true);
        if (cpfInicial != null && !cpfInicial.isBlank()) {
            view.setCpf(cpfInicial);
        }

        view.setOnClienteCadastrado(novoCliente -> {
            resultado[0] = novoCliente;
            dialog.dispose();
        });

        dialog.setContentPane(view);
        dialog.setVisible(true);

        return resultado[0];
    }
}
