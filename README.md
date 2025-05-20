🦷 OdontofastPipeline 
<div align="center">
  <img src="https://img.shields.io/badge/java-21-orange?style=for-the-badge&logo=java" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-latest-green?style=for-the-badge&logo=spring-boot" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Azure%20DevOps-pipeline-blue?style=for-the-badge&logo=azure-devops" alt="Azure DevOps"/>
  <img src="https://img.shields.io/badge/SQL%20Server-azure-blue?style=for-the-badge&logo=microsoft-sql-server" alt="SQL Server"/>
</div>
<br>

Aplicação Java Spring Boot para gerenciamento odontológico com pipeline CI/CD automatizada no Azure DevOps

<br>
📋 Visão Geral
O Odontofast é uma aplicação de gerenciamento odontológico desenvolvida em Java. Este repositório inclui:

☕ Código-fonte da aplicação Java Spring Boot<br>
🔄 Arquivo de configuração da pipeline Azure DevOps (azure-pipelines.yml)<br>
📚 Documentação do projeto<br>

<br>
Pipeline CI/CD<br>
A pipeline automatizada no Azure DevOps consiste em três estágios principais:
<br>
1. Criação da Infraestrutura (CriarInfra)<br>
Este estágio provisiona todos os recursos necessários na Azure:

📦 Grupo de recursos na região East US<br>
📊 Plano de serviço Linux com SKU F1 (gratuito)<br>
🌐 App Service com runtime Java 21<br>
🗄️ Servidor SQL com banco de dados S0<br>
🔒 Configuração de regras de firewall para o SQL Server<br>

<br>
2. Build da Aplicação (BuildApp)<br>
Este estágio compila a aplicação Java:

🔨 Compila o código usando Maven<br>
🧪 Executa testes unitários<br>
📦 Empacota a aplicação em um arquivo JAR<br>
📤 Publica o artefato para uso no estágio de deploy<br>

<br>
3. Deploy da Aplicação (DeployApp)<br>
Este estágio implanta a aplicação no ambiente Azure:

Baixa o artefato compilado<br>
Implanta o JAR no App Service<br>
Configura as conexões com o banco de dados SQL Server<br>
Desabilita o RabbitMQ (não utilizado neste projeto)<br>
Reinicia a aplicação para aplicar todas as configurações<br>

<br>
⚡ Gatilhos da Pipeline<br>
A pipeline é executada automaticamente quando há alterações nas seguintes branches:

main
master
minharelease

<br>
🔧 Variáveis de Ambiente<br>
A pipeline utiliza diversas variáveis para configurar os recursos:
VariávelDescriçãorm552656Informações do estudanteresourceGroup, service-plan, etc.Nomes de recursos Azuresql-server-name, sql-admin-user, etc.Configurações do SQL ServerruntimeVersão do runtime Java (21)
<br>
📋 Pré-requisitos para Execução Local

JDK 21
Maven
IDE Java (recomendado: IntelliJ IDEA ou Eclipse)
Azure CLI (para interagir com recursos Azure)

<br>
🚀 Como Executar Localmente

Clone este repositório
bashgit clone https://github.com/seu-usuario/odontofast-pipeline.git

Navegue até a pasta raiz do projeto
bash
cd odontofast-pipeline

Execute o Maven para compilar o projeto
bash
mvn clean install

Configure as variáveis de ambiente necessárias
bashexport SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;database=odontodb
export SPRING_DATASOURCE_USERNAME=sa
export SPRING_DATASOURCE_PASSWORD=yourpassword

Execute a aplicação
bashjava -jar target/*.jar


<br>
🔄 Como Configurar a Pipeline

Crie um projeto no Azure DevOps<br>
Configure uma conexão de serviço chamada 'MyAzureSubscription'<br>
Adicione este repositório ao Azure DevOps<br>
Configure a pipeline usando o arquivo azure-pipelines.yml existente

<br>
