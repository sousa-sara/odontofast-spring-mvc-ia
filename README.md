🦷 OdontofastPipeline 🚀
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

☕ Código-fonte da aplicação Java Spring Boot
🔄 Arquivo de configuração da pipeline Azure DevOps (azure-pipelines.yml)
📚 Documentação do projeto

<br>
🚀 Pipeline CI/CD
A pipeline automatizada no Azure DevOps consiste em três estágios principais:
<br>
🏗️ 1. Criação da Infraestrutura (CriarInfra)
Este estágio provisiona todos os recursos necessários na Azure:

📦 Grupo de recursos na região East US<br>
📊 Plano de serviço Linux com SKU F1 (gratuito)<br>
🌐 App Service com runtime Java 21<br>
🗄️ Servidor SQL com banco de dados S0<br>
🔒 Configuração de regras de firewall para o SQL Server<br>

<br>
🛠️ 2. Build da Aplicação (BuildApp)
Este estágio compila a aplicação Java:

🔨 Compila o código usando Maven
🧪 Executa testes unitários
📦 Empacota a aplicação em um arquivo JAR
📤 Publica o artefato para uso no estágio de deploy

<br>
🚢 3. Deploy da Aplicação (DeployApp)
Este estágio implanta a aplicação no ambiente Azure:

📥 Baixa o artefato compilado
🚀 Implanta o JAR no App Service
⚙️ Configura as conexões com o banco de dados SQL Server
🔌 Desabilita o RabbitMQ (não utilizado neste projeto)
🔄 Reinicia a aplicação para aplicar todas as configurações

<br>
⚡ Gatilhos da Pipeline
A pipeline é executada automaticamente quando há alterações nas seguintes branches:

main
master
minharelease

<br>
🔧 Variáveis de Ambiente
A pipeline utiliza diversas variáveis para configurar os recursos:
VariávelDescriçãorm552656Informações do estudanteresourceGroup, service-plan, etc.Nomes de recursos Azuresql-server-name, sql-admin-user, etc.Configurações do SQL ServerruntimeVersão do runtime Java (21)
<br>
📋 Pré-requisitos para Execução Local

☕ JDK 21
🛠️ Maven
💻 IDE Java (recomendado: IntelliJ IDEA ou Eclipse)
🌐 Azure CLI (para interagir com recursos Azure)

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

Crie um projeto no Azure DevOps
Configure uma conexão de serviço chamada 'MyAzureSubscription'
Adicione este repositório ao Azure DevOps
Configure a pipeline usando o arquivo azure-pipelines.yml existente

<br>



- Arquivo .yaml que usamos para rodar nossa pipeline:

# Trigger: Define quando a pipeline será automaticamente executada
trigger:
  branches:
    include:
    - main
    - master
    - minharelease

# Pool: Define o ambiente de execução da pipeline
pool:
  vmImage: "ubuntu-latest"

# Variáveis globais usadas em toda a pipeline
variables:
- name: rm552656
  value: rm552656
- name: location
  value: eastus
- name: resourceGroup
  value: rg-OdontofastPipeline
- name: service-plan
  value: planOdontofastPipeline
- name: app-name
  value: OdontofastPipeline-rm552656
- name: runtime
  value: JAVA:21-java21
- name: sku
  value: F1
- name: nome-artefato
  value: OdontofastPipeline
# Variáveis para SQL Server - COM NOME ÚNICO PARA CADA BUILD
- name: sql-server-name
  value: odontofast-sql-$(Build.BuildId)
- name: sql-db-name
  value: odontodb
- name: sql-admin-user
  value: sqladmin
- name: sql-admin-password
  value: Fiap@2ds2025

# Estágio 1: Criar a infraestrutura na Azure
stages:
- stage: CriarInfra
  jobs:
  - job: criaWebApp
    displayName: Criar ou atualizar o Serviço de Aplicativo
    steps:
    - task: AzureCLI@2
      inputs:
        azureSubscription: 'MyAzureSubscription'
        scriptType: 'bash'
        scriptLocation: 'inlineScript'
        inlineScript: |
          # Criar o Grupo de Recursos
          echo "Criando grupo de recursos..."
          az group create --location $(location) --name $(resourceGroup)
          
          # Criar o Plano de Serviço com SO igual a Linux
          echo "Criando plano de serviço..."
          az appservice plan create -g $(resourceGroup) -n $(service-plan) --is-linux --sku $(sku)
          
          # Criar um Serviço de Aplicativo com JAVA 21 SE como runtime
          echo "Criando App Service..."
          az webapp create -g $(resourceGroup) -p $(service-plan) -n $(app-name) --runtime "$(runtime)"
          
          # Criar SQL Server com nome único que inclui o Build ID (garantindo exclusividade)
          echo "Criando SQL Server com nome único: $(sql-server-name)..."
          az sql server create --resource-group $(resourceGroup) --name $(sql-server-name) --location eastus2 --admin-user $(sql-admin-user) --admin-password "$(sql-admin-password)"
          
          # Criar banco de dados com nível S0
          echo "Criando banco de dados..."
          az sql db create --resource-group $(resourceGroup) --server $(sql-server-name) --name $(sql-db-name) --service-objective S0
          
          # Configurar regra de firewall para permitir serviços Azure
          echo "Configurando regras de firewall..."
          az sql server firewall-rule create --name AllowAzureServices \
            --resource-group $(resourceGroup) --server $(sql-server-name) \
            --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0
          
          # Guardar o FQDN para uso posterior
          SQL_FQDN=$(az sql server show --name $(sql-server-name) --resource-group $(resourceGroup) --query fullyQualifiedDomainName -o tsv)
          echo "##vso[task.setvariable variable=SQL_FQDN;isOutput=true]$SQL_FQDN"
          echo "SQL Server criado: $SQL_FQDN"
        visibleAzLogin: false
      name: infraStep

