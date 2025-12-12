# Java Backend Architecture Overview

This document describes the architecture of the **ESTIM Java backend**, the flow of requests through its layers, and the rationale behind the chosen design.

The backend is implemented following a **layered, hexagonal, domain-centric** architecture, with clear separation between:

- **Presentation layer** (controllers + DTOs)
- **Application layer** (use cases / services / commands / queries)
- **Domain layer** (entities, value objects, domain events, repositories as ports)
- **Infrastructure layer** (adapters: persistence, security, OAuth, payment, messaging)
- **Event handlers layer** (application reactions to domain events)

---

## Architectural Style

The backend combines:

- **Layered architecture**  
  to separate concerns (presentation, application logic, domain model, infrastructure).

- **Hexagonal / Ports-and-Adapters principles**  
  where the domain and application layers define *interfaces (ports)* and infrastructure provides *implementations (adapters)*.

- **Domain-Driven Design (DDD) influences**  
  with explicit domain models (User, LibraryEntry, WishlistItem, etc.) and domain events (UserRegistered, GameAddedToLibrary, etc.).

This structure improves testability, maintainability, and makes it straightforward to add new delivery mechanisms (e.g., CLI, other APIs) or replace infrastructure (e.g., change database, payment provider, or message broker).

---

## Architecture overview

## Packages and Layers

### 2.1 Presentation Layer

**Packages:**

- `com.estim.javaapi.controllers`
- `com.estim.javaapi.presentation.*`

**Responsibility:**

- Expose HTTP endpoints.
- Map HTTP requests to application commands/queries.
- Map domain results to HTTP responses / DTOs.
- Handle HTTP-specific concerns (status codes, headers, validation).

**Key elements:**

- **Controllers** (`controllers`):
  - `AuthController`
  - `HealthController`
  - `LibraryController`
  - `OAuthController`
  - `PasswordController`
  - `PaymentMethodController`
  - `ProfileController`
  - `WishlistController`

  Each controller:
  - Receives HTTP requests.
  - Uses *presentation DTOs* for input/output.
  - Delegates to application services in `application.*`.

- **DTOs & mappers** (`presentation`):
  - `presentation.auth`  
    `LoginRequest`, `RegisterUserRequest`, `OAuthLoginRequest`, `OAuthLinkRequest`, `LoginResponse`, `RegisterUserResponse`, `CurrentUserResponse`, `AuthenticatedUserSummary`.
  - `presentation.library`  
    `LibraryEntryResponse`, `UpdateLibraryEntryRequest`, `LibraryMapper`.
  - `presentation.payment`  
    `PaymentMethodRequest`, `PaymentMethodResponse`.
  - `presentation.profile`  
    `UpdateUserProfileRequest`, `UserProfileResponse`, `PrivacySettingsResponse`.
  - `presentation.wishlist`  
    `WishlistItemRequest`, `WishlistItemResponse`, `WishlistMapper`.
  - `presentation.common`  
    `ErrorResponse`, `UserDtoMapper`, `PaymentMethodMapper`.

**Flow:**

1. Controller receives an HTTP request + JSON body.
2. Request JSON is deserialized into a *Request DTO* (e.g., `LoginRequest`).
3. Controller builds a **Command** or **Query** object (application layer).
4. Controller calls the appropriate application service.
5. Service returns domain objects or simple structures.
6. Controller converts them into *Response DTOs* using mappers.
7. Controller returns an HTTP response.

---

### 2.2 Application Layer

**Package:**

- `com.estim.javaapi.application.*`

This layer contains all **use cases / business workflows**, expressed in terms of **Commands**, **Queries**, and **Services**.

Subpackages:

- `application.auth`
- `application.library`
- `application.oauth`
- `application.password`
- `application.payment`
- `application.profile`
- `application.wishlist`
- `application.handlers`

#### 2.2.1 Commands, Queries, Services

Each subpackage is organized in a similar pattern:

- **Commands / Queries**  
  Simple immutable objects that carry the input for a use case.

  Examples:
  - `AuthenticateUserCommand`
  - `RegisterUserCommand`
  - `AddGameToLibraryCommand`
  - `RequestPasswordResetCommand`
  - `ResetPasswordCommand`
  - `AddToWishlistCommand`
  - `ListUserLibraryQuery`
  - `GetUserProfileQuery`
  - `ListWishlistForUserQuery`
  - `RemovePaymentMethodCommand`
  - `UpdateUserProfileCommand`

- **Services**  
  Orchestrate the use case logic, coordinate with repositories and domain logic.

  Examples:
  - `AuthenticateUserService`, `RegisterUserService`, `LogoutUserService`, `GetCurrentUserService`
  - `AddGameToLibraryService`, `ListUserLibraryService`
  - `LoginWithOAuthService`, `LinkOAuthAccountService`
  - `RequestPasswordResetService`, `ResetPasswordService`
  - `AddPaymentMethodService`, `RemovePaymentMethodService`, `ListPaymentMethodsService`
  - `GetUserProfileService`, `UpdateUserProfileService`
  - `AddToWishlistService`, `ListWishlistService`, `RemoveFromWishlistService`, `UpdateWishlistItemService`

