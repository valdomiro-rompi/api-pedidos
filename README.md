# API de Pedidos

Uma API REST robusta para gerenciamento de pedidos, desenvolvida com Spring Boot 3.2 e Java 17.

## 📋 Sobre o Projeto

Sistema backend responsável por processar e armazenar pedidos de clientes, oferecendo operações CRUD completas com validações de dados e arquitetura em camadas bem definida.

### ✨ Funcionalidades

- ✅ Criar novos pedidos
- ✅ Listar todos os pedidos
- ✅ Buscar pedido por ID
- ✅ **Fila de pedidos com Stack (LIFO)**
- ✅ **Processamento automático de fila**
- ✅ Validação robusta de dados
- ✅ Logs estruturados e auditoria
- ✅ Métricas e monitoramento
- ✅ Cobertura de testes > 80%

## 🚀 Tecnologias

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| Java | 17+ | Linguagem de programação |
| Spring Boot | 3.2.0 | Framework principal |
| Spring Data JPA | 3.2.0 | Persistência de dados |
| Maven | 3.6+ | Gerenciamento de dependências |
| H2 Database | - | Banco em memória (dev/test) |
| PostgreSQL | 14+ | Banco de dados (produção) |
| JUnit 5 | - | Framework de testes |
| Testcontainers | - | Testes de integração |
| JaCoCo | 0.8.10 | Cobertura de código |

## 📦 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- 2GB RAM disponível

## 🛠️ Instalação e Execução

### Clonando o repositório
```bash
git clone <url-do-repositorio>
cd api-pedidos
```

### Executando localmente
```bash
# Compilar o projeto
mvn clean compile

# Compilar o projeto ignorando 
mvn ./

# Executar testes
mvn test

# Executar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### Gerando JAR executável
```bash
mvn clean package
mvn clean package -DskipTests
java -jar target/api-pedidos-0.0.1-SNAPSHOT.jar
```

## 📚 Documentação da API

### Base URL
```
http://localhost:8080/api/pedidos
```

### 📮 Testando com Postman

A coleção do Postman está disponível em `docs/API_Pedidos.postman_collection.json` e inclui:

- **Endpoints básicos**: Criar, listar e buscar pedidos
- **Endpoints da fila**: Status, visualizar e processar fila
- **Cenário de teste completo**: 10 chamadas sequenciais para testar a fila LIFO
- **Testes de validação**: Casos de erro e validações

**Como importar:**
1. Abra o Postman
2. Clique em "Import"
3. Selecione o arquivo `docs/API_Pedidos.postman_collection.json`
4. Importe também o ambiente: `docs/API_Pedidos.postman_environment.json`

📖 **Guia detalhado**: [docs/POSTMAN_USAGE.md](docs/POSTMAN_USAGE.md)

### Endpoints Disponíveis

#### 📝 Criar Pedido
```http
POST /api/pedidos
Content-Type: application/json

{
    "nomeCliente": "João Silva",
    "descricao": "Pedido de notebook Dell",
    "valor": 3500.00
}
```

**Resposta (201 Created):**
```json
{
    "id": 1,
    "nomeCliente": "João Silva",
    "descricao": "Pedido de notebook Dell",
    "valor": 3500.00,
    "dataPedido": "2025-10-11T14:30:00"
}
```

#### 📋 Listar Todos os Pedidos
```http
GET /api/pedidos
```

**Resposta (200 OK):**
```json
[
    {
        "id": 1,
        "nomeCliente": "João Silva",
        "descricao": "Pedido de notebook Dell",
        "valor": 3500.00,
        "dataPedido": "2025-10-11T14:30:00"
    }
]
```

#### 🔍 Buscar Pedido por ID
```http
GET /api/pedidos/{id}
```

**Resposta (200 OK):**
```json
{
    "id": 1,
    "nomeCliente": "João Silva",
    "descricao": "Pedido de notebook Dell",
    "valor": 3500.00,
    "dataPedido": "2025-10-11T14:30:00"
}
```

### 📋 Endpoints da Fila de Pedidos

#### 📊 Status da Fila
```http
GET /api/pedidos/fila/status
```

**Resposta (200 OK):**
```json
{
    "tamanho": 3,
    "vazia": false
}
```

#### 👀 Visualizar Próximo Pedido
```http
GET /api/pedidos/fila/proximo
```

**Resposta (200 OK):** Retorna o próximo pedido sem removê-lo
**Resposta (204 No Content):** Fila vazia

#### ⚡ Processar Próximo Pedido
```http
POST /api/pedidos/fila/processar
```

**Resposta (200 OK):** Remove e retorna o próximo pedido (LIFO)
**Resposta (204 No Content):** Fila vazia

### Códigos de Status HTTP

| Código | Descrição |
|--------|-----------|
| 200 | Requisição bem-sucedida |
| 201 | Recurso criado com sucesso |
| 204 | Sem conteúdo (fila vazia) |
| 400 | Dados inválidos na requisição |
| 404 | Recurso não encontrado |
| 500 | Erro interno do servidor |

### 🎯 Comportamento da Fila (LIFO)

A fila de pedidos funciona como uma **Stack (pilha)**:
- **Último pedido criado** = **Primeiro a ser processado**
- **Primeiro pedido criado** = **Último a ser processado**
- **Adição automática**: Todo pedido criado é automaticamente adicionado à fila
- **Processamento manual**: Use os endpoints da fila para processar pedidos

## 🏗️ Arquitetura

O projeto segue o padrão de **Arquitetura em Camadas**:

```
┌─────────────────────────────────────┐
│         Cliente (HTTP)              │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Controller Layer               │
│   - PedidoController                │
│   - Validações de entrada           │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│       Service Layer                 │
│   - PedidoService                   │
│   - Lógica de negócio               │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Repository Layer               │
│   - PedidoRepository                │
│   - Spring Data JPA                 │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Banco de Dados                 │
│   - H2 / PostgreSQL                 │
└─────────────────────────────────────┘
```

## 🗄️ Modelo de Dados

### Entidade: Pedido

| Campo | Tipo | Restrições | Descrição |
|-------|------|------------|-----------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Identificador único |
| nome_cliente | VARCHAR(255) | NOT NULL | Nome do cliente |
| descricao | VARCHAR(500) | NOT NULL | Descrição do pedido |
| valor | DECIMAL(10,2) | NOT NULL, > 0 | Valor total do pedido |
| data_pedido | TIMESTAMP | NOT NULL | Data/hora de criação |

## ⚙️ Configuração

### Perfis de Ambiente

#### Desenvolvimento (padrão)
- Banco H2 em memória
- Console H2 habilitado: `http://localhost:8080/h2-console`
- Logs detalhados habilitados

