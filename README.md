# BsDnD - Sistema Bancário Híbrido (API REST e CLI)

## Sobre o Projeto

O BsDnD é um ecossistema bancário simulado desenvolvido em Java e Spring Boot, concebido como um **projeto de evolução contínua**. O sistema demonstra a transição de uma aplicação de linha de comando (CLI) para um ambiente híbrido com API REST completa.

O foco central deste repositório é a aplicação de princípios de engenharia de software de nível corporativo, com ênfase em segurança, escalabilidade e manutenibilidade através de padrões de design modernos.

---

## Funcionalidades Principais

### Interface de Linha de Comando (CLI)
- Menu Interativo: Fluxo completo de navegação via terminal.
- Autenticação Segura: Sistema de login com proteção de senha no console.
- Gestão de Contas: Abertura de contas e consulta de saldos/extratos.
- Operações Transacionais: Transferências entre contas com validação de senha de transação.
- Sistema de Empréstimos: Cálculo dinâmico de limites baseado em renda e bônus.

### Interface API REST
- Endpoints RESTful: Suporte a operações de usuários, contas e transferências via HTTP.
- Segurança JWT: Proteção de rotas utilizando JSON Web Tokens.
- Tratamento Global de Exceções: Respostas padronizadas para erros de negócio e técnicos.

---

## Arquitetura e Princípios SOLID

O projeto foi estruturado para garantir extensibilidade e baixo acoplamento, aplicando rigorosamente os princípios **SOLID** (SRP, OCP, LSP, ISP e DIP) em toda a sua arquitetura em camadas. Esta abordagem facilita a manutenção e a escalabilidade, permitindo que o sistema cresça de forma modular e segura, garantindo que novas funcionalidades possam ser adicionadas com impacto mínimo no código existente.

---

## Segurança Unificada

O sistema utiliza uma infraestrutura de segurança compartilhada:
- Contexto de Segurança Global: Tanto a API quanto a CLI utilizam o SecurityContextHolder do Spring Security para gerenciar a identidade do usuário.
- Implementação de UserDetails: A entidade BankUser integra-se nativamente ao framework de segurança.
- Criptografia: Uso de BCrypt para armazenamento de senhas e proteção de dados sensíveis em memória.
- Filtros de Segurança: Validação de tokens Bearer para todas as requisições à API.

---

## Documentação da API REST

A API utiliza autenticação baseada em token.

Fluxo de Autenticação:
1. POST em /api/auth/login com CPF e senha.
2. Recebimento do JWT.
3. Inclusão do header "Authorization: Bearer <token>" em requisições subsequentes.

Endpoints Principais:
- POST /api/auth/login: Autenticação e geração de token.
- POST /api/users: Cadastro de novos usuários.
- GET /api/accounts: Listagem de contas do usuário autenticado.
- POST /api/transfers: Execução de transferências entre contas.

---

## Como Executar

### Pré-requisitos
- Java 17 ou superior.
- Maven 3.9 ou superior.
- MySQL Server.

### Configuração
1. Clone o repositório.
2. Crie o banco de dados 'bsdnd' no MySQL.
3. Crie um arquivo .env na raiz do projeto com as seguintes variáveis:
   DB_NAME=bsdnd
   DB_USER=seu_usuario
   DB_PASSWORD=sua_senha

### Execução Local
- Para rodar a API (Modo Servidor): mvn spring-boot:run
- Para rodar a CLI (Modo Interativo): java -jar target/BsDnD-0.0.1-SNAPSHOT.jar --cli
- Para rodar via Docker: docker-compose up --build

---

## Testes Automatizados

O projeto utiliza JUnit 5 para testes de integração. A suíte de testes utiliza o perfil "test" com um banco de dados H2 em memória, garantindo isolamento total do ambiente de desenvolvimento.
Comando para execução: mvn test

---

## Autor

Jean da Cruz Silva
GitHub: https://github.com/jeaaanc