- **Policies and helpers**
  - `DefaultPasswordPolicy`, `PasswordPolicy`, `PasswordHasher`
  - `TokenService`
  - `PasswordResetTokenGenerator`, `DefaultPasswordResetTokenGenerator`
  - `PaymentProviderClient` (port for external payment providers)

**Responsibilities:**

- Implement **application logic**, not HTTP or persistence details.
- Use domain objects (entities, value objects) and domain repositories (ports).
- Publish domain events via `DomainEventPublisher` when something relevant happens (e.g., user registered, game added to library).

#### 2.2.2 Application Event Handlers

**Package:**

- `application.handlers`

**Representative classes:**

- `AddGameToLibraryOnGamePurchased`
- `AuditLogger`
- `AuditLogOnUserLoggedIn`
- `CreateActivityOnDomainEvent`
- `SendPasswordResetEmailOnRequested`
- `SendWelcomeEmailOnUserRegistered`
- `UnlockAchievementOnGameEvent`
- `WishilistPricingNotificationHandler`
- `EmailSender` (port-like abstraction)

These classes subscribe to **domain events** and orchestrate **side-effect use cases**, for example:

- When `UserRegistered` is raised → `SendWelcomeEmailOnUserRegistered` may send an email.
- When `UserLoggedIn` is raised → `AuditLogOnUserLoggedIn` writes an audit log.
- When a pricing event occurs → `WishilistPricingNotificationHandler` notifies user.

They form a bridge between **pure domain events** and **external effects** via infrastructure (mail, audit logs, messaging).

---

### 2.3 Domain Layer

**Package:**

- `com.estim.javaapi.domain.*`

This is the **core of the system**, independent of HTTP, databases, frameworks, or messaging infrastructure.

Subpackages:

- `domain.common`
- `domain.user`
- `domain.library`
- `domain.wishlist`

#### 2.3.1 Common domain infrastructure

- `AbstractDomainEvent`
- `DomainEvent`
- `IntegrationEvent`
- `DomainEventPublisher` (port/interface)

Domain events model significant state changes at the domain level (e.g., `UserRegistered`, `GameAddedToWishlist`).

#### 2.3.2 User domain

**Entities, value objects, and enums:**

- `User`, `UserId`
- `UserProfile`, `PrivacySettings`, `UserStatus`
- `Email`
- `PasswordHash`
- `OAuthAccount`, `OauthAccountId`, `OAuthProvider`
- `PaymentMethod`, `PaymentMethodId`, `PaymentProvider`
- `PasswordResetToken`, `PasswordResetTokenId`

**Repositories (ports):**

- `UserRepository`
- `PaymentMethodRepository`
- `PasswordResetTokenRepository`

**Domain events:**

- `UserRegistered`
- `UserEmailVerified`
- `UserLoggedIn`
- `UserProfileUpdated`
- `UserBanned` / `UserUnbanned`
- `PaymentMethodAdded`
- `PaymentMethodRemoved`
- `PasswordResetRequested`
- `PasswordChanged`
- `OAuthAccountLinked`

These events are published through `DomainEventPublisher` and consumed by handlers in `application.handlers` and infrastructure components.

#### 2.3.3 Library domain

- `LibraryEntry`, `LibraryEntryId`
- `GameId` (value object representing a game)
- `LibraryRepository` (port)
- Events:
  - `GameAddedToLibrary`

#### 2.3.4 Wishlist domain

- `WishlistItem`
- `WishlistRepository` (port)

Events:

- `GameAddedToWishlist`
- `GameRemovedFromWishlist`
- `WishlistNotificationTriggered`

The domain layer guarantees invariants and encapsulates rules. Application services rely on these domain objects to enforce business constraints.

---

### 2.4 Infrastructure Layer

**Package:**

- `com.estim.javaapi.infrastructure.*`

Provides **technical adapters** that implement the ports defined in the domain and application layers:

Subpackages:

- `audit`
- `events`
- `mail`
- `oauth`
- `payment`
- `persistence`
- `security`

#### 2.4.1 Audit

- `ConsoleAuditLogger`  
  An audit logging implementation (e.g., logs to console). It can be wired to `AuditLogger` / event handlers.

#### 2.4.2 Events and Outbox

- `EventSerializer`
- `ExternalEventEnvelope`
- `OutboxEvent`
- `OutboxRepository`
- `OutboxEventPublisher`
- `KafkaDomainEventPublisher`
- `SimpleEventBus`

These classes implement:

- An **in-process event bus** (`SimpleEventBus`).
- An **Outbox pattern** for reliable publishing of events (`OutboxEvent`, `OutboxRepository`, `OutboxEventPublisher`).
- An adapter for **external message brokers** such as Kafka (`KafkaDomainEventPublisher`).

They adapt `DomainEventPublisher` (domain port) to actual transport technologies.

#### 2.4.3 Mail

- `ConsoleEmailSender`  
  An implementation of `EmailSender` that sends email-like notifications to the console (or logs). It encapsulates email delivery concerns outside the domain and application core.

#### 2.4.4 OAuth

- `GoogleOAuthClient`
- `SteamOAuthClient`
- `OAuthUserInfo`

