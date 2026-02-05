## Backend Java - Portfolio Lab API - Spring Boot & Cloud Infrastructure.

### Esta API é responsável por autenticação via código temporário, métricas de uso do sistema e integração com serviços externos como e-mail, SMS e Discord.
Utilizada no modo laboratório do site de portfólio para validar acesso, testar endpoints e monitorar metricas e interações reais de usuários que interagiem com o laboratório.

**O Diagrama:** Fluxo No Backend .

```mermaid
graph TD
    subgraph Cliente [Frontend Angular]
        A[Visitante] -->|1 Solicita acesso| B[Interface do Portfólio]
        B -->|6 Usa JWT| C[Modo Laboratório]
    end

    subgraph Infra [Infraestrutura]
        B -->|Request HTTP| D[Nginx - Proxy Reverso]
        D -->|X-Forwarded-For| E[API Spring Boot]
    end

    subgraph Backend [Spring Boot]
        E --> F[Spring Security]
        F -->|2 Requisição permitida| G[LabController]
        G -->|3 Gera e envia código| H[LabService]
        H -->|4 Valida código| I[JwtTokenService]
        I -->|5 Gera JWT| B
        F --> J[SystemMetricsController]
    end

    subgraph Integracoes [Serviços Externos]
        H -->|Email ou SMS| K[JavaMail ou Twilio]
        J -->|Webhook| L[Discord Notifications]
        H -->|Webhook| L
    end
```

## Como funciona :
- É gerado um codigo de 6 digitos e enviado para o email se for digitado email, ou sms caso seja telefone celular
- Assim que é digitado o codigo e confirmado uma nova janela é aberta
- Com codigo jwt que o usuario conseguiu ao validar o código OTP ele pode fazer um teste de template - tem permissão.

## 🛠️ Minotoramenteo e verificação:
    ### Seu Código de Verificação - Email / sms

![twilio_cod](https://github.com/user-attachments/assets/5ecba2d1-820b-4df8-b2ec-0146477e259a)

<img width="524" height="420" alt="email_cod" src="https://github.com/user-attachments/assets/b53b2f45-7657-42e7-8ff0-db7f90143120" />

    ### Notficações de acesso.
    - Email

<img width="671" height="425" alt="email_monitor_acess_lab" src="https://github.com/user-attachments/assets/ef2f227d-7eb6-4cd9-8482-e91e65214f4f" />
<img width="623" height="441" alt="email_monitor_test_template" src="https://github.com/user-attachments/assets/b3f421fa-023e-4665-bae7-3e70158a9516" />
<img width="679" height="436" alt="email_monitor_down_curriculo" src="https://github.com/user-attachments/assets/470f33bf-6b93-48f6-8288-c2d90cd7c040" />

    - Discord

<img width="644" height="91" alt="discord_monitor_acess_lab" src="https://github.com/user-attachments/assets/7ac56146-3730-433c-9e84-b485eebf09bf" />
<img width="790" height="98" alt="discord_monitor_test_template" src="https://github.com/user-attachments/assets/98f1fa1f-934f-4ed4-819f-5ce58e8041a2" />
<img width="777" height="100" alt="discord_monitor_down_curriculo" src="https://github.com/user-attachments/assets/25137d92-aee2-4790-8157-e98305f520b1" />


## 🛠️ Tecnologias Utilizadas

![Skills](https://skillicons.dev/icons?i=java,spring,postgres,docker,githubactions,nginx,ubuntu,idea,vscode)

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                z
