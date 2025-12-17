# 🎯 PROMPT PARA ACTUALIZAR EL FRONTEND

Copia y pega este prompt en tu asistente de IA o úsalo como guía:

---

## CONTEXTO

Tengo un frontend en React Native que consume una API REST en Spring Boot. El backend fue actualizado y ahora usa un formato de respuesta unificado y el campo de login cambió de `email` a `username`. Necesito que actualices el frontend para que funcione correctamente con la nueva API.

## URL BASE DEL BACKEND

```
https://emsx-bdiii.onrender.com
```

## CAMBIOS CRÍTICOS REQUERIDOS

### 1. **Endpoint de Login - CAMBIO IMPORTANTE**

**ANTES (INCORRECTO):**
```javascript
POST /api/auth/login
{
  "email": "admin@emsx.local",
  "password": "admin123"
}
```

**AHORA (CORRECTO):**
```javascript
POST /api/auth/login
{
  "username": "admin",  // ⚠️ CAMBIÓ DE 'email' A 'username'
  "password": "admin123"
}
```

### 2. **Formato de Respuesta Unificado**

**Todas las respuestas ahora tienen este formato:**
```typescript
{
  success: boolean;
  message: string;
  data: T | null;
  timestamp: string;
}
```

**Ejemplo de respuesta de login:**
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
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  },
  "timestamp": "2025-12-15T13:55:42.965369238"
}
```

### 3. **Estructura de Datos del Login**

**ANTES:**
```javascript
// Probablemente accedías así:
response.token
response.user
```

**AHORA:**
```javascript
// Debes acceder así:
response.data.token
response.data.user.id
response.data.user.username
response.data.user.email
```

## TAREAS ESPECÍFICAS

1. **Actualizar función de login:**
   - Cambiar el campo `email` por `username` en el request body
   - Actualizar el manejo de la respuesta para usar `response.data.token` y `response.data.user`
   - Verificar `response.success` antes de procesar los datos

2. **Actualizar almacenamiento del token:**
   - Guardar `response.data.token` (no `response.token`)
   - Guardar `response.data.user` completo

3. **Actualizar todas las peticiones HTTP:**
   - Asegurar que incluyan el header `Authorization: Bearer <token>`
   - Verificar `response.success` antes de procesar `response.data`

4. **Actualizar manejo de errores:**
   - Los errores ahora vienen en `response.message` cuando `response.success === false`
   - Verificar el status code HTTP (401, 500, etc.) para manejo específico

## ENDPOINTS DISPONIBLES

### Autenticación (NO requieren token):
- `POST /api/auth/login` - Login con `username` y `password`
- `POST /api/auth/register` - Registro con `firstname`, `lastname`, `username`, `email`, `password`
- `GET /api/auth/me` - Obtener usuario autenticado (requiere token)

### Recursos (REQUIEREN token en header):
- `GET /api/clientes` - Listar clientes
- `GET /api/productos` - Listar productos
- `GET /api/pedidos` - Listar pedidos
- `GET /api/facturas` - Listar facturas
- `GET /api/usuarios` - Listar usuarios

**Todos los endpoints de recursos devuelven:**
```json
{
  "success": true,
  "message": "Lista de [recurso] obtenida correctamente",
  "data": [...],
  "timestamp": "..."
}
```

## CÓDIGO DE EJEMPLO

```javascript
// Función de login actualizada
const login = async (username, password) => {
  try {
    const response = await fetch('https://emsx-bdiii.onrender.com/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username, // ⚠️ IMPORTANTE: 'username', no 'email'
        password: password,
      }),
    });

    const data = await response.json();

    // Verificar success antes de procesar
    if (data.success && data.data) {
      // Guardar token
      await AsyncStorage.setItem('token', data.data.token);
      // Guardar usuario
      await AsyncStorage.setItem('user', JSON.stringify(data.data.user));
      return { success: true, user: data.data.user, token: data.data.token };
    } else {
      return { success: false, message: data.message };
    }
  } catch (error) {
    return { success: false, message: 'Error de conexión' };
  }
};

// Función para peticiones autenticadas
const authenticatedFetch = async (endpoint, options = {}) => {
  const token = await AsyncStorage.getItem('token');
  
  return fetch(`https://emsx-bdiii.onrender.com${endpoint}`, {
    ...options,
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });
};

// Ejemplo de uso
const fetchClientes = async () => {
  try {
    const response = await authenticatedFetch('/api/clientes');
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

## CREDENCIALES DE PRUEBA

```
Username: admin
Password: admin123
```

## OBJETIVO

Actualizar el frontend para que:
1. ✅ Use `username` en lugar de `email` para el login
2. ✅ Maneje el formato de respuesta unificado (`success`, `message`, `data`, `timestamp`)
3. ✅ Acceda correctamente a `response.data.token` y `response.data.user`
4. ✅ Incluya el header `Authorization: Bearer <token>` en todas las peticiones protegidas
5. ✅ Maneje errores correctamente usando `response.success` y `response.message`

---

**IMPORTANTE:** No modifiques la estructura de la base de datos ni los endpoints existentes. Solo actualiza cómo el frontend consume la API.



