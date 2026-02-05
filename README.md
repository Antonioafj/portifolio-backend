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
