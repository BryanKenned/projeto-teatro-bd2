package br.com.projetoteatro.view;

import br.com.projetoteatro.model.Administrador;
import br.com.projetoteatro.service.AdministradorService;
import javax.swing.*;

public class CadastroAdministradorView extends JFrame {
    private AdministradorService admService;

    private JTextField txtNome;
    private JTextField txtEmail;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JPasswordField txtSenha;

    public CadastroAdministradorView(AdministradorService admService) {
        this.admService = admService;

        setTitle("Cadastro de Administrador");
        setSize(350, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Cadastro Administrador");
        lblTitulo.setBounds(100, 20, 200, 25);
        add(lblTitulo);

        txtNome = criarCampo("Nome:", 60);
        txtEmail = criarCampo("E-mail:", 100);
        txtCpf = criarCampo("CPF:", 140);
        txtTelefone = criarCampo("Telefone:", 180);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setBounds(30, 220, 80, 25);
        add(lblSenha);
        txtSenha = new JPasswordField();
        txtSenha.setBounds(110, 220, 150, 25);
        add(txtSenha);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(80, 280, 90, 30);
        add(btnSalvar);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds(180, 280, 90, 30);
        add(btnVoltar);

        btnSalvar.addActionListener(e -> {
            try {
                String nome = txtNome.getText();
                String email = txtEmail.getText();
                String cpf = txtCpf.getText();
                String telefone = txtTelefone.getText();
                String senha = new String(txtSenha.getPassword());

                Administrador adm = new Administrador(nome, email, telefone, cpf, senha);

                admService.cadastrarAdministrador(adm);
                System.out.println(
                        "[INFO] Administrador cadastrado com sucesso: " + adm.getNome() + " (" + adm.getEmail() + ")");
                JOptionPane.showMessageDialog(this, "Administrador cadastrado com sucesso!");
                dispose();
            } catch (Exception ex) {
                System.err.println("[ERRO] Erro ao cadastrar administrador: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        btnVoltar.addActionListener(e -> dispose());
    }

    private JTextField criarCampo(String label, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(30, y, 80, 25);
        add(lbl);
        JTextField txt = new JTextField();
        txt.setBounds(110, y, 150, 25);
        add(txt);
        return txt;
    }
}