These classes provide concrete integrations with external OAuth providers.  
Application services in `application.oauth` call them via interfaces, preserving hexagonal boundaries.

#### 2.4.5 Payment

- `NoopPaymentProviderClient`
- `PagSeguroClient`

These are implementations of the `PaymentProviderClient` port defined in the application layer. Different adapters can be plugged depending on environment (e.g., sandbox, test, production).

#### 2.4.6 Persistence

**Packages:**
- `infrastructure.persistence.library`
- `infrastructure.persistence.user`
- `infrastructure.persistence.wishlist`

Each subpackage typically contains:

- JPA entities (`*JpaEntity`)
- Spring Data repositories (`*JpaRepository`)
- Mappers to translate between Domain entities and JPA entities (`UserMapper`, etc.)
- Implementations of domain repositories (ports):

  - `LibraryRepositoryImpl` implements `LibraryRepository`
  - `UserRepositoryImpl` implements `UserRepository`
  - `WishlistRepositoryImpl` implements `WishlistRepository`
  - `PasswordResetTokenRepositoryImpl` implements `PasswordResetTokenRepository`

This is where **PostgreSQL persistence** is realized, without leaking JPA or SQL into the domain or application layers.

#### 2.4.7 Security

- `AuthenticatedUser`
- `BCryptPasswordHasher`
- `CorsConfig`
- `JwtAuthenticationFilter`
- `JwtAuthenticationProvider`
- `JwtTokenService`
- `SecurityConfig`
- `SecurityContext`

These classes form the integration with **Spring Security**, JWT parsing/validation, and password hashing, while exposing higher-level abstractions used by application and presentation layers.

- `BCryptPasswordHasher` is an implementation of the `PasswordHasher` abstraction from the application layer.
- `JwtTokenService` implements token operations used by `TokenService` / controllers.

---

## 3. API REST Endpoints

### 3.1 Authentication

In the `AuthController` class, we handle the endpoints related to the standard authentication process for our system (Email and password). The commont base path for all endpoints is `/auth`.

1. Create a new account

**POST `/auth/register`**

**Requested body:**
```json
{
  "email": "user@example.com",
  "password": "SafePassword1!",
  "displayName": "testUser"
}
```
**Responses**:

**201 Created**:
```json
{
  "userId": "f3e5d4a2-7c90-4f34-9b89-5c82033cf5c2",
  "email": "user@example.com",
  "displayName": "userTest",
  "createdAt": "2025-12-11T21:30:00Z"
}
```

**400 Bad Request**
```json
{
  "code": "VALIDATION_ERROR",
  "message": "Email is already in use",
  "details": null
}
```
2. Log into an existing account

**POST `/auth/login`**

Authenticate an existing user with email and password

**Request body:**

```json
{
  "email": "user@example.com",
  "password": "PlainTextPassword123!"
}
```

**Responses:**

**200 Ok**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1Yzk0YTZiYS04ZjcxLTQ...",
  "user": {
    "userId": "f3e5d4a2-7c90-4f34-9b89-5c82033cf5c2",
    "email": "user@example.com",
    "displayName": "testUser",
    "emailVerified": "false"
  }
}
```

**401 Unauthorized**
```json
{
  "code": "AUTH_FAILED",
  "message": "Invalid credentials",
  "details": null
}

```
3. Log out of current account

**POST `/auth/logout`**

Invalidate current access token (Logout is provided by `LogOutUserService`)

**Request:**

```json
"Authorization: Bearer "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...""
```

**Responses:**

**204 No Content**

Every request finishes without returning a body.

4. Check user profile

**GET `/auth/me`**

Returns user profile (not fully implemented)

**Request:**

```json
"Authorization: Bearer "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...""
"Origin: "https://jujgomezru.github.io""
```

**Responses:**

**200 OK**
```json
{
  "userId": "f3e5d4a2-7c90-4f34-9b89-5c82033cf5c2",
  "email": "user@example.com",
  "displayName": "Test User",
  "avatarUrl": null,
  "emailVerified": false
}
```

**403 Forbidden**

No body returned

### OAuth Authentication

In the `OAuthController` class we handle the endpoints related to the Google OAuth authentication service. All access points use an `external token` from the provider, that gets verified by the backend.

The commont base path for all endpoints is `/auth/oauth`

1. Link an OAuth to the current user (not fully implemented)

**POST `/auth/oauth/link`**

**Request:**

```json
{
  "provider": "GOOGLE",
  "externalToken": "ya29.a0AfB_byEXTERNAL_OAUTH_TOKEN",
  "redirectUri": "http://localhost:3000/oauth/callback"
}
```

**Responses:**

**204 No Content**

No body is returned

**400 Bad Request**

```json
{
  "code": "OAUTH_LINK_FAILED",
  "message": "Invalid Authorization header format",
  "details": null
}
```

2. Log in with an OAuth provider

**POST `/auth/oauth/login`**

**Request:**

```json
{
  "provider": "GOOGLE",
  "externalToken": "ya29.a0AfB_byEXTERNAL_OAUTH_TOKEN"
}
```

**Responses:**

**200 OK**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1Yzk0YTZiYS04ZjcxLTQ...",
  "user": {
    "userId": "f3e5d4a2-7c90-4f34-9b89-5c82033cf5c2",
    "email": "user@example.com",
    "displayName": "testUser"
  }
}
```
**401 Unauthorized**