# Estágio 2: Build da aplicação Java
- stage: BuildApp
  variables:
  - name: mavenPOMFile
    value: 'pom.xml'
  jobs:
  - job: buildWebApp
    displayName: Realizar o Build do App
    steps:
    - task: Maven@4
      displayName: 'Build OdontofastPipeline'
      inputs:
        mavenPomFile: '$(mavenPOMFile)'
        testRunTitle: 'Testes Unitários'
        jdkVersionOption: 1.21
    - task: CopyFiles@2
      displayName: 'Copiar a aplicação OdontofastPipeline'
      inputs:
        SourceFolder: '$(system.defaultworkingdirectory)'
        Contents: 'target/*.jar'
        TargetFolder: '$(build.artifactstagingdirectory)'
    - task: PublishBuildArtifacts@1
      displayName: 'Publicar artefato do Build OdontofastPipeline'
      inputs:
        PathtoPublish: '$(build.artifactstagingdirectory)'
        ArtifactName: $(nome-artefato)

# Estágio 3: Deploy da aplicação no Azure App Service
- stage: DeployApp
  dependsOn: CriarInfra
  variables:
    SQL_FQDN: $[ stageDependencies.CriarInfra.criaWebApp.outputs['infraStep.SQL_FQDN'] ]
  jobs:
  - job: DeployWebApp
    displayName: Deploy no Serviço de Aplicativo
    steps:
    - task: DownloadBuildArtifacts@1
      displayName: 'Baixar artefato gerado'
      inputs:
        buildType: 'current'
        downloadType: 'specific'
        downloadPath: '$(System.DefaultWorkingDirectory)'
    - task: AzureRmWebAppDeployment@4
      displayName: 'Deploy OdontofastPipeline'
      inputs:
        azureSubscription: 'MyAzureSubscription'
        appType: 'webApp'
        WebAppName: $(app-name)
        packageForLinux: '$(System.DefaultWorkingDirectory)/$(nome-artefato)/target/*.jar'
    - task: AzureCLI@2
      displayName: 'Configurar Banco de Dados SQL Server e Desabilitar RabbitMQ'
      inputs:
        azureSubscription: 'MyAzureSubscription'
        scriptType: 'bash'
        scriptLocation: 'inlineScript'
        inlineScript: |
          echo "Configurando conexão com SQL Server: $(sql-server-name).database.windows.net"
          az webapp config appsettings set --resource-group $(resourceGroup) --name $(app-name) --settings \
            SPRING_DATASOURCE_URL="jdbc:sqlserver://$(sql-server-name).database.windows.net:1433;database=$(sql-db-name);encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;" \
            SPRING_DATASOURCE_USERNAME="$(sql-admin-user)" \
            SPRING_DATASOURCE_PASSWORD="$(sql-admin-password)" \
            SPRING_DATASOURCE_DRIVER_CLASS_NAME="com.microsoft.sqlserver.jdbc.SQLServerDriver" \
            SPRING_JPA_DATABASE_PLATFORM="org.hibernate.dialect.SQLServer2012Dialect" \
            SPRING_JPA_HIBERNATE_DDL_AUTO="update" \
            SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL="true" \
            SPRING_JPA_SHOW_SQL="true" \
            SPRING_RABBITMQ_LISTENER_SIMPLE_AUTO_STARTUP="false" \
            SPRING_RABBITMQ_LISTENER_DIRECT_AUTO_STARTUP="false" \
            SPRING_RABBITMQ_LISTENER_SIMPLE_RETRY_ENABLED="false" \
            SPRING_RABBITMQ_LISTENER_DIRECT_RETRY_ENABLED="false" \
            SPRING_RABBITMQ_TEMPLATE_RETRY_ENABLED="false"
    - task: AzureCLI@2
      displayName: 'Reiniciar aplicação após configuração'
      inputs:
        azureSubscription: 'MyAzureSubscription'
        scriptType: 'bash'
        scriptLocation: 'inlineScript'
        inlineScript: |
          az webapp restart -g $(resourceGroup) -n $(app-name)
          echo "Aguardando inicialização da aplicação..."
          sleep 30
