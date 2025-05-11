package com.example.odontofast.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class IAService {

    private final OllamaChatModel chatModel;

    public IAService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    // Métodos existentes mantidos
    public String analisarFeedbackPaciente(String feedback) {
        Prompt prompt = new Prompt(
                "Analise o seguinte feedback e classifique como 'Positivo', 'Neutro' ou 'Negativo'. " +
                        "Feedback: " + feedback
        );

        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    public String gerarResumoAgendamentos(String agendamentosDoDia) {
        Prompt prompt = new Prompt(
                "Crie um resumo estruturado dos seguintes agendamentos do dia: " + agendamentosDoDia
        );

        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    public String gerarPerguntasAnamnese(String informacoesIniciais) {
        Prompt prompt = new Prompt(
                "Como dentista experiente, analise as seguintes informações iniciais do paciente e " +
                        "gere uma lista de perguntas complementares específicas e relevantes para uma anamnese " +
                        "odontológica mais completa. Foque em potenciais fatores de risco e relações com a saúde bucal.\n\n" +
                        "Organize as perguntas por categorias relevantes e apresente sua resposta em HTML formatado usando:\n" +
                        "1. Use elementos <h4> para os títulos das categorias\n" +
                        "2. Use listas <ul> com <li> para cada pergunta\n" +
                        "3. Separe claramente cada categoria com um pequeno espaço\n" +
                        "4. Use <strong> para enfatizar palavras-chave importantes\n" +
                        "5. Limite-se a no máximo 5 perguntas por categoria\n" +
                        "6. Use no máximo 6 categorias diferentes\n\n" +
                        "Informações iniciais do paciente: " + informacoesIniciais
        );

        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }
}