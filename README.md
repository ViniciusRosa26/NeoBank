# NeoBank

![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge&logo=redis)
![Docker](https://img.shields.io/badge/Docker-Container-blue?style=for-the-badge&logo=docker)
![React](https://img.shields.io/badge/React-Frontend-61DAFB?style=for-the-badge&logo=react)

## Sobre o projeto

O **NeoBank** é um projeto de banco digital desenvolvido com **Java e Spring Boot**, inspirado em funcionalidades de bancos modernos.
O objetivo do projeto foi praticar desenvolvimento backend com uma aplicação mais próxima de um sistema real, utilizando autenticação, banco de dados, arquitetura em camadas, segurança, transações financeiras, Pix, conta bancária, cartão de crédito, cache com Redis, WebSocket e deploy com Docker/AWS.
Este projeto faz parte do meu portfólio de estudos em desenvolvimento backend.



## Funcionalidades

- Cadastro de usuários
- Login com autenticação JWT
- Proteção de rotas com Spring Security
- Criação e gerenciamento de contas bancárias
- Operações financeiras
- Depósito
- Saque
- Transferência
- Operações via Pix
- Cadastro de chave Pix
- Entidade de cartão de crédito
- Cálculo de limite de cartão baseado em dados do usuário
- Controle de roles de usuário
- Tratamento global de exceções
- Respostas de erro padronizadas
- Integração com PostgreSQL
- Integração com Redis
- Comunicação em tempo real com WebSocket
- Documentação da API com Swagger/OpenAPI
- Frontend simples em React para testes de autenticação
- Ambiente containerizado com Docker Compose

---

## Tecnologias utilizadas

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Redis
- WebSocket
- Lombok
- Swagger / OpenAPI
- Maven

### Frontend

- React
- Vite
- JavaScript
- CSS

### Infraestrutura

- Docker
- Docker Compose
- AWS EC2
- PostgreSQL em container
- Redis em container

---

## Arquitetura do backend

O backend foi organizado em camadas para separar melhor as responsabilidades da aplicação.

```txt
src/main/java/com/example/NeoBank
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── handler
├── repository
├── service
├── websocket
└── NeoBankApplication.java
