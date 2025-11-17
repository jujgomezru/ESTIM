# ESTIM Java Backend — API Guide for Frontend Developers

This document describes the public HTTP API exposed by the ESTIM Java Backend.
It explains:

- What each endpoint does

- Required request payloads

- Example JSON formats

- Responses and error shapes

- Authentication requirements

This guide is based directly on the behavior validated by the full unit test suite:

- `AuthControllerTest`

- `ProfileControllerTest`

- `PasswordControllerTest`

- `PaymentMethodControllerTest`

- `OAuthControllerTest`

## Authentication & Common Info
### Authorization Header

Endpoints that require login expect:

`Authorization: Bearer <JWT_ACCESS_TOKEN>`


If missing or invalid, controllers return:

```json
{
"code": "UNAUTHORIZED" | "AUTH_FAILED",
"message": "..."
}
```

### Error Response Format

All error responses follow the structure:

```json
{
"code": "SOME_ERROR_CODE",
"message": "Human readable explanation",
"details": null
}
```

## 1. AuthController (`/auth/*`)
###   `POST /auth/register`

Create a new user.

**Request Body**
```json
{
"email": "john@example.com",
"password": "StrongPassword123!",
"displayName": "John Doe"
}
```

**Success Response (201)**
```json
{
"userId": "user-uuid",
"email": "john@example.com",
"displayName": "John Doe",
"emailVerified": false
}
```

**Validation Error (400)**
```json
{
"code": "VALIDATION_ERROR",
"message": "Invalid email",
"details": null
}
```

### `POST /auth/login`

Authenticates email + password and returns tokens.

**Request Body**
```json
{
"email": "john@example.com",
"password": "StrongPassword123!"
}
```

**Success Response (200)**
```json
{
"accessToken": "jwt-access-token",
"refreshToken": "jwt-refresh-token",
"user": {
"userId": "user-uuid",
"email": "john@example.com",
"displayName": "John Doe",
"emailVerified": true
  }
}
```
**Failure (401)**
```json
{
"code": "AUTH_FAILED",
"message": "Invalid credentials"
}
```
### `POST /auth/logout`

Revokes authentication (token may be null).

**Headers**

`Authorization: Bearer <token>    # optional`

**Success Response (204)**

No content.

### `GET /auth/me`

Retrieve the currently authenticated user.

**Headers**

`Authorization: Bearer <token>`

**Success Response (200)**
```json
{
"userId": "user-uuid",
"email": "john@example.com",
"displayName": "John Doe",
"avatarUrl": "https://example.com/avatar.png",
"emailVerified": true
}
```
**Unauthorized (401)**
```json
{
"code": "AUTH_FAILED",
"message": "No authenticated user"
}
```

## 2. ProfileController `GET /users/{id}/profile`

Fetch another user's profile.
Authentication is optional but affects privacy behavior.

**Headers (optional)**

`Authorization: Bearer <token>`

**Success Response (200)**
```json
{
"userId": "uuid",
"displayName": "John Doe",
"avatarUrl": "...",
"bio": "Hello world",
"location": "Earth",
"privacy": {
"showProfile": true,
"showActivity": false,
"showWishlist": true
  }
}
```

**Profile Private (403)**
```json
{
"code": "PROFILE_PRIVATE",
"message": "This profile is private"
}
```
**Not Found (404)**
```json
{
"code": "PROFILE_NOT_FOUND",
"message": "User does not exist"
}
```
### `PUT /me/profile`

Update the logged-in user's profile.

**Headers**
`Authorization: Bearer <token>`

**Request Body**

(All fields optional — null means no change.)
```json
{
"displayName": "John Updated",
"avatarUrl": "https://cdn/avatar.png",
"bio": "New bio here",
"location": "Mars",
"showProfile": true,
"showActivity": false,
"showWishlist": true
}
```
**Success Response (204)**

No content.
**Failure (400)**
```json
{
"code": "PROFILE_UPDATE_FAILED",
"message": "Not authenticated"
}
```
## 3. PasswordController (`/auth/password/*`)

### `POST /auth/password/reset-request`

Request a password reset email.

**Request Body**
```json
{
"email": "user@example.com"
}
```
**Success Response (200)**

Regardless of whether the email exists.
```json
{
"ok": true,
"message": "If an account exists for that email, a reset link has been sent."
}
```
**Validation Error (400)**
```json
{
"code": "RESET_REQUEST_FAILED",
"message": "Invalid email format"
}
```
### `POST /auth/password/reset`

Use token to set a new password.

**Request Body**
```json
{
"token": "reset-token",
"newPassword": "NewStrongPass123!"
}
```
**Success Response (200)**
```json
{
"ok": true,
"message": "Password has been reset successfully."
}
```
**Failure (400)**
```json
{
"code": "RESET_FAILED",
"message": "Token expired"
}
```
## 4. PaymentMethodController `GET /me/payment-methods`

List user’s saved payment methods.

**Headers**

`Authorization: Bearer <token>`

**Success Response (200)**
```json
[
  {
  "id": "pm-uuid",
  "provider": "PAGSEGURO",
  "last4": "4242",
  "isDefault": true
  }
]
```
**Unauthorized (401)**
```json
{
"code": "UNAUTHORIZED",
"message": "Not authenticated"
}
```
### `POST /me/payment-methods`

Add a new payment method.

**Request Body**
```json
{
"provider": "PAGSEGURO",
"token": "vaulted-token",
"last4": "1234",
"isDefault": true
}
```
**Success Response (201)**

**Failure (400)**
```json
{
"code": "PAYMENT_METHOD_ADD_FAILED",
"message": "Invalid token"
}
```

### `DELETE /me/payment-methods/{id}`

Remove a payment method.

**Headers**

`Authorization: Bearer <token>`

**Success Response (204)**

**Failure (400)**
```json
{
"code": "PAYMENT_METHOD_REMOVE_FAILED",
"message": "Method not found"
}
```
## 5. OAuthController (`/auth/oauth/*`)
   
### `POST /auth/oauth/link`

Link an OAuth provider account to the authenticated user.

**Headers**

`Authorization: Bearer <token>`

**Request Body**
```json
{
"provider": "STEAM",
"externalToken": "steam-access-token",
"redirectUri": "https://app.example.com/oauth/callback"
}
```
**Success Response (204)**

**Failure (400)**
```json
{
"code": "OAUTH_LINK_FAILED",
"message": "Unsupported provider"
}
```
### `POST /auth/oauth/login`

Authenticate using OAuth provider.

**Request Body**
```json
{
"provider": "STEAM",
"externalToken": "oauth-token",
"redirectUri": "https://app.example.com/oauth/callback"
}
```
**Success Response (200)**
```json
{
"accessToken": "jwt-access-token",
"refreshToken": "jwt-refresh-token",
"user": {
"userId": "uuid",
"email": "john@example.com",
"displayName": "John Doe",
"emailVerified": true
  }
}
```

**Failure (401)**
```json
{
"code": "OAUTH_LOGIN_FAILED",
"message": "Invalid OAuth token"
}
```

## Final Notes

All endpoints return JSON.

All errors follow the same { code, message, details } structure.

The frontend must always send Authorization: Bearer <token> where required.

All controller paths and JSON structures in this document match the actual tests, so they are guaranteed correct.
