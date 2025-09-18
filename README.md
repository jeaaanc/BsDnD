# BsDnD - Banco Simulado(Java + Spring Boot)

> Projeto de estudo: sistema bancário simples em Java/Spring Boot para
cadastro de usuários, criação de contas e operações básicas(depósito, saque, transferência).
Feito para estudos e prática  
>Obs: Projeto em andamento

## Sumário
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação & configuração](#instalação--configuração)
- [Como rodar (sem Docker)](#como-rodar-sem-docker)
- [Como rodar com Docker](#como-rodar-com-docker)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [Troubleshooting](#troubleshooting)
- [Contribuição](#contribuição)
- [Licença](#licença)
- [Autor](#autor)

---

## Tecnologias
- Java 17
- Spring Boot
- Maven
- MySQL
- Docker (opcional)
- Flyway (migrações)

---



## Pré-requisitos
1. **Instalar dependências**:
    - [Java 17+](https://adoptium.net/)
    - [Maven](https://maven.apache.org/)
    - [MySQL](https://dev.mysql.com/downloads/)
    - (opcional) [Docker](https://www.docker.com/)

---

## Instalação & configuração
1. Clone o gitHub
```bash
git clone https://github.com/seu-usuario/seu-repo.git
cd seu-repo
  ```

2. Criar um Banco de dados MySQL
```
3. CREATE DATABASE bsdnd
```

3. Criar arquivo .env na raiz do projeto com esses dados (não vai para o Git):
```
DB_USER=root
DB_PASSWORD=senha_do_banco
DB_NAME=bsdnd
```
4. Configuração do application.properties:
```
spring.datasource.url=jdbc:mysql://localhost:3306/${DB_NAME}?useSSL=false&serverTimezone=UTC
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true 
```
---

## Estrutura do Projeto

└── main
└── java
└── bankSdnd/example/bsDnD
├── bank
│ ├── config
│ ├── controller
│ ├── domain
│ ├── dto
│ ├── exception
│ ├── menu
│ ├── repository
│ ├── service
│ └── util

---

## Como rodar sem docker

teste

---

## Como rodar com docker

Usar a imagem do OpenJDK 17  
```FROM openjdk:17-jdk-slim```

Definir diretório de trabalho  
```WORKDIR /app```

Copiar o arquivo JAR gerado pelo maven  
```COPY target/BsDnD-0.0.1-SNAPSHOT.jar app.jar```

Expor a porta que sua aplicação usa (Spring Boot normalmente 8080)  
```EXPOSE 8080```

Comando para rodar a aplicação  
```CMD ["java", "-jar", "app.jar"]```

---
#### 1 Gere o JAR da aplicação com Maven
```mvn clean package```

#### 2 Suba os containers com
```docker-compose up --build```

---


## Autor
Jean da Cruz Silva
