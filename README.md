## Backend Java - Portfolio Lab API  
### Spring Boot & Cloud Infrastructure

### Esta API é responsável por autenticação via código temporário, métricas de uso do sistema e integração com serviços externos como e-mail, SMS e Discord.

Utilizada no **Modo Laboratório** do site de portfólio para validar acesso, testar endpoints e monitorar métricas e interações reais de usuários que interajam com o laboratório.

<p align="center">
  <img src="https://img.shields.io/badge/Status-Em%20Produ%C3%A7%C3%A3o-blue" />
  <img src="https://img.shields.io/badge/Frontend-Angular-DD0031" />
  <img src="https://img.shields.io/badge/Backend-Java%20%7C%20Spring-6DB33F" />
  <img src="https://img.shields.io/badge/Auth-JWT-orange" />
  <img src="https://img.shields.io/badge/Infra-Docker%20%7C%20Nginx-2496ED" />
  <img src="https://img.shields.io/badge/Cloud-OCI-red" />
</p>

<p align="center">
  <img src="https://img.shields.io/github/stars/Antonioafj/portifolio-frontend?style=social" />
</p>

---

## 🧠 Diagrama — Fluxo do Backend

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

---

## 🛣️ Endpoints da API

| Endpoint | Método | Proteção | Função |
|--------|--------|----------|--------|
| `/cv-download` | `POST` | Público | Registra download do currículo e captura IP real via `X-Forwarded-For`. |
| `/send-code` | `POST` | Público | Dispara código OTP via SMS (Twilio) ou E-mail. |
| `/verify-code` | `POST` | Público | Valida código OTP e retorna JWT. |
| `/test-template` | `POST` | JWT | Endpoint protegido para testes no Modo Laboratório. |

---

> ⚠️ O envio de código via SMS está restrito ao ambiente de testes, pois o serviço Twilio está em modo *trial*.


---
## 🔐 Monitoramento e Verificação

### Código de Verificação (Email / SMS)

<p align="center">
  <img src="https://github.com/user-attachments/assets/7910e8cf-6bca-4bcb-a2d6-1c4e80965a76" width="260" />
  <img src="https://github.com/user-attachments/assets/b53b2f45-7657-42e7-8ff0-db7f90143120" width="260" />
</p>

---

### Notificações de Acesso — Email

<p align="center">
  <img src="https://github.com/user-attachments/assets/ef2f227d-7eb6-4cd9-8482-e91e65214f4f" width="260" />
  <img src="https://github.com/user-attachments/assets/b3f421fa-023e-4665-bae7-3e70158a9516" width="260" />
  <img src="https://github.com/user-attachments/assets/470f33bf-6b93-48f6-8288-c2d90cd7c040" width="260" />
</p>

---

### Notificações de Acesso — Discord

<p align="center">
  <img src="https://github.com/user-attachments/assets/7ac56146-3730-433c-9e84-b485eebf09bf" width="260" />
  <img src="https://github.com/user-attachments/assets/98f1fa1f-934f-4ed4-819f-5ce58e8041a2" width="260" />
  <img src="https://github.com/user-attachments/assets/25137d92-aee2-4790-8157-e98305f520b1" width="260" />
</p>

---

## 🔒 Segurança Aplicada

- Autenticação baseada em **OTP com tempo de expiração**
- Emissão de **JWT assinado**
- Separação clara entre **endpoints públicos e protegidos**
- Captura de IP real via **Proxy Reverso (Nginx)**
- Bloqueio de acesso ao Modo Laboratório sem token válido

---

## 🌍 Localização por IP

A partir do IP real capturado via proxy reverso, a API consulta:

- http://ip-api.com/json/

Para obtenção de localização aproximada do usuário.

---

## ▶️ Executando Localmente (Docker)

```bash
docker compose up -d
```

Pré-requisitos:
- Docker
- Docker Compose

---

## 🛠️ Tecnologias Utilizadas

![Skills](https://skillicons.dev/icons?i=java,spring,postgres,docker,githubactions,nginx,ubuntu,idea,vscode)

---

## 🧭 Roadmap

- Rate limit por IP no fluxo OTP
- Cache de métricas
- Dashboard interno para visualização de métricas
- Integração com observabilidade (ex: Prometheus)

---

## 🔗 Link do Site

👉 <a href="https://antonioafj.dev/" target="_blank" rel="noopener noreferrer">
https://antonioafj.dev/
</a>