#### Produção
- PostgreSQL
- Logs otimizados
- Métricas habilitadas

### Variáveis de Ambiente

```bash
# Banco de dados (produção)
DB_USERNAME=usuario_db
DB_PASSWORD=senha_db

# Perfil ativo
SPRING_PROFILES_ACTIVE=prod

# Porta do servidor
SERVER_PORT=8080
```

## 🧪 Testes

### Executando Testes
```bash
# Todos os testes
mvn test

# Testes com relatório de cobertura
mvn clean test jacoco:report

# Verificar cobertura mínima (80%)
mvn jacoco:check
```

### Estratégia de Testes
- **Testes Unitários**: JUnit 5 + Mockito
- **Testes de Integração**: Spring Boot Test + Testcontainers
- **Cobertura Mínima**: 80% (instruções) e 75% (branches)

### Relatório de Cobertura
Após executar os testes, o relatório estará disponível em:
`target/site/jacoco/index.html`

## 📊 Monitoramento

### Métricas (Spring Boot Actuator)
```bash
# Health check
GET http://localhost:8080/actuator/health

# Métricas da aplicação
GET http://localhost:8080/actuator/metrics

# Informações da aplicação
GET http://localhost:8080/actuator/info
```

### Logs
- **Framework**: SLF4J + Logback
- **Formato**: JSON estruturado
- **Níveis**: ERROR, WARN, INFO, DEBUG
- **Localização**: `logs/` directory

## 🚀 Deploy

### Docker (Recomendado)
```dockerfile
FROM openjdk:17-jre-slim
COPY target/api-pedidos-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Comandos Docker
```bash
# Build da imagem
docker build -t api-pedidos .

# Executar container
docker run -p 8080:8080 api-pedidos
```

## 📋 Validações de Negócio

### Regras de Validação
- **Nome do Cliente**: obrigatório, máximo 255 caracteres
- **Descrição**: obrigatória, máximo 500 caracteres  
- **Valor**: obrigatório, maior que R$ 0,01
- **Data do Pedido**: gerada automaticamente

## 🛣️ Roadmap

### Versão 1.0 (Atual)
- ✅ CRUD básico de pedidos
- ✅ **Fila de pedidos com Stack (LIFO)**
- ✅ **Endpoints de gerenciamento da fila**
- ✅ Validações de entrada
- ✅ Testes automatizados
- ✅ Logs e métricas

### Versão 1.1 (Próxima)
- ⬜ Atualização de pedidos (PUT)
- ⬜ Exclusão de pedidos (DELETE)
- ⬜ Paginação na listagem
- ⬜ Filtros de busca

### Versão 2.0 (Futuro)
- ⬜ Autenticação JWT
- ⬜ Documentação Swagger/OpenAPI
- ⬜ Cache Redis
- ⬜ Relacionamento com produtos

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código
- Seguir convenções Java/Spring Boot
- Cobertura de testes > 80%
- Documentação atualizada
- Commits semânticos

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 📞 Suporte

Para dúvidas ou suporte:
- 📧 Email: [email]
- 🐛 Issues: [GitHub Issues](link-para-issues)
- 📖 Documentação: [docs/](docs/)

---

**Desenvolvido com ❤️ usando Spring Boot**