package dev.antonio.portifolio.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class TemplateService {

    // O TemplateEngine é o "motor" do Thymeleaf que processa os arquivos HTML
    private final TemplateEngine templateEngine;

    /**
     * Gera o corpo do e-mail processando um arquivo HTML dinâmico.
     * @param nomeRecrutador Nome que será exibido no e-mail.
     * @return String contendo o HTML completo e pronto para ser enviado.
     */
    public String generateTemplate(String nomeRecrutador) {

        // 1. Criamos um objeto Context, que funciona como um "balde" de dados
        Context context = new Context();

        // 2. Adicionamos variáveis ao contexto.
        // No HTML, o Thymeleaf buscará por algo como th:text="${nome}"
        context.setVariable("nome", nomeRecrutador);

        // 3. O motor processa o arquivo localizado em 'src/main/resources/templates/email-template.html'
        // Ele substitui as tags dinâmicas pelos valores que colocamos no context.
        return templateEngine.process("email-template", context);
    }
}