package dev.antonio.portifolio.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
public class TemplateService {

    private  final TemplateEngine templateEngine;


    public String generateTemplate(String nomeRecrutador) {

        Context context = new Context();
        context.setVariable("nome", nomeRecrutador);

        return templateEngine.process("email-template", context);
        }
 }

