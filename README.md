<!--# urnaeletronica-virtual-java-->


# Urna Virtual - Projeto de Testes Automatizados 

## 📋 Descrição
Este projeto consiste no desenvolvimento de uma API RESTful para gerenciar votações online. A API permite o cadastro de eleitores, candidatos e a realização de apuração de votos. Além disso, são implementados testes automatizados para validar as regras de negócio. O projeto é parte da disciplina de Testes Automatizados, ministrada pelo professor Wellington de Oliveira.

<!--## 📅 Prazo de Entrega
**16/09/2024** até 23h59 (segunda-feira).-->

## 🚀 Funcionalidades
- Cadastro de eleitores e candidatos com validações específicas.
- Registro de votos para candidatos a prefeito e vereador.
- Apuração dos votos com geração de relatórios.
- Regras de negócio para gerenciamento do status dos eleitores e candidatos.
- Implementação de testes unitários e de integração com cobertura de 80%.

## 📦 Estrutura do Projeto
- **Controller**: Gerenciamento das requisições HTTP.
- **Service**: Processamento das regras de negócio.
- **Repository**: Persistência de dados.
- **Entities**: Representação das entidades Eleitor, Candidato, Voto e Apuração.
  
## 🛠 Tecnologias Utilizadas
- Java 11
- Spring Boot
- JPA/Hibernate
- JUnit 5
- Mockito

## 📝 Regras de Negócio
### Eleitor
- O eleitor pode ter o status: `APTO`, `INATIVO`, `BLOQUEADO`, `PENDENTE` ou `VOTOU`.
- Um eleitor inativo não pode ser removido do sistema, apenas seu status pode ser alterado.
  
### Candidato
- Cada candidato deve ter um CPF válido e número único.
- Função deve ser identificada como `1` para prefeito e `2` para vereador.
  
### Votação
- Apenas eleitores aptos podem votar.
- O sistema gera um hash como comprovante de votação.
  
### Apuração
- O total de votos para cada candidato é apurado e os candidatos são classificados de forma decrescente pelo número de votos.

## 🔍 Testes Automatizados
Foram implementados testes unitários e de integração, abrangendo:
- Validações de entidades (eleitor, candidato, voto).
- Regras de negócio complexas (status de eleitor e candidato, apuração).
- Cobertura mínima de 80%.

<!--## ⚙️ Instalação
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/urna-virtual.git ->>
