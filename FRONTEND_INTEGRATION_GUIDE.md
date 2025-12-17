# 📱 Guía de Integración Frontend - API EMSX

## 🔗 URL Base del Backend

```
https://emsx-bdiii.onrender.com
```

---

## 🔐 Endpoints de Autenticación

### 1. **POST /api/auth/login**

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**⚠️ IMPORTANTE:** El campo es `username`, NO `email`.

**Response Exitosa (200 OK):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@emsx.local"
    },
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzM0Mjg5NDQyLCJleHAiOjE3MzQzNzU4NDJ9..."
  },
  "timestamp": "2025-12-15T13:55:42.965369238"
}
```

**Response Error (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Credenciales incorrectas",
  "data": null,
  "timestamp": "2025-12-15T13:55:42.965369238"
}
```

**Response Error (500 Internal Server Error):**
```json
{
  "success": false,
  "message": "Error al autenticar: [mensaje de error]",
  "data": null,
  "timestamp": "2025-12-15T13:55:42.965369238"
}
```

---

### 2. **POST /api/auth/register**

**Request Body:**
```json
{
  "firstname": "Juan",
  "lastname": "Pérez",
  "username": "juanperez",
  "email": "juan@example.com",
  "password": "password123"
}
```

**Response Exitosa (200 OK):**
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 2,
    "username": "juanperez",
    "email": "juan@example.com",
    "firstname": "Juan",
    "lastname": "Pérez",
    "role": "ROLE_USER"
  },
  "timestamp": "2025-12-15T13:55:42.965369238"
}
```

**Response Error (400 Bad Request):**
```json
{
  "success": false,
  "message": "Error al registrar usuario: El username ya está en uso",
  "data": null,
  "timestamp": "2025-12-15T13:55:42.965369238"
}
```

---

### 3. **GET /api/auth/me**

**Headers Requeridos:**
```
Authorization: Bearer <token>
```

**Response Exitosa (200 OK):**
```json
{
  "success": true,
  "message": "Usuario autenticado",
  "data": {
    "id": 1,
    "firstname": "Admin",
    "lastname": "Root",
    "username": "admin",
    "email": "admin@emsx.local",
    "password": null,
    "usuarioRoles": [...]
  },
  "timestamp": "2025-12-15T13:55:42.965369238"
}
```

**Response Error (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Usuario no autenticado",
  "data": null,
  "timestamp": "2025-12-15T13:55:42.965369238"
}
```

---

## 📋 Endpoints de Recursos

### **Clientes**
- `GET /api/clientes` - Listar todos los clientes
- `GET /api/clientes/{id}` - Obtener cliente por ID
- `POST /api/clientes` - Crear cliente
- `PUT /api/clientes/{id}` - Actualizar cliente
- `DELETE /api/clientes/{id}` - Eliminar cliente

### **Productos**
- `GET /api/productos` - Listar todos los productos
- `GET /api/productos/{id}` - Obtener producto por ID
- `POST /api/productos` - Crear producto
- `PUT /api/productos/{id}` - Actualizar producto
- `DELETE /api/productos/{id}` - Eliminar producto

### **Pedidos**
- `GET /api/pedidos` - Listar todos los pedidos
- `GET /api/pedidos/{id}` - Obtener pedido por ID
- `POST /api/pedidos` - Crear pedido
- `PUT /api/pedidos/{id}` - Actualizar pedido
- `DELETE /api/pedidos/{id}` - Eliminar pedido

### **Facturas**
- `GET /api/facturas` - Listar todas las facturas
- `GET /api/facturas/{id}` - Obtener factura por ID
- `POST /api/facturas` - Crear factura
- `PUT /api/facturas/{id}` - Actualizar factura
- `DELETE /api/facturas/{id}` - Eliminar factura

### **Usuarios**
- `GET /api/usuarios` - Listar todos los usuarios
- `GET /api/usuarios/{id}` - Obtener usuario por ID

**⚠️ NOTA:** Todos los endpoints de recursos requieren autenticación JWT (excepto `/api/auth/**`).

---

## 🔑 Manejo del Token JWT

### **Almacenamiento:**
- Guardar el token después del login exitoso
- Usar `AsyncStorage` (React Native) o `localStorage` (React Web)