```json
{
  "code": "OAUTH_LOGIN_FAILED",
  "message": "Failed to validate OAuth token for login",
  "details": null
}
```


3. Register a new user using an OAuth provider

**POST `/auth/oauth/register`**

**Request:**

```json
{
  "provider": "GOOGLE",
  "externalToken": "ya29.a0AfB_byEXTERNAL_OAUTH_TOKEN"
}

```

**Response:**

**200 OK**

```json
{
  "accessToken": "LOCAL_JWT_HERE",
  "refreshToken": "optional-refresh-token",
  "user": {
    "userId": "f3e5d4a2-7c90-4f34-9b89-5c82033cf5c2",
    "email": "user@example.com",
    "displayName": "testUser"
  }
}
```
**400 Bad Request**

```json
{
  "code": "OAUTH_REGISTER_FAILED",
  "message": "Missing OAuth access token",
  "details": null
}
```

### Library

In the `LibraryController` class we handle the verification and display of the library contents 

The commont base path for all endpoints is `me/library`

1. Get currently owned games

**GET `/me/library`**

**Request:**

```json
"Authorization: Bearer "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...""
```

**Response:**

**200 OK**

```json
[
  {
    "id":"83d8d8a1-dffa-4f70-abc2-3ff921fd1833",
    "gameId":"42ee5f63-2a25-42b1-a3b6-68efecbb2733",
    "gameTitle":"Cyberpunk Legends",
    "coverImageUrl":"https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400&h=300&fit=crop",
    "source":"seed-library",
    "addedAt":"2025-12-10T03:05:49.416863Z"
  }
  ,
  {
    "id":"4d1406e1-5ea6-4d8e-950a-ffcf55e26dc2",
    "gameId":"3c9f0495-b75a-4771-aae3-5f32d86bde9d",
    "gameTitle":"Medieval Kingdoms",
    "coverImageUrl":"https://images.unsplash.com/photo-1518709414768-a88981a4515d?w=400&h=300&fit=crop",
    "source":"seed-library",
    "addedAt":"2025-12-10T03:05:49.416863Z"
  }
]
```
**403 Unauthorized**

No body is returned

2. Add a game to library (purchase)

**POST `/me/library`**

**Requests:** 

```json
{
  "gameId": "9a6b0c88-24a4-4b61-a976-2bff8a35fa5a",
  "source": "PURCHASE"
}

```

**Responses:**

**201 Created**

```json
{
  "id":"f484b9be-4a73-4c9c-9754-a663acd42d14","gameId":"fa034c8a-3370-4b6c-8a32-aa14ae6be4c9",
  "gameTitle":null,
  "coverImageUrl":null,
  "source":"PURCHASE",
  "addedAt":"2025-12-12T06:26:39.416863Z"
}
```

**409 Conflict**

```json
{
  "code":"LIBRARY_ADD_FAILED",
  "message":"Game is already in user's library",
  "details":null
}
```

3. Update library entry (not implemented)

**PATCH `/me/library/{gameId}`**

**Requests:** 

```json
{
  "additionalPlayTimeMinutes": 30,
  "tags": ["completed", "favorite"],
  "status": "COMPLETED"
}

```

**Responses:**

**501 Not implemented"

```json
{
  "code":"LIBRARY_UPDATE_NOT_IMPLEMENTED",
  "message":"Updating library entries is not supported by the current schema. Extend the `libraries` table before implementing this service.",
  "details":null}

```

### Wishlist

In the `WishlistController` class we handle the verification and display of the wishlist contents 

The commont base path for all endpoints is `me/wishlist`

1. Get current wishlist

**POST `/me/wishlist`**

**Requests:**

```json
"Authorization: Bearer "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...""
```

**Responses:**

**200 OK**

```json
[
  {"gameId":"685eb61d-8769-4aee-8424-29502a82305d",
    "gameTitle":"Cube Quest",
    "coverImageUrl":null,
    "addedAt":"2025-12-10T03:05:49.416863Z",
    "notificationPreferences":{},
    "currentPrice":9.99
  },
  {"gameId":"88139cc9-3809-41ba-87ac-00bc0a4d02d0",      
    "gameTitle":"Space Odyssey",
    "coverImageUrl":"https://images.unsplash.com/photo-1614732414444-096e5f1122d5?w=400&h=300&fit=crop",
    "addedAt":"2025-12-10T03:05:49.416863Z",
    "notificationPreferences":{},
    "currentPrice":24.9
  }
]
```
**403 Forbidden**

No body is returned


2. Add a game to wishlist

**POST `/me/wishlist`**

**Requests:**

```json
{
  "gameId": "3f97e4d7-6158-4529-a1dd-e66f3d8f98c0",
  "notificationPreferences": {
    "priceDrop": true,
    "newPromotion": false
  }
}

```

**Responses:**

**201 Created**

