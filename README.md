# BsDnD - Simulador de Banco com Arquitetura Evolutiva (CLI + API REST)

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9-orange.svg)](https://maven.apache.org/)

## 📜 Sobre o Projeto

**BsDnD** é um projeto de estudos focado na construção de um sistema bancário simulado, desenvolvido com Java e Spring Boot. O projeto nasceu como uma **aplicação de linha de comando (CLI)** robusta, com o objetivo de solidificar conceitos fundamentais de backend, como segurança, design de software e arquitetura em camadas.

O objetivo principal é a **evolução contínua**. A base de código foi projetada para ser escalável, e o próximo grande passo é a implementação de uma **camada de API REST**, transformando a aplicação em um sistema híbrido que pode ser operado tanto via terminal (`--cli`) quanto por requisições HTTP, refletindo um ambiente de mercado mais realista.

Este repositório serve como um portfólio dinâmico, demonstrando não apenas o produto final, mas o processo de refatoração, a aplicação de boas práticas e a evolução arquitetural de um sistema.

## ✨ Funcionalidades Atuais (Modo CLI)

* **Gestão de Usuários:** Cadastro e autenticação de novos usuários.
* **Gestão de Contas:** Abertura de contas bancárias associadas a um usuário.
* **Operações Bancárias:**
    * Consulta de saldo.
    * Depósitos e Saques.
    * Transferências entre contas.
* **Produtos Financeiros:**
    * Cálculo de limite e solicitação de empréstimos.
* **Segurança:**
    * Captura de senha segura (sem eco no terminal ou com máscara em GUI).
    * Armazenamento de senhas com criptografia BCrypt.
    * Re-autenticação por senha para operações sensíveis.

## 🏛️ Arquitetura e Princípios de Design

Este projeto foi construído com foco em boas práticas de engenharia de software:

* **Arquitetura em Camadas:** Clara separação entre `Controller`, `Service`, `Repository` e `Domain`.
* **Injeção de Dependência:** Utilização do contêiner do Spring para gerenciar o ciclo de vida e as dependências dos componentes.
* **Princípio da Responsabilidade Única (SRP):** Classes e métodos focados em uma única tarefa (ex: `LoanService` para empréstimos, `ConsoleUI` apenas para exibição).
* **Segurança em Primeiro Lugar:** Tratamento cuidadoso de dados sensíveis (`char[]` para senhas, limpeza de memória).
* **Tratamento de Erros Robusto:** Uso de uma hierarquia de exceções customizadas (`BusinessException`) para erros de negócio.
* **Ambientes Separados:** Configuração de testes com um perfil (`test`) e banco de dados em memória (H2) para isolamento.

## 🛠️ Tecnologias Utilizadas

* **Backend:** Java 17, Spring Boot
* **Persistência:** Spring Data JPA, Hibernate
* **Banco de Dados:** MySQL
* **Build:** Maven
* **Segurança:** Spring Security (PasswordEncoder)
* **Migrações de Banco (Opcional):** Flyway
* **Conteinerização (Opcional):** Docker, Docker Compose

## 🚀 Como Executar o Projeto

Siga os passos abaixo para configurar e executar a aplicação em seu ambiente local.

### Pré-requisitos

* [Java 17 (JDK)](https://adoptium.net/)
* [Apache Maven](https://maven.apache.org/download.cgi)
* [MySQL Server](https://dev.mysql.com/downloads/mysql/)
* (Opcional) [Docker](https://www.docker.com/products/docker-desktop/)

### Configuração do Ambiente

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/jeaaanc/BsDnD.git](https://github.com/jeaaanc/BsDnD.git)
    cd BsDnD
    ```
2.  **Crie o Banco de Dados:**
    Conecte-se ao seu MySQL e execute o seguinte comando:
    ```sql
    CREATE DATABASE bsdnd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
3.  **Crie o arquivo de ambiente:**
    Na raiz do projeto, crie um arquivo chamado `.env` e adicione suas credenciais do banco:
    ```env
    DB_NAME=bsdnd
    DB_USER=seu_usuario_mysql
    DB_PASSWORD=sua_senha_mysql
    ```

### Opção 1: Rodando Localmente (Sem Docker)

1.  **Compile e empacote o projeto:**
    (Este comando também rodará os testes automatizados)
    ```bash
    mvn clean package
    ```
2.  **Execute a aplicação no modo interativo:**
    ```bash
    java -jar target/BsDnD-0.0.1-SNAPSHOT.jar --cli
    ```
    O menu da aplicação aparecerá no seu terminal.

### Opção 2: Rodando com Docker

Se você tiver o Docker e o Docker Compose instalados, o processo é mais simples.

1.  **Garanta que o arquivo `.jar` foi gerado:**
    Rode o comando de build do Maven pelo menos uma vez:
    ```bash
    mvn clean package
    ```
2.  **Suba os contêineres:**
    Este comando irá construir a imagem da sua aplicação e iniciar o contêiner do banco de dados e da aplicação.
    ```bash
    docker-compose up --build
    ```

## 🗺️ Roadmap (Próximos Passos)

* [ ] Implementação da camada de **API REST** com endpoints para todas as funcionalidades.
* [ ] Documentação da API com **Swagger/OpenAPI**.
* [ ] Refinamento da UI de console e adição de mais funcionalidades.
* [ ] Adição de mais testes de unidade e integração.

## ✒️ Autor

* **Jean da Cruz Silva** - [GitHub](https://github.com/jeaaanc)