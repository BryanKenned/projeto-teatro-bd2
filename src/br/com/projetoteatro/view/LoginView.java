package br.com.projetoteatro.view;

import br.com.projetoteatro.exceptions.LoginInvalidoException;
import br.com.projetoteatro.model.Pessoa;
import br.com.projetoteatro.service.AdministradorService;
import br.com.projetoteatro.service.CodigoRecuperacaoSenhaService;
import br.com.projetoteatro.service.LoginService;
import br.com.projetoteatro.service.PropostaService;
import br.com.projetoteatro.service.RegrasService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class LoginView extends JFrame {

    private static final String CAMINHO = "usuario.txt";

    private JTextField txtUser;
    private JTextField txtSenha;
    private JCheckBox lembrarSenha;

    private AdministradorService admService;
    private LoginService loginService;
    private CodigoRecuperacaoSenhaService codigoService;
    private RegrasService regrasService;
    private PropostaService propostaService;

    public LoginView(
            LoginService loginService,
            AdministradorService admService,
            CodigoRecuperacaoSenhaService codigoService,
            RegrasService regrasService,
            PropostaService propostaService) {

        this.loginService = loginService;
        this.admService = admService;
        this.codigoService = codigoService;
        this.regrasService = regrasService;
        this.propostaService = propostaService;

        setTitle("Gerenciamento Teatro");
        setSize(380, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(null);

        JLabel texto = new JLabel("Login Sistema");
        texto.setBounds(150, 30, 100, 20);
        add(texto);

        JLabel lblUser = new JLabel("User: ");
        lblUser.setBounds(50, 80, 80, 25);
        add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(110, 80, 150, 25);
        add(txtUser);

        JLabel lblSenha = new JLabel("Senha: ");
        lblSenha.setBounds(50, 130, 80, 25);
        add(lblSenha);

        txtSenha = new JTextField();
        txtSenha.setBounds(110, 130, 150, 25);
        add(txtSenha);

        JButton btnLogin = new JButton("login");
        btnLogin.setBounds(135, 200, 100, 30);
        add(btnLogin);

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBounds(135, 250, 100, 30);
        add(btnCadastrar);

        lembrarSenha = new JCheckBox("Lembrar usuário");
        lembrarSenha.setBounds(80, 170, 90, 20);
        add(lembrarSenha);

        JLabel lblEsqueciSenha = new JLabel("<html><u>Esqueci a senha</u></html>");

        lblEsqueciSenha.setBounds(190, 170, 120, 20);
        lblEsqueciSenha.setForeground(Color.BLUE);
        lblEsqueciSenha.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        add(lblEsqueciSenha);

        lblEsqueciSenha.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                RecuperarSenhaView telaRecuperar = new RecuperarSenhaView(
                        codigoService,
                        admService);

                telaRecuperar.setVisible(true);
                setVisible(false);

                telaRecuperar.addWindowListener(
                        new java.awt.event.WindowAdapter() {

                            @Override
                            public void windowClosed(
                                    java.awt.event.WindowEvent ev) {

                                setVisible(true);
                            }
                        });
            }
        });

        try {

            File file = new File(CAMINHO);

            if (file.exists()) {

                Scanner sc = new Scanner(file);

                if (sc.hasNextLine()) {

                    String emailSalvo = sc.nextLine();

                    txtUser.setText(emailSalvo);
                    lembrarSenha.setSelected(true);
                }

                sc.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        btnCadastrar.addActionListener(e -> {

            this.setVisible(false);

            CadastroAdministradorView cadastro = new CadastroAdministradorView(admService);

            cadastro.setVisible(true);

            cadastro.addWindowListener(
                    new java.awt.event.WindowAdapter() {

                        @Override
                        public void windowClosed(
                                java.awt.event.WindowEvent e) {

                            setVisible(true);
                        }
                    });
        });

        btnLogin.addActionListener(e -> {

            String usuario = txtUser.getText();
            String senha = txtSenha.getText();

            try {

                if (lembrarSenha.isSelected()) {

                    try (FileWriter writer = new FileWriter(CAMINHO)) {

                        writer.write(usuario);
                    }

                } else {

                    File file = new File(CAMINHO);

                    if (file.exists()) {
                        file.delete();
                    }
                }

                System.out.println("[INFO] Tentativa de login para o usuário: " + usuario);
                Pessoa logado = loginService.autenticar(usuario, senha);

                if (logado != null) {
                    System.out.println("[INFO] Usuário autenticado com sucesso: " + logado.getNome() + " ("
                            + logado.getEmail() + ")");
                    new DashBoardView(
                            regrasService,
                            propostaService).setVisible(true);
                    dispose();
                }

            } catch (LoginInvalidoException ex) {
                System.out.println("[WARN] Falha na autenticação: " + ex.getMessage());
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage());

            } catch (Exception ex) {
                System.err.println("[ERRO] Erro inesperado durante o login: " + ex.getMessage());
                JOptionPane.showMessageDialog(
                        this,
                        "Erro inesperado: " + ex.getMessage());
            }
        });
    }
}