```json
{
  "gameId":"3f97e4d7-6158-4529-a1dd-e66f3d8f98c0",
  "gameTitle":"Dark Chronicles",
  "coverImageUrl":"https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&h=300&fit=crop","addedAt":"2025-12-12T14:51:48.133799Z",
  "notificationPreferences":{},
  "currentPrice":4.99}
```
**403 Forbidden**

No body is returned

3. Delete a game from wishlist

**DELETE `/me/wishlist/{gameId}`**

**Requests:**

```json
{
  "gameId": "3f97e4d7-6158-4529-a1dd-e66f3d8f98c0",
  "notificationPreferences": {
    "priceDrop": true,
    "newPromotion": false
  }
}

```

**Responses:**

**204 No Content**

No body is returned

**403 Forbidden**

No body is returned

---

## SOLID application

The architecture and code development of this backend was done while trying to apply SOLID principles, in order to ensure a clean, readable and easy to extend architecture, that facilitates further development of the existing project for future ocassions and collaborators. While there are some SOLID rule breaks, due to design patterns and prioritizing development speed on some ocassions, here, we present examples of the general SOLID implementation on this project:

### Single Responsability Principle

This principle states that a class should only have one reason to change. Basically, every class should have one single and defined responsability. The main violations of this principle are the "super-classes", large chunks of code with multiple responsabilities. These "super-classes" are not well modularized, lead to high coupling and inflexible systems, with high level of dependencies that render the system vulnerable to failures of the "super-class", with large efforts to apply the necessary corrections.

Out of all the principles, this is the principle we applied the most on this project. Most classes don't exceed 200 lines of code, and they were design with a single responsability in mind, which matches the name of the class. 

In the application layer, we clearly divide the Services, Commands, Queries, Results, etcetera, to make sure that each step of the application flow is quickly encapsulated to its own logic. 




### Open/Closed Principle

This principle states that, at the time of writing a class, it should be done by considering that any new class should always remain open for extension, rather than open to modification directly. This indicates that any time that we need to add new functionality to existing code, we should do this by creating a new class, that extends, or gets composed by the original module, and trying to avoid internal modifications as much as possible. This is a good practice, since avoiding direct fixes prevents the development of bugs during the new feature introduction process. Also, applying this practice makes the code easy to reuse, since we can apply proven solutions to new modules, without resorting to change the building bricks for multiple implementations due to one new functionality.

Here, we present an example of implementation of this principle:

```java
@Service
public interface TokenService {

    String generateAccessToken(UserId userId);

    String generateRefreshToken(UserId userId);

    UserId parseUserIdFromAccessToken(String token);

    default void revokeRefreshToken(String refreshToken) {
    }

    default void revokeAllForUser(UserId userId) {
    }
}
```
This is a very short, general, atomic interface, defined to use tokens in the project. It contains the minimum amount of methods necessary to ensure every token has the same base functionality. There are multiple types of tokens. In this project, we use JWT to introduce an expiration logout due to inactivity:

```java
@Component
public class JwtTokenService implements TokenService {

    private final byte[] secretKeyBytes;
    private final SecretKey signingKey;   
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;

    public JwtTokenService(
        @Value("${security.jwt.secret:dev-secret-change-me}") String secret,
        @Value("${security.jwt.access-token-ttl:PT15M}") Duration accessTokenTtl,
        @Value("${security.jwt.refresh-token-ttl:P7D}") Duration refreshTokenTtl
    ) {
        this.secretKeyBytes = Objects.requireNonNull(secret, "secret must not be null")
            .getBytes(StandardCharsets.UTF_8);

        this.signingKey = Keys.hmacShaKeyFor(this.secretKeyBytes);

        this.accessTokenTtl = Objects.requireNonNull(accessTokenTtl, "accessTokenTtl must not be null");
        this.refreshTokenTtl = Objects.requireNonNull(refreshTokenTtl, "refreshTokenTtl must not be null");
    }

    @Override
    public String generateAccessToken(UserId userId) {
        return generateToken(userId, accessTokenTtl, "access");
    }

    @Override
    public String generateRefreshToken(UserId userId) {
        return generateToken(userId, refreshTokenTtl, "refresh");
    }

    private String generateToken(UserId userId, Duration ttl, String type) {
        Instant now = Instant.now();
        Instant expiry = now.plus(ttl);

        return Jwts.builder()
            .setSubject(userId.value().toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .claim("typ", type)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    @Override
    public UserId parseUserIdFromAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

            String subject = claims.getSubject();
            return new UserId(UUID.fromString(subject));

        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid or expired access token", ex);
        }
    }
}
```

As we can see, the `JwtTokenService` class extends the main interface `TokenService`. However, we don't need to change anything in `TokenService` to implement a modular JWT Token service, even if the specific logic of the JWT token is significantly more complex than the original interface. The JWT token can be used as a generic implementation for multiple domains of the system, and in the event we want to implement a new type of token, we can easily extend the currently functional `TokenService`, without having to modify it and more importantly, without affecting the already functional JWT Token.


### Liskov Sustitution Principle

