# Sistema Gerenciador de Apresentações Musicais (SGAM)

Este projeto foi desenvolvido para automatizar e otimizar o gerenciamento de apresentações musicais dos grupos da Escola de Música da Universidade Federal do Rio Grande do Norte (UFRN).

## 📋 Visão do Produto
Para grupos musicais e organizadores de eventos que enfrentam dificuldades no controle logístico, o **SGAM API** é um software de gestão web que centraliza a administração de membros, instrumentos e solicitações de transporte, integrando a gestão artística com a infraestrutura necessária.

## 🚀 Tecnologias Utilizadas
- **Linguagem:** Java 21+
- **Framework:** Spring Boot 3+
- **Módulos Spring:** Web, Data JPA, Validation).
- **Banco de Dados:** PostgreSQL.
- **DevOps:** GitHub Actions (CI) para build e testes automáticos.
- **Documentação:** Swagger/OpenAPI.

## 🏗️ Escopo do MVP
- **Autenticação e Autorização:** Perfis de usuário (Aluno, Funcionário, Administrador).
- **Gestão de Grupos:** Cadastro de grupos musicais e seus membros.
- **Gestão de Recursos:** Registro e vinculação de instrumentos aos membros.
- **Eventos:** Criação e gerenciamento de eventos (local, data, responsáveis).
- **Logística:** Módulo de solicitação de apresentação e transporte (veículos/motoristas).

## ⚙️ Processos e Metodologia
- **Metodologia:** Ágil (Scrum/Kanban adaptado).
- **Cadência:** Sprints de 2 semanas.
- **Gestão de Código:** Git Flow adaptado com Pull Requests e Code Review.
- **CI/CD:** Pipeline automatizada no GitHub Actions que valida o build e executa testes a cada commit na branch `develop`.

## 🛠️ Como Executar o Projeto

### Pré-requisitos
- Java 21 ou superior
- Maven 3+
- Docker e Docker Compose (para o banco de dados)

### Passos
1. Clone o repositório:
   ```bash
   git clone https://github.com/gabrielly-freire/sgam-api.git
   ```
2. Suba o banco de dados via Docker:
   ```bash
   docker-compose up db -d
   ```
3. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Acesse a documentação Swagger:
   `http://localhost:8080/api/swagger-ui.html`

## 👥 Equipe
- **Ana Beatriz Camilo da Costa** - Desenvolvedora
- **Arthur Boma Skeete Mypoyo** - Desenvolvedor
- **Fernando Silva dos Santos** - Analista de Sistema e Desenvolvedor
- **Francisca Gabrielly Lopes Freire** - Devops e Desenvolvedora

## 🔗 Links Úteis
- [Quadro Kanban (GitHub Projects)](https://github.com/users/gabrielly-freire/projects/5)
