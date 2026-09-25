# Marketplace

API REST de um marketplace simples, construída com Java 21 e Spring Boot.
Projeto de estudo: cada etapa foi feita passo a passo, com foco em entender as
decisões de arquitetura (camadas, DTOs, transações, tratamento de erros).

## Funcionalidades (v1)

- **Usuários**: CRUD completo. Um usuário pode ser comprador e/ou vendedor.
- **Produtos**: CRUD completo, com preço, estoque e vendedor. O vendedor é
  definido na criação e não muda no `PUT`.
- **Pedidos**: criação e listagem. Ao criar um pedido, o estoque de cada
  produto é validado e debitado.
  - Tudo ou nada: se um item do pedido falhar (ex.: estoque insuficiente),
    nenhum estoque é alterado (`@Transactional`).
  - O preço de cada item vem do banco, não da requisição.
  - A resposta traz o subtotal de cada item e o total do pedido.

## Tecnologias

- Java 21
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- PostgreSQL 16 (via Docker)
- Maven (com Maven Wrapper)

## Estrutura

```
src/main/java/com/ernandesventura/marketplace/
├── controller/   # Endpoints REST
├── service/      # Regras de negócio
├── repository/   # Acesso ao banco (Spring Data JPA)
├── model/        # Entidades JPA (Usuario, Produto, Pedido, ItemPedido)
├── dto/          # Records de entrada/saída (Request/Response)
└── exception/    # Exceções customizadas e handler global
```

## Como rodar

**Pré-requisitos:** Java 21 e Docker.

1. Suba o banco de dados:

   ```bash
   docker compose up -d
   ```

2. Rode a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

A API fica disponível em `http://localhost:8080`. As tabelas são criadas
automaticamente (`ddl-auto=update`).

## Endpoints

### Usuários

| Método | Rota             | Descrição          |
|--------|------------------|--------------------|
| GET    | `/usuarios`      | Lista todos        |
| GET    | `/usuarios/{id}` | Busca por ID       |
| POST   | `/usuarios`      | Cria um usuário    |
| PUT    | `/usuarios/{id}` | Atualiza           |
| DELETE | `/usuarios/{id}` | Remove             |

### Produtos

| Método | Rota             | Descrição          |
|--------|------------------|--------------------|
| GET    | `/produtos`      | Lista todos        |
| GET    | `/produtos/{id}` | Busca por ID       |
| POST   | `/produtos`      | Cria um produto    |
| PUT    | `/produtos/{id}` | Atualiza           |
| DELETE | `/produtos/{id}` | Remove             |

### Pedidos

| Método | Rota       | Descrição          |
|--------|------------|--------------------|
| GET    | `/pedidos` | Lista todos        |
| POST   | `/pedidos` | Cria um pedido     |

## Exemplos

**Criar usuário**

```http
POST /usuarios
Content-Type: application/json

{
  "nome": "Comprador Teste",
  "email": "comprador@teste.com"
}
```

**Criar produto**

```http
POST /produtos
Content-Type: application/json

{
  "nome": "Teclado",
  "descricao": "Teclado mecânico",
  "preco": 150.00,
  "quantidadeEstoque": 10,
  "vendedorId": 1
}
```

Resposta (`201 Created`) — o vendedor aparece só com id e nome:

```json
{
  "id": 1,
  "nome": "Teclado",
  "descricao": "Teclado mecânico",
  "preco": 150.00,
  "quantidadeEstoque": 10,
  "vendedorId": 1,
  "vendedorNome": "Vendedor Teste"
}
```

**Criar pedido**

```http
POST /pedidos
Content-Type: application/json

{
  "compradorId": 2,
  "itens": [
    { "produtoId": 1, "quantidade": 2 }
  ]
}
```

Resposta (`201 Created`):

```json
{
  "id": 1,
  "compradorId": 2,
  "compradorNome": "Comprador Teste",
  "dataPedido": "2026-09-24T20:00:00",
  "itens": [
    {
      "produtoId": 1,
      "produtoNome": "Teclado",
      "quantidade": 2,
      "precoUnitario": 150.00,
      "subtotal": 300.00
    }
  ],
  "total": 300.00
}
```

A pasta [`http/`](http/) tem roteiros de teste para cada recurso
([`usuarios.http`](http/usuarios.http), [`produtos.http`](http/produtos.http),
[`pedidos.http`](http/pedidos.http)), que podem ser executados pelo HTTP
Client do IntelliJ.

## Erros

Os erros de negócio são tratados por um handler global
(`GlobalExceptionHandler`) e retornam um corpo padrão:

```json
{
  "status": 409,
  "mensagem": "Estoque insuficiente para o produto: Mouse",
  "timestamp": "2026-09-24T20:00:00"
}
```

| Status | Quando                                                        |
|--------|---------------------------------------------------------------|
| 404    | Usuário, produto ou vendedor não encontrado (em qualquer rota) |
| 409    | Estoque insuficiente para o pedido                            |

## Próximos passos

- Autenticação/login com senha
- Categorias de produtos
- Frete
- Pagamento (integração com gateway)
- Avaliações de produtos e vendedores
- Imagens de produto
- IDs únicos com UUID em todas as entidades (após a autenticação)
