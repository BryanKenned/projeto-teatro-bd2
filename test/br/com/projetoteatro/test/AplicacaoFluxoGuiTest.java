package br.com.projetoteatro.test;

import br.com.projetoteatro.service.CodigoRecuperacaoSenhaService;
import br.com.projetoteatro.service.EnviarEmailService;
import br.com.projetoteatro.service.validators.ServicoTeatro;
import br.com.projetoteatro.view.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class AplicacaoFluxoGuiTest {

        @Test
        @DisplayName("FLUXO DA APLICAÇÃO: Deve inicializar ServicoTeatro, LoginView e DashBoardView sem erros")
        public void deveInicializarTelasSemExcecao() {
                if (GraphicsEnvironment.isHeadless()) {
                        System.out.println("Ambiente Headless detectado - pulando renderização gráfica direta.");
                        return;
                }

                ServicoTeatro servicoTeatro = new ServicoTeatro();
                assertNotNull(servicoTeatro.getLoginService());
                assertNotNull(servicoTeatro.getPropostaService());
                assertNotNull(servicoTeatro.getRegrasService());
                assertNotNull(servicoTeatro.getContratoService());
                assertNotNull(servicoTeatro.getIngressoService());
                assertNotNull(servicoTeatro.getUsuarioService());
                assertNotNull(servicoTeatro.getSessaoService());

                EnviarEmailService emailService = new EnviarEmailService();
                CodigoRecuperacaoSenhaService codigoService = new CodigoRecuperacaoSenhaService(emailService);

                LoginView login = new LoginView(
                                servicoTeatro.getLoginService(),
                                servicoTeatro.getAdministradorService(),
                                codigoService,
                                servicoTeatro.getRegrasService(),
                                servicoTeatro.getPropostaService());
                assertNotNull(login, "LoginView deve instanciar com sucesso");
                login.dispose();

                DashBoardView dashBoard = new DashBoardView(
                                servicoTeatro.getRegrasService(),
                                servicoTeatro.getPropostaService());
                assertNotNull(dashBoard, "DashBoardView deve instanciar com sucesso com todas as abas");
                dashBoard.dispose();
        }

        @Test
        @DisplayName("CICLO DE VIDA JPA & SWING: Login, Cadastro e Operações no banco sem erro de EntityManagerFactory fechado")
        public void deveExecutarFluxoLoginCadastroEOperacoesSemErroJpaClosed() throws Exception {
                assertTrue(br.com.projetoteatro.config.JPAUtil.getEntityManagerFactory().isOpen(),
                                "EntityManagerFactory deve estar aberto durante o ciclo de uso");

                ServicoTeatro servico = new ServicoTeatro();
                String sufixo = String.valueOf(System.currentTimeMillis() % 1000000);

                long val = (System.currentTimeMillis() + System.nanoTime()) % 1000000000L;
                String base = String.format("%09d", Math.abs(val));
                if (base.matches("(\\d)\\1{8}")) {
                        base = "1" + base.substring(1, 8) + "2";
                }
                int[] d = new int[11];
                for (int i = 0; i < 9; i++) {
                        d[i] = base.charAt(i) - '0';
                }
                int soma1 = 0;
                for (int i = 0; i < 9; i++) {
                        soma1 += d[i] * (10 - i);
                }
                int d1 = (soma1 % 11 < 2) ? 0 : 11 - (soma1 % 11);
                d[9] = d1;
                int soma2 = 0;
                for (int i = 0; i < 10; i++) {
                        soma2 += d[i] * (11 - i);
                }
                int d2 = (soma2 % 11 < 2) ? 0 : 11 - (soma2 % 11);
                String cpfValido = base + d1 + d2;

                br.com.projetoteatro.model.Administrador novoAdm = new br.com.projetoteatro.model.Administrador(
                                "Adm Teste " + sufixo,
                                "adm_" + sufixo + "@teatro.com",
                                "81999990099",
                                cpfValido,
                                "senhaForte123");
                servico.getAdministradorService().cadastrarAdministrador(novoAdm);
                assertNotNull(novoAdm.getId(), "Administrador deve ser persistido no banco com ID gerado");

                br.com.projetoteatro.model.Pessoa autenticado = servico.getLoginService().autenticar(novoAdm.getEmail(),
                                "senhaForte123");
                assertNotNull(autenticado, "Usuário deve ser autenticado com sucesso consultando o banco");
                assertEquals(novoAdm.getNome(), autenticado.getNome());

                java.util.List<br.com.projetoteatro.model.Contrato> contratos = servico.getContratoService()
                                .listarContratosAtivos();
                assertNotNull(contratos, "Operação de leitura no banco deve executar com sucesso");

                br.com.projetoteatro.model.Contratante contratante = new br.com.projetoteatro.model.Contratante(
                                "Produtora " + sufixo,
                                "prod_" + sufixo + "@teatro.com",
                                "81988887777",
                                "cpf_pr_" + sufixo,
                                "123");
                new br.com.projetoteatro.repository.ArtistaRepository().adicionarArtista(contratante);

                int offset = 900 + (int) (System.currentTimeMillis() % 400);
                br.com.projetoteatro.model.PropostaAluguel proposta = new br.com.projetoteatro.model.PropostaAluguel(
                                contratante,
                                "Espetáculo Teste " + sufixo,
                                3000.0,
                                java.time.LocalDate.now().plusDays(offset),
                                java.time.LocalDate.now().plusDays(offset + 1),
                                java.time.LocalTime.of(19, 0),
                                java.time.LocalTime.of(21, 0),
                                50.0);
                servico.getPropostaService().cadastrarProposta(proposta);
                assertNotNull(proposta.getId(), "Operação de escrita no banco deve persistir a proposta");

                assertTrue(br.com.projetoteatro.config.JPAUtil.getEntityManagerFactory().isOpen(),
                                "EntityManagerFactory deve permanecer aberto após operações");
        }

        @Test
        @DisplayName("FLUXO COMPLETO H2 & SWING: Cadastrar no H2, autenticar no Login, gerenciar Cliente e sessões sem fechar EMF")
        public void deveSimularFluxoCompletoLoginCadastroEOperacoesSwingNoH2() throws Exception {
                assertTrue(br.com.projetoteatro.config.JPAUtil.getEntityManagerFactory().isOpen(),
                                "EntityManagerFactory deve estar aberto antes de iniciar operações");

                ServicoTeatro servico = new ServicoTeatro();
                String sufixo = String.valueOf(System.currentTimeMillis() % 1000000);

                long val = (System.currentTimeMillis() + System.nanoTime()) % 1000000000L;
                String base = String.format("%09d", Math.abs(val));
                if (base.matches("(\\d)\\1{8}")) {
                        base = "1" + base.substring(1, 8) + "2";
                }
                int[] d = new int[11];
                for (int i = 0; i < 9; i++) {
                        d[i] = base.charAt(i) - '0';
                }
                int soma1 = 0;
                for (int i = 0; i < 9; i++) {
                        soma1 += d[i] * (10 - i);
                }
                int d1 = (soma1 % 11 < 2) ? 0 : 11 - (soma1 % 11);
                d[9] = d1;
                int soma2 = 0;
                for (int i = 0; i < 10; i++) {
                        soma2 += d[i] * (11 - i);
                }
                int d2 = (soma2 % 11 < 2) ? 0 : 11 - (soma2 % 11);
                String cpfValido = base + d1 + d2;

                br.com.projetoteatro.model.Administrador adm = new br.com.projetoteatro.model.Administrador(
                                "Adm H2 " + sufixo,
                                "adm_h2_" + sufixo + "@teatro.com",
                                "81988880000",
                                cpfValido,
                                "senhaH2_123");
                servico.getAdministradorService().cadastrarAdministrador(adm);
                assertNotNull(adm.getId(), "Administrador deve ser persistido no H2 com sucesso");

                br.com.projetoteatro.model.Pessoa auth = servico.getLoginService().autenticar("adm_h2_" + sufixo + "@teatro.com", "senhaH2_123");
                assertNotNull(auth, "Administrador recém cadastrado deve ser autenticado no H2");
                assertEquals("Adm H2 " + sufixo, auth.getNome());

                long val2 = (System.currentTimeMillis() + System.nanoTime() + 12345L) % 1000000000L;
                String base2 = String.format("%09d", Math.abs(val2));
                if (base2.matches("(\\d)\\1{8}")) {
                        base2 = "2" + base2.substring(1, 8) + "3";
                }
                int[] dCli = new int[11];
                for (int i = 0; i < 9; i++) {
                        dCli[i] = base2.charAt(i) - '0';
                }
                int sc1 = 0;
                for (int i = 0; i < 9; i++) {
                        sc1 += dCli[i] * (10 - i);
                }
                int dc1 = (sc1 % 11 < 2) ? 0 : 11 - (sc1 % 11);
                dCli[9] = dc1;
                int sc2 = 0;
                for (int i = 0; i < 10; i++) {
                        sc2 += dCli[i] * (11 - i);
                }
                int dc2 = (sc2 % 11 < 2) ? 0 : 11 - (sc2 % 11);
                String cpfValidoCli = base2 + dc1 + dc2;

                br.com.projetoteatro.model.Usuario comprador = new br.com.projetoteatro.model.Usuario(
                                "Comprador H2 " + sufixo,
                                "comp_" + sufixo + "@email.com",
                                "81999998888",
                                cpfValidoCli,
                                "senhaComp");
                br.com.projetoteatro.service.ClienteService clienteService = new br.com.projetoteatro.service.ClienteService(new br.com.projetoteatro.repository.ClienteRepository());
                clienteService.cadastrarCliente(comprador);
                assertNotNull(comprador.getId(), "Cliente comprador deve ser persistido no H2");

                br.com.projetoteatro.model.Usuario clienteBuscado = clienteService.buscarPorCpf(cpfValidoCli);
                assertNotNull(clienteBuscado, "Cliente deve ser consultado com sucesso no H2");

                assertTrue(br.com.projetoteatro.config.JPAUtil.getEntityManagerFactory().isOpen(),
                                "EntityManagerFactory deve permanecer aberto e acessível");
        }
}
