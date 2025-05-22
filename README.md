# 🦷 OdontofastPipeline

<div align="center">
  <img src="https://img.shields.io/badge/java-21-orange?style=for-the-badge&logo=java" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-latest-green?style=for-the-badge&logo=spring-boot" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Azure%20DevOps-pipeline-blue?style=for-the-badge&logo=azure-devops" alt="Azure DevOps"/>
  <img src="https://img.shields.io/badge/SQL%20Server-azure-blue?style=for-the-badge&logo=microsoft-sql-server" alt="SQL Server"/>
</div>

## 📋 Visão Geral

Odontofast é uma aplicação de gerenciamento odontológico desenvolvida em Java com Spring Boot e integração completa de pipeline CI/CD no Azure DevOps.

Este repositório inclui:
- ☕ Código-fonte da aplicação Java Spring Boot
- 🔄 Arquivo de configuração da pipeline Azure DevOps (`azure-pipelines.yml`)
- 📚 Documentação do projeto

## 🚀 Pipeline CI/CD

A pipeline automatizada no Azure DevOps consiste em três estágios principais:

### 1. Criação da Infraestrutura (CriarInfra)

Este estágio provisiona todos os recursos necessários na Azure:
- 📦 Grupo de recursos na região East US
- 📊 Plano de serviço Linux com SKU F1 (gratuito)
- 🌐 App Service com runtime Java 21
- 🗄️ Servidor SQL com banco de dados S0
- 🔒 Configuração de regras de firewall para o SQL Server

### 2. Build da Aplicação (BuildApp)

Este estágio compila a aplicação Java:
- 🔨 Compila o código usando Maven
- 🧪 Executa testes unitários
- 📦 Empacota a aplicação em um arquivo JAR
- 📤 Publica o artefato para uso no estágio de deploy

### 3. Deploy da Aplicação (DeployApp)

Este estágio implanta a aplicação no ambiente Azure:
- ⬇️ Baixa o artefato compilado
- 🚀 Implanta o JAR no App Service
- 🔄 Configura as conexões com o banco de dados SQL Server
- ⚙️ Desabilita o RabbitMQ (não utilizado neste projeto)
- 🔄 Reinicia a aplicação para aplicar todas as configurações

## ⚡ Gatilhos da Pipeline

A pipeline é executada automaticamente quando há alterações nas seguintes branches:
- `main`
- `master`
- `minharelease`

## 🔧 Variáveis de Ambiente

A pipeline utiliza diversas variáveis para configurar os recursos:

| Variável | Descrição |
|----------|-----------|
| `rm552656` | Informações do estudante |
| `resourceGroup`, `service-plan`, etc. | Nomes de recursos Azure |
| `sql-server-name`, `sql-admin-user`, etc. | Configurações do SQL Server |
| `runtime` | Versão do runtime Java (21) |

## 📋 Pré-requisitos para Execução Local

- JDK 21
- Maven
- IDE Java (recomendado: IntelliJ IDEA ou Eclipse)
- Azure CLI (para interagir com recursos Azure)

## 💻 Como Executar Localmente

1. Clone este repositório
   ```bash
   git clone https://github.com/seu-usuario/odontofast-pipeline.git

## 💻 Como Executar Localmente

1. Navegue até a pasta raiz do projeto
```bash
   cd odontofast-pipeline
```
## Execute o Maven para compilar o projeto
```bash
mvn clean install
```
## Configure as variáveis de ambiente necessárias
```bash
export SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;database=odontodb
export SPRING_DATASOURCE_USERNAME=sa
export SPRING_DATASOURCE_PASSWORD=yourpassword
```
##Execute a aplicação
```bash
java -jar target/*.jar
```

## 🔄 Como Configurar a Pipeline

- Crie um projeto no Azure DevOps
- Configure uma conexão de serviço chamada 'MyAzureSubscription'
- Adicione este repositório ao Azure DevOps
- Configure a pipeline usando o arquivo azure-pipelines.yml existente

## 📊 Scripts SQL Úteis
```bash
Consultar dentistas
SELECT * FROM [dbo].[c_op_dentista];

-- Consultar funções dos dentistas
SELECT * FROM [dbo].[c_op_dentista_roles];

-- Remover todas as funções de dentistas
DELETE FROM [dbo].[c_op_dentista_roles];

-- Remover um dentista específico
DELETE FROM [dbo].[c_op_dentista] WHERE id_dentista=12;
```

## Arquitetura

## Link do Vídeo de Apresentação 
[Clique aqui](https://www.youtube.com/watch?v=MxP7L1ZopFg&feature=youtu.be)


.YAML ilustrado:
![image](https://github.com/user-attachments/assets/94032f4b-baf1-449a-9c7e-185a33a10abc)


## Integrantes Odontofast
- Felipe Amador - RM553528
- Leonardo Oliveira - RM554024
- Sara Sousa - RM552656
