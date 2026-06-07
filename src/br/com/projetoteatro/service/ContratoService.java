package br.com.projetoteatro.service;

import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.model.Ingresso;
import br.com.projetoteatro.repository.ContratoRepository;
import br.com.projetoteatro.repository.IngressoRepository;

import java.time.LocalDate;
import java.util.List;

public class ContratoService {

    // Adicionado o tipo correto para evitar erros de compilação nas chamadas
    private PdfService pdfService = new PdfService();
    private EnviarEmailService emailService = new EnviarEmailService();
    private ContratoRepository contratoRepository = new ContratoRepository();
    private IngressoRepository ingressoRepository = new IngressoRepository();

    public void activarContrato(PropostaAluguel proposta){
        proposta.contratar();

        String pdf = pdfService.gerarContrato(proposta);

        emailService.enviarArquivoPdf(
                proposta.getContratante().getEmail(),
                "Contrato de aluguel",
                "Segue contrato em anexo.", pdf
        );
    }

    // REGRA CRÍTICA: Varre o repositório para ver se há ingressos vendidos para o futuro
    private boolean existeIngressoFuturo(long contratoId) {
        List<Ingresso> ingressos = ingressoRepository.listar();
        PropostaAluguel contrato = contratoRepository.buscaContratoPorId(contratoId);

        if (ingressos == null || contrato == null) return false;

        for (Ingresso i : ingressos) {
            // Mesma navegação direta corrigida aqui
            if (i.getSessao().getPeca().getId() == contratoId) {
                if (i.getSessao().getData().isAfter(contrato.getDataFim())) {
                    return true;
                }
            }
        }
        return false;
    }

    // <<< MÉTODO QUE ESTAVA FALTANDO: Calcula a receita total dos ingressos da peça >>>
    public double calcularTotalIngressos(long contratoId) {
        List<Ingresso> ingressos = ingressoRepository.listar();
        double totalArrecadado = 0;

        if (ingressos == null) return 0;

        for (Ingresso i : ingressos) {
            // Navegação direta: Ingresso -> Sessão -> Peça -> ID
            if (i.getSessao().getPeca().getId() == contratoId) {
                totalArrecadado += i.getValor();
            }
        }
        return totalArrecadado;
    }

    // ENCERRAMENTO: Código descomentado e integrado ao seu ContratoRepository real
    public void encerrarContrato(long contratoId, double valorRepassadoArtista) {

        PropostaAluguel contrato = contratoRepository.buscaContratoPorId(contratoId);

        // 1. Validações de segurança
        if (contrato == null) {
            throw new RuntimeException("Contrato não encontrado");
        }

        if (contrato.getStatusProposta().toString().equals("ENCERRADO")) {
            throw new RuntimeException("Contrato já está encerrado");
        }

        // 2. Regra crítica: não pode ter ingressos futuros
        if (existeIngressoFuturo(contratoId)) {
            throw new RuntimeException(
                    "Não é possível encerrar: existem ingressos vendidos para datas futuras"
            );
        }

        // 3. Calcular valores financeiros
        double totalVendido = calcularTotalIngressos(contratoId);
        double saldoAluguel = totalVendido - valorRepassadoArtista;

        // 4. Atualizar contrato com os dados reais
        contrato.setStatusProposta(br.com.projetoteatro.enums.StatusProposta.ENCERRADO);
        contrato.setDataFim(LocalDate.now()); // Seta a data de encerramento real na hora do fechamento

        // AJUSTE: Usando o nome correto do método que criamos no seu repositório
        contratoRepository.atualizarContrato(contrato);

        // 5. Saídas de controle no console
        System.out.println("Contrato encerrado com sucesso!");
        System.out.println("Total vendido: " + totalVendido);
        System.out.println("Repassado ao artista: " + valorRepassadoArtista);
        System.out.println("Saldo do aluguel (Lucro Teatro): " + saldoAluguel);
    }
}