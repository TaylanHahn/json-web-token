> Guia de Referência 📚 
## <img src="https://jwt.io/img/pic_logo.svg" width="25"> JWT (JSON Web Token)
### Autenticação Stateless & Segurança Moderna

Este repositório atua como uma **bússola técnica** para a implementação de segurança com tokens. Ele centraliza, de forma prática e organizada, desde a anatomia do token até as melhores práticas de segurança e integração com Spring Security. Um material projetado para desmistificar o fluxo de autenticação *stateless*.

### 🧠 Conceito

O **JWT** (RFC 7519) é um padrão aberto para transmitir informações de forma segura entre partes como um objeto JSON. Diferente da autenticação baseada em sessão (onde o servidor guarda o estado), o JWT é **stateless**: o próprio token contém todas as informações necessárias para identificar o usuário.

**Principais Vantagens:**
* **Escalabilidade:** O servidor não precisa consultar o banco de dados de sessões a cada requisição.
* **Mobile-Ready:** Ideal para APIs consumidas por apps móveis e SPAs (Single Page Applications).
* **Interoperabilidade:** Por ser JSON, é lido por qualquer linguagem (Java, JS, Python, etc.).

### 🧬 Anatomia do Token

Um JWT é composto por três partes separadas por pontos (`.`), formando a estrutura: `aaaaa.bbbbb.ccccc`

### 1. Header (Cabeçalho)
Define o tipo do token e o algoritmo de assinatura (ex: HMAC SHA256 ou RSA).

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
````

### 📦 2. Payload (Carga Útil)

Contém as Claims (afirmações) sobre a entidade (usuário) e metadados.

- 🏷️ **Registered Claims:** `sub` (subject), `iss` (issuer), `exp` (expiration).

- 🧩 **Public/Private Claims:** Dados customizados, como `role: "admin"`.

- 🧩 **JSON**

```json
{
  "sub": "1234567890",
  "name": "Dev Java",
  "admin": true,
  "iat": 1516239022
}
````

### ✍️ 3. Signature (Assinatura)

Garante que o token não foi alterado. É criada combinando o Header codificado + Payload codificado + uma Chave Secreta (que só o servidor conhece).

- 🧩 **Java**

```java
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
{
````

---

### 🔄 O Fluxo de Autenticação

- 🔑 **1. Login:** O cliente envia credenciais (usuário/senha) para o servidor.

- 🏗️ **2. Criação:** O servidor valida, cria o JWT (assinando com sua chave secreta) e devolve ao cliente.

- 💾 **3. Armazenamento:** O cliente guarda o JWT (ex: localStorage ou HttpOnly Cookie).

- 📩 **4. Requisição:** Em toda requisição subsequente, o cliente envia o JWT no cabeçalho:  
  `Authorization: Bearer <token>`

- ✅ **5. Validação:** O servidor verifica a assinatura do token. Se válida, libera o acesso sem consultar o banco.


### 🛠️ Implementação (Java & Spring Boot)

Exemplo prático utilizando a biblioteca java-jwt (Auth0) ou jjwt.

**🎟️ Gerando o Token**

```java
String token = Jwts.builder()
    .setSubject("usuario_email")
    .setIssuer("SuaAplicacao")
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
    .signWith(SignatureAlgorithm.HS256, "SuaChaveSecretaSuperSegura")
    .compact();
````

**🧪 Validando o Token (Filtro do Spring Security)**

````java
// Dentro do doFilterInternal
String token = recuperarToken(request);
if (jwtService.isTokenValido(token)) {
    String usuario = jwtService.obterUsuario(token);
    // Autentica o usuário no contexto do Spring
    SecurityContextHolder.getContext().setAuthentication(auth);
}
````

---

### 🛡️ Boas Práticas de Segurança

- 🔐 **HTTPS Sempre:** O token viaja em toda requisição; sem HTTPS ele pode ser interceptado por terceiros.

- 👁️ **Não exponha dados sensíveis:** O payload é apenas codificado em Base64, não criptografado. Qualquer pessoa que capturar o token consegue ler o conteúdo — nunca coloque senhas ou dados críticos.

- ⏱️ **Tempo de Expiração (exp):** Use expiração curta (ex: 15 min a 1h) e implemente Refresh Token para renovar o acesso sem exigir novo login.

- 🧬 **Algoritmo Forte:** Prefira RS256 (chave pública/privada) quando houver múltiplos serviços validando o token.

---

### 📚 Referências

- 🧪 **JWT.io Debugger** — Ferramenta oficial para decodificar, inspecionar e validar tokens JWT de forma segura para testes.
- 📜 **RFC 7519** — Especificação oficial do padrão JSON Web Token (JWT), definindo estrutura, claims e regras de validação.