### **Envío en Requests:**
- Incluir el header `Authorization: Bearer <token>` en todas las peticiones protegidas
- El token expira en 24 horas

### **Manejo de Expiración:**
- Si recibes `401 Unauthorized`, el token puede estar expirado
- Redirigir al usuario al login

---

## 📝 Formato Unificado de Respuestas

**Todas las respuestas de la API siguen este formato:**

```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  timestamp: string; // ISO 8601 format
}
```

**Ejemplo de uso en TypeScript/JavaScript:**
```typescript
interface LoginResponse {
  user: {
    id: number;
    username: string;
    email: string;
  };
  token: string;
}

const response: ApiResponse<LoginResponse> = await fetch(...);
if (response.success) {
  // Guardar token
  await AsyncStorage.setItem('token', response.data.token);
  // Guardar usuario
  await AsyncStorage.setItem('user', JSON.stringify(response.data.user));
}
```

---

## 🚨 Cambios Importantes desde Versión Anterior

### 1. **Campo de Login Cambió de `email` a `username`**
```javascript
// ❌ ANTES (INCORRECTO)
{
  "email": "admin@emsx.local",
  "password": "admin123"
}

// ✅ AHORA (CORRECTO)
{
  "username": "admin",
  "password": "admin123"
}
```

### 2. **Formato de Respuesta Unificado**
- Todas las respuestas ahora tienen `success`, `message`, `data`, `timestamp`
- Verificar `response.success` antes de procesar `response.data`

### 3. **Estructura de Login Response**
```javascript
// La respuesta de login ahora tiene esta estructura:
{
  success: true,
  message: "Login exitoso",
  data: {
    user: {
      id: 1,
      username: "admin",
      email: "admin@emsx.local"
    },
    token: "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

---

## 💻 Ejemplo de Código React Native

```javascript
// services/api.js
const API_BASE_URL = 'https://emsx-bdiii.onrender.com';

export const login = async (username, password) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username, // ⚠️ IMPORTANTE: usar 'username', no 'email'
        password: password,
      }),
    });

    const data = await response.json();

    if (data.success && data.data) {
      // Guardar token
      await AsyncStorage.setItem('token', data.data.token);
      // Guardar información del usuario
      await AsyncStorage.setItem('user', JSON.stringify(data.data.user));
      return { success: true, data: data.data };
    } else {
      return { success: false, message: data.message };
    }
  } catch (error) {
    return { success: false, message: 'Error de conexión' };
  }
};

export const getAuthenticatedUser = async () => {
  try {
    const token = await AsyncStorage.getItem('token');
    
    const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    const data = await response.json();

    if (data.success && data.data) {
      return { success: true, user: data.data };
    } else {
      return { success: false, message: data.message };
    }
  } catch (error) {
    return { success: false, message: 'Error de conexión' };
  }
};

export const fetchClientes = async () => {
  try {
    const token = await AsyncStorage.getItem('token');
    
    const response = await fetch(`${API_BASE_URL}/api/clientes`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    const data = await response.json();

    if (data.success && data.data) {
      return { success: true, clientes: data.data };
    } else {
      return { success: false, message: data.message };
    }
  } catch (error) {
    return { success: false, message: 'Error de conexión' };
  }
};
```

---

## ✅ Checklist de Cambios Necesarios en el Frontend

- [ ] Cambiar campo de login de `email` a `username`
- [ ] Actualizar función de login para usar el nuevo formato de request
- [ ] Actualizar manejo de respuesta de login para usar `response.data.user` y `response.data.token`
- [ ] Verificar que todas las peticiones incluyan el header `Authorization: Bearer <token>`
- [ ] Actualizar manejo de errores para verificar `response.success` antes de procesar datos
- [ ] Actualizar tipos/interfaces TypeScript si los usas
- [ ] Probar login con credenciales: `username: "admin"`, `password: "admin123"`

---

## 🧪 Credenciales de Prueba

```
Username: admin
Password: admin123
```

---

## 📞 Soporte

Si encuentras algún problema, verifica:
1. Que la URL base sea correcta: `https://emsx-bdiii.onrender.com`
2. Que el campo de login sea `username` (no `email`)
3. Que el token JWT se esté enviando en el header `Authorization`
4. Que estés verificando `response.success` antes de procesar `response.data`



