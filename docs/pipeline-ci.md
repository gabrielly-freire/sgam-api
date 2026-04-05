# Documentação da Pipeline CI/CD - SGAM API

Esta documentação descreve o fluxo de Integração Contínua (CI) e Entrega Contínua (CD) configurado para o projeto SGAM API utilizando GitHub Actions. O arquivo de configuração principal encontra-se em `.github/workflows/workflow.yml`.

---

## Gatilhos (Triggers)

A pipeline é acionada automaticamente nos seguintes cenários:
- **Push**: Nas branches `main` e `develop`.
- **Pull Request**: Nas branches `main` e `develop`, nos estados `opened`, `reopened`, `synchronize` e `ready_for_review`.

---

## Ambiente de Execução

- **Java Version**: 21 (Temurin Distribution)
- **Sistema Operacional**: Ubuntu Latest
- **Gerenciador de Dependências**: Maven (com cache ativado para acelerar builds subsequentes)

---

## Etapas da Pipeline (Jobs)

O fluxo de trabalho é dividido em jobs sequenciais e condicionais:

### 1. Check code style (lint)
- **Objetivo**: Garantir que o código segue os padrões definidos no projeto.
- **Comando**: `mvn validate` (Checkstyle).

### 2. SpotBugs analysis (spotbugs)
- **Dependência**: Executa somente após o `lint` passar.
- **Objetivo**: Análise estática do código para identificar bugs potenciais e más práticas.
- **Comando**: `mvn compile spotbugs:check`.
- **Artefato**: Gera um relatório em `target/spotbugsXml.xml`.

### 3. Unit & integration tests (test)
- **Dependência**: Executa somente após o `spotbugs` passar.
- **Objetivo**: Executar os testes automatizados e gerar relatório de cobertura.
- **Comando**: `mvn test jacoco:report -Dspring.profiles.active=test`.
- **Artefatos**:
  - Relatório de Cobertura (JaCoCo) em `target/site/jacoco/`.
  - Relatórios de Testes (Surefire) em `target/surefire-reports/`.

### 4. Build and push application (build)
- **Dependência**: Executa somente após o `test` passar.
- **Objetivo**: Validar o empacotamento da aplicação e publicar a imagem no Docker Hub.
- **Passos**:
  1. Verifica se o JAR é construído corretamente (`mvn package -DskipTests`).
  2. Faz login no Docker Hub usando segredos (`DOCKER_USERNAME` e `DOCKER_TOKEN`).
  3. Constrói e envia a imagem Docker: `${DOCKER_USERNAME}/sgam:latest`.

### 5. Dependency vulnerability scan (security-scan)
- **Condição**: Executa **apenas na branch `main`** e depende do sucesso do job `build`.
- **Objetivo**: Identificar vulnerabilidades de segurança em dependências e na imagem Docker.
- **Ferramentas**:
  - **OWASP Dependency Check**: Verifica o `pom.xml` contra bases de CVEs conhecidas. Gera relatório em `target/dependency-check-report.html`.
  - **Trivy**: Escaneia a imagem Docker em busca de vulnerabilidades críticas ou altas.
- **Publicação**: Os resultados do Trivy são enviados para a aba "Security" do GitHub via SARIF.

---

## CD (Continuous Deployment) - Em Breve

As etapas de deploy e destruição de recursos estão atualmente comentadas no workflow, prontas para serem implementadas:
- `deploy`: Implantação em ambiente de produção.
- `destroy`: Limpeza e remoção de recursos da infraestrutura.

---

## Como visualizar os relatórios?

Sempre que uma pipeline é finalizada, os artefatos (relatórios de bugs, cobertura e segurança) ficam disponíveis para download na aba **"Actions"** do GitHub, selecionando a execução específica do workflow.