This principle indicates that a program that requires instances of a specific class to function correctly, can receive instances of any of its respective subclasses, and remain fully functional. The main point of this principle, is that any subclass should share the generalities of the parent class, rather than breaking them. Otherwise, either the proposed parent class is not the correct starting point for the subclass in question, or we are introducing functionalities that break existing logic, instead of extending it. 

Let's look at a different example to study the application of the Liskov's Sustitution Principle in this project (although the previous example works as well, since it's perfectly possible to swap `TokenService`with `JwtTokenService` without breaking functionality).

```java
public interface PasswordHasher {

    PasswordHash hash(String rawPassword);

    boolean matches(String rawPassword, PasswordHash hash);
}

```

Here, we have an implementation of this interface:

```java
@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final int strength;
    public BCryptPasswordHasher(@Value("${bcrypt.strength:12}") int strength){
        this.strength = strength;
    }

    @Override
    public PasswordHash hash(String rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        String salt = BCrypt.gensalt(strength);
        String hashed = BCrypt.hashpw(rawPassword, salt);
        return new PasswordHash(hashed);
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash hash) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        Objects.requireNonNull(hash, "hash must not be null");
        return BCrypt.checkpw(rawPassword, hash.value());
    }
}
```

The class introduces additional functionality, as it uses the `BCrypt` library for the specific hashing strategy to implement the hash. However, it's use of the abstract methods of `PasswordHasher` does not break its intended functionality, and therefore, its usage in other classes can easily be swapped by the specific `BCryptPasswordHasher` class.

### Interface Segregation Principle

This principle indicates that a client should never be forced to implement methods they will not use. It makes sure that interfaces are easy to refactor and extend, without including optional methods that might cause issues with implementations, whether it means the optional methods use is ambiguous, or causing potential failures by an incorrect implementation of methods that are not relevant to the extension.

In this project, we attempted to apply this principle, by keeping interfaces short and self-contained to very specific tasks, and allow more complex services to compose of multiple independent interfaces, instead of creating one giant custom-made interface. This would be a bad practice, since these large interface might have methods that will be used frequently during the development process, but having to depend on the whole interface is risky and unnecessary. Here is an example of current implementation of this principle:

```java
@Service
public class RegisterUserService {

    private final UserRepository userRepository;
    private final PasswordPolicy passwordPolicy;
    private final PasswordHasher passwordHasher;
    private final DomainEventPublisher eventPublisher;

    public RegisterUserService(UserRepository userRepository,
                               PasswordPolicy passwordPolicy,
                               PasswordHasher passwordHasher,
                               DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordPolicy = Objects.requireNonNull(passwordPolicy);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public User register(RegisterUserCommand command) {
        validateCommand(command);

        Email email = Email.of(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        String displayName = command.displayName().trim();
        validateDisplayName(displayName);

        if (userRepository.existsByDisplayName(displayName)) {
            throw new IllegalArgumentException("Display name already in use");
        }

        if (!passwordPolicy.isValid(command.password())) {
            throw new IllegalArgumentException(passwordPolicy.description());
        }

        var hashedPassword = passwordHasher.hash(command.password());

        UserId id = new UserId(UUID.randomUUID());

        PrivacySettings privacySettings = new PrivacySettings(
            true,
            true,
            true
        );

        UserProfile initialProfile = new UserProfile(
            displayName,
            null,
            privacySettings
        );

        User user = User.register(
            id,
            email,
            hashedPassword,
            initialProfile
        );

        User saved = userRepository.save(user);

        eventPublisher.publishAll(saved.domainEvents());
        saved.clearDomainEvents();

        return saved;
    }

    private void validateCommand(RegisterUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        if (command.email() == null || command.email().isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }

        if (command.password() == null || command.password().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }

        if (command.displayName() == null || command.displayName().isBlank()) {
            throw new IllegalArgumentException("Display name must not be blank");
        }
    }

    private void validateDisplayName(String displayName) {
        if (displayName.contains(" ")) {
            throw new IllegalArgumentException("Display name must not contain spaces");
        }

        if (!displayName.matches("^[A-Za-z_][A-Za-z0-9_]*$")) {
            throw new IllegalArgumentException(
                "Display name must start with a letter or underscore and contain only letters, digits, or underscores"
            );
        }
    }
}
```
This Service implements 4 interfaces, and each one of them has separate responsabilities. Let's examine two of these in more detail:

```java
package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.user.PasswordHash;

public interface PasswordHasher {

    PasswordHash hash(String rawPassword);

    boolean matches(String rawPassword, PasswordHash hash);
}
```

```java
package com.estim.javaapi.application.auth;

public interface PasswordPolicy {

    boolean isValid(String rawPassword);

    default String description() {
        return "Password must meet security policy requirements.";
    }
}

```
Generally, the `RegisterUserService` class needs to include password logic in order to validate the password, and store it in the database. However, the segregation of interface shown here, divides the individual logic requirements of password handling for the service. The first interface is used as a port for a password hashing module, while the second interface is used to implement minimal safety password checks to determine if the password is approved for registration. While the Service requires this modules in the abstract, it doesn't need specific details for it to be applied correctly, that can be handled by the implementation of the used interfaces. Also, the interfaces shown have single responsabilities, and they can easily be used as atomic interfaces for other services.



### Dependency Inversion Principle

This principle indicates that high-level modules should not depend on low-level modules. Instead, they should depend on abstractions. This allows for lower coupling, and ensures to separate the high-end logic functionality from the implementation details needed in production. This way, the logic modules handle the general directives of the module, and how does it integrate with lower-level classes like database integrations, is handled specifically by the abstractions. This also allows to change low-level implementation if needed, for example, with a microservices architecture where the same high-level logic can be applied to different infrastructures.

In this project, we thought of this principle by the use of Repositories to handle business logic (like `domain.user.UserRepository`) and specific `Impl` classes to handle the implementation logic of said repositories (like `infrastructure.persistence.user.UserRepositoryImpl`). We can check this in more detail here:

```java
public interface UserRepository {

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    User save(User user);

    boolean existsByEmail(Email email);

    boolean existsByDisplayName(String displayName);

    Optional<User> findByOAuthProviderAndExternalId(OAuthProvider provider, String externalUserId);
}
```
We create an interface, intended to work as a port for the User information. This provides a clear and universal communication interface that is required by the domain in order to apply their business logic (for example, logging an user). This interface doesn't define logic, or knows about the way the user information is stored, it just makes sure whichever user petition request is received, is correctly handled by the backend.

```java
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
            .map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public boolean existsByDisplayName(String displayName) {
        return jpaRepository.existsByDisplayName(displayName);
    }

    @Override
    public Optional<User> findByOAuthProviderAndExternalId(OAuthProvider provider, String externalUserId) {
        return jpaRepository.findByOAuthProviderAndExternalUserId(provider, externalUserId)
            .map(mapper::toDomain);
    }
}
```
This class is an implementation of the previously defined Interface. This class makes sure the user petitions are correctly implemented by a service. In this particular case, it adapts the Repository to provide JPA implementation.

The original repository knows nothing of JPA infrastructure, and can easily be used for implementation for different services such as database queries, whereas the Implementation class only knows of how to adapt the interface to JPA, and doesn't require changes if the domain logic changes during the development process.

## Design patterns

In this project, we used two design patterns to facilitate the overall design of our Java backend:

### Adapter

The adapter design pattern, attempts to solve the communication problem between two interacting classes. Since two interacting classes may come from incompatible interfaces, they might be unable to share information using their respective interfaces. In order to allow separate modules or classes to communicate, rather than modifying the classes in question internally, we can just create a new "Adapter" class, that receives one interface, and returns a different interface. This way, we can not only ensure the interactivity of the modules, we can do this with a single, modular and extendible class that avoids the presence of bugs or failures during the communication process. In our example, this is represented with the OAuth module, as shown on the following illustration:

```java
@Component
public class RegisterWithOAuthService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;
    ...

    public RegisterWithOAuthService(
        UserRepository userRepository,
        OAuthAccountRepository oAuthAccountRepository,
        ...
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.oAuthAccountRepository = Objects.requireNonNull(oAuthAccountRepository);
        ...
    }

    public AuthenticationResult register(RegisterWithOAuthCommand command) {
        ...

        OAuthProvider provider = OAuthProvider.valueOf(command.provider().toUpperCase());
        ...

        OAuthUserInfo info = googleOAuthClient.fetchUserInfo(command.accessToken());
        String externalUserId = info.externalUserId();
        String emailStr = info.email();
        ...

        var existingAccountOpt =
            oAuthAccountRepository.findByProviderAndExternalUserId(provider, externalUserId);

        if (existingAccountOpt.isPresent()) {
            OAuthAccount existingAccount = existingAccountOpt.get();
            User user = userRepository.findById(existingAccount.userId())
                .orElseThrow(() -> new IllegalStateException("User not found for existing OAuth account"));

            ...

            return new AuthenticationResult(accessToken, refreshToken, user);
        }

        ...

        User user = User.register(
            userId,
            email,
            passwordHash,
            profile
        );

        OAuthAccount account = OAuthAccount.create(
            userId,
            provider,
            externalUserId,
            emailStr
        );

        user.linkOAuthAccount(account);

        userRepository.save(user);
        oAuthAccountRepository.save(account);

        ...

        return new AuthenticationResult(accessToken, refreshToken, user);
    }

    ...
}

```

This is the Client, and it was written by composing an abstraction, `OauthAccountRepository` which you can see as follows:

```java
public interface OAuthAccountRepository {

    Optional<OAuthAccount> findByProviderAndExternalUserId(
        OAuthProvider provider,
        String externalUserId
    );

    List<OAuthAccount> findByUserId(UserId userId);

    OAuthAccount save(OAuthAccount account);
}

```

Now, this interface is pretty extendable, easy to use, and functional to produce outputs for the OAuth registration client, but we can't confirm or rely on its compatibility with the infrastructure. As a matter of fact, it is definetly not compatible with the infrastructure repository, which we will use for the services:

```java
@Repository
public interface OAuthAccountJpaRepository extends JpaRepository<OAuthAccountJpaEntity, UUID> {

    Optional<OAuthAccountJpaEntity> findByProviderAndExternalUserId(
        OAuthProvider provider,
        String externalUserId
    );

    List<OAuthAccountJpaEntity> findByUserId(UUID userId);
}
```
While they might look similar, the service extends a `JpaRepository` directly, and can not interpret the original `OAutchAccountRepository` that we created the client with. To solve this, we used an Adapter class, that correctly maps our domain repository to the JPA infrastructure repository:
```java
@Repository
public class OAuthAccountRepositoryImpl implements OAuthAccountRepository {

    private final OAuthAccountJpaRepository jpa;
    private final UserJpaRepository userJpaRepository; 

    public OAuthAccountRepositoryImpl(
        OAuthAccountJpaRepository jpa,
        UserJpaRepository userJpaRepository
    ) {
        this.jpa = jpa;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<OAuthAccount> findByProviderAndExternalUserId(
        OAuthProvider provider,
        String externalUserId
    ) {
        return jpa.findByProviderAndExternalUserId(provider, externalUserId)
            .map(this::toDomain);
    }

    @Override
    public List<OAuthAccount> findByUserId(UserId userId) {
        return jpa.findByUserId(userId.value())
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public OAuthAccount save(OAuthAccount account) {
        OAuthAccountJpaEntity entity = toEntity(account);
        OAuthAccountJpaEntity saved = jpa.save(entity);
        return toDomain(saved);
    }


    private OAuthAccount toDomain(OAuthAccountJpaEntity e) {
        return new OAuthAccount(
            new OAuthAccountId(e.getId()),
            new UserId(e.getUser().getId()),
            e.getProvider(),
            e.getExternalUserId(),
            e.getEmail(),
            e.getLinkedAt()
        );
    }

    private OAuthAccountJpaEntity toEntity(OAuthAccount domain) {
        OAuthAccountJpaEntity e = new OAuthAccountJpaEntity();
        e.setId(domain.id().value());
        e.setUser(
            userJpaRepository.findById(domain.userId().value())
                .orElseThrow()
        );

        e.setProvider(domain.provider());
        e.setExternalUserId(domain.externalUserId());
        e.setEmail(domain.email());
        e.setLinkedAt(domain.linkedAt());
        return e;
    }
}
```

The adapter implements the original `OAuthAccountRepository`, but creates an instance of the JPA repository to ensure it's proper function as a port between the two.

Here we can see an equivalent UML diagram of the pattern:

### Strategy

We also had the strategy pattern in mind, although, due to the achieved complexity of the code, we didn't implement two or more strategies on any of its examples just yet. However, we did made the process of adding strategies to the code easier, by just extending the currently functional strategy, rather than modifying the existing code to adapt to a new strategy.

The strategy pattern consists on ensuring that different behaviours (strategies) can be correctly applied to the same context. The particular strength of Strategy, is that the context can delegate the particular execution to the "Strategy" class, which will instantiate the desired concrete strategy, without having to adapt to the execution of the particular strategy directly. This is desirable in case we want to leave the door open to multiple strategies in the future, as well as separating the strategy choosing logic from the strategy implementation logic.

We used this design pattern for the hashing module:

```java
@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final int strength;

    public BCryptPasswordHasher(@Value("${bcrypt.strength:12}") int strength){
        this.strength = strength;
    }

    @Override
    public PasswordHash hash(String rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        String salt = BCrypt.gensalt(strength);
        String hashed = BCrypt.hashpw(rawPassword, salt);
        return new PasswordHash(hashed);
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash hash) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        Objects.requireNonNull(hash, "hash must not be null");
        return BCrypt.checkpw(rawPassword, hash.value());
    }
}
```
This is a simple and functional strategy to hash the passwords of our users in our database, to expose their secrets to be exposed in the case of a data leak. We have `AuthenticateUserService` that may use this hashing service, but we might need to use a different hashing service at some point for the same service:

```java
package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserRepository;
import com.estim.javaapi.domain.user.events.UserLoggedIn;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class AuthenticateUserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final DomainEventPublisher eventPublisher;

    public AuthenticateUserService(UserRepository userRepository,
                                   PasswordHasher passwordHasher,
                                   TokenService tokenService,
                                   DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.tokenService = Objects.requireNonNull(tokenService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public AuthenticationResult authenticate(AuthenticateUserCommand command) {
        Email email = new Email(command.email());

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordHasher.matches(command.password(), user.passwordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!user.status().canLogin()) {
            throw new IllegalStateException("User is not allowed to log in");
        }

        String accessToken = tokenService.generateAccessToken(user.id());
        String refreshToken = tokenService.generateRefreshToken(user.id());

        eventPublisher.publish(new UserLoggedIn(user.id(), Instant.now()));

        return new AuthenticationResult(accessToken, refreshToken, user);
    }
}
```
Similar to our explanation of the Liskov Principle, this service would serve as the context, and it doesn't specificly requests for a `BCryptPasswordHasher`. Instead, any generic `PasswordHasher` implementation will do. We previously defined `PasswordHasher`, which is an interface that, in this case, serves as a common interface for every valid strategy for the Service. In the event we want to introduce a new Strategy, we don´t have to change the service. We only need to implement `PasswordHasher` to develop our new strategy:

Here is an UML diagram example the usage of this pattern:


---







