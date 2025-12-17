# Diagnóstico Backend EMSX (Render + React Native)

**Fecha de análisis:** 2024  
**Versión Spring Boot:** 3.5.4  
**Java:** 17  
**Despliegue:** Render (Docker)

---

## Estado General

El backend está **bien estructurado** con arquitectura limpia, separación de responsabilidades y uso de Spring Security con JWT. Sin embargo, se detectaron **varios problemas críticos** que pueden impedir la conexión desde React Native en producción.

**Estructura del proyecto:**
- ✅ Configuración Maven correcta
- ✅ Separación en capas (controllers, services, repositories)
- ✅ Uso de DTOs y mappers (MapStruct)
- ✅ Manejo de excepciones global
- ⚠️ **Problema detectado:** Estructura de directorios duplicada (`src/main` y `src/src/main`)

---

## 1. Puertos y Render

### ✅ **OK** - Configuración de puerto en `application.properties`

```properties
server.port=${PORT:8080}
```

**Análisis:**
- Correctamente configurado para usar la variable de entorno `PORT` de Render
- Fallback a 8080 si no existe la variable
- Render inyecta automáticamente `PORT` en el contenedor

### ❌ **ERROR CRÍTICO** - Dockerfile

```dockerfile
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Problema identificado:**
- El Dockerfile expone el puerto 8080 de forma hardcodeada
- **NO pasa la variable PORT al proceso Java**
- Render asigna un puerto dinámico (ej: 10000, 23456) pero la aplicación siempre escucha en 8080
- Esto causa que Render no pueda enrutar el tráfico correctamente

**Solución requerida:**
```dockerfile
EXPOSE $PORT
ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=$PORT app.jar"]
```

**Impacto:** 🔴 **ALTO** - Puede causar "Connection refused" o que el servicio no responda

---

## 2. CORS

### ✅ **OK** - Configuración global en `CorsConfig.java`

```java
config.setAllowedOriginPatterns(List.of("*"));
config.setAllowedOrigins(List.of("*"));
config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
config.setAllowedHeaders(List.of("*"));
config.setAllowCredentials(false);
```

**Análisis:**
- CORS está correctamente configurado a nivel global
- Permite todos los orígenes (`*`) - necesario para apps móviles
- Métodos HTTP permitidos incluyen todos los necesarios
- Headers permitidos: todos (`*`)

### ⚠️ **ADVERTENCIA** - Redundancia en controladores

**Problema detectado:**
- Múltiples controladores tienen `@CrossOrigin(origins = "*")` redundante
- Esto no causa errores pero es innecesario (CORS ya está configurado globalmente)

**Controladores con @CrossOrigin redundante:**
- `AuthController`
- `ClienteController`
- `ProductoController`
- `PedidoController`
- `FacturaController`
- `UsuarioController`
- `DashboardController`

**Impacto:** 🟡 **BAJO** - No causa problemas, solo redundancia

### ⚠️ **POSIBLE PROBLEMA** - Credenciales deshabilitadas

```java
config.setAllowCredentials(false);
```

**Análisis:**
- Cuando `allowCredentials = false` y `allowedOrigins = "*"`, puede haber problemas con algunos navegadores/apps
- Para React Native, esto generalmente no es un problema ya que no envía cookies por defecto
- **Sin embargo**, si el frontend necesita enviar cookies o usar credenciales, esto causará errores

**Impacto:** 🟡 **MEDIO** - Solo si el frontend requiere credenciales

---

## 3. Seguridad (JWT / Spring Security)

### ✅ **OK** - Configuración de Spring Security

**Rutas públicas (permitAll):**
```java
.requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", 
                 "/swagger-ui.html", "/actuator/health", "/error").permitAll()
```

**Rutas protegidas:**
- Todas las demás rutas requieren autenticación JWT
- El token debe enviarse en el header: `Authorization: Bearer <token>`

### ✅ **OK** - Filtro JWT

El `JwtAuthenticationFilter` está correctamente implementado:
- Extrae el token del header `Authorization`
- Valida el token antes de permitir acceso
- Si no hay token, permite pasar (para rutas públicas)

### ⚠️ **ADVERTENCIA** - PasswordEncoder inseguro

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
}
```

**Problema crítico de seguridad:**
- Las contraseñas se almacenan en **texto plano**
- **NO usar en producción**
- Debe cambiarse a `BCryptPasswordEncoder`

**Impacto:** 🔴 **CRÍTICO** - Problema de seguridad, pero no afecta la conectividad

### ✅ **OK** - JWT Service

- Carga `JWT_SECRET` desde variables de entorno
- Soporta Base64 y texto plano
- Extiende claves cortas a 64 bytes (requerido para HS512)
- Valida tokens correctamente

---

## 4. Endpoints Disponibles

### **URL Base en Render:**
```
https://emsx-backend.onrender.com
```
*(Nota: Verificar el nombre real del servicio en Render)*

### **Endpoints Públicos (NO requieren JWT):**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Registro de nuevo usuario |
| `POST` | `/api/auth/login` | Login (retorna JWT) |
| `GET` | `/api/auth/me` | Obtener usuario autenticado (requiere JWT) |
| `GET` | `/v3/api-docs/**` | Documentación OpenAPI |
| `GET` | `/swagger-ui/**` | Interfaz Swagger UI |
| `GET` | `/actuator/health` | Health check |

### **Endpoints Protegidos (REQUIEREN JWT):**

#### **Empleados**
- `GET /api/employees` - Listar todos
- `GET /api/employees/{id}` - Obtener por ID
- `POST /api/employees` - Crear
- `PUT /api/employees/{id}` - Actualizar
- `DELETE /api/employees/{id}` - Eliminar

#### **Departamentos**
- `GET /api/departments` - Listar todos
- `GET /api/departments/{id}` - Obtener por ID
- `POST /api/departments` - Crear
- `PUT /api/departments/{id}` - Actualizar
- `DELETE /api/departments/{id}` - Eliminar

#### **Habilidades (Skills)**
- `GET /api/skills` - Listar todas
- `GET /api/skills/{id}` - Obtener por ID
- `POST /api/skills` - Crear
- `PUT /api/skills/{id}` - Actualizar
- `DELETE /api/skills/{id}` - Eliminar

#### **Relaciones Empleado-Habilidad**
- `POST /api/employee-skills/assign` - Asignar habilidad
- `DELETE /api/employee-skills/remove` - Remover habilidad
- `GET /api/employee-skills/employee/{employeeId}` - Habilidades de un empleado
- `GET /api/employee-skills/skill/{skillId}` - Empleados con una habilidad

#### **Dependientes**
- `GET /api/dependents` - Listar todos
- `GET /api/dependents/employee/{employeeId}` - Por empleado
- `POST /api/dependents` - Crear
- `DELETE /api/dependents/{id}` - Eliminar

#### **Clientes**
- `GET /api/clientes` - Listar todos
- `GET /api/clientes/{id}` - Obtener por ID
- `POST /api/clientes` - Crear
- `PUT /api/clientes/{id}` - Actualizar
- `DELETE /api/clientes/{id}` - Eliminar

#### **Productos**
- `GET /api/productos` - Listar todos
- `GET /api/productos/{id}` - Obtener por ID
- `POST /api/productos` - Crear
- `PUT /api/productos/{id}` - Actualizar
- `DELETE /api/productos/{id}` - Eliminar

#### **Pedidos**
- `GET /api/pedidos` - Listar todos
- `GET /api/pedidos/{id}` - Obtener por ID
- `POST /api/pedidos` - Crear
- `PUT /api/pedidos/{id}` - Actualizar
- `DELETE /api/pedidos/{id}` - Eliminar

#### **Facturas**
- `GET /api/facturas` - Listar todas
- `GET /api/facturas/{id}` - Obtener por ID
- `POST /api/facturas` - Crear
- `PUT /api/facturas/{id}` - Actualizar
- `DELETE /api/facturas/{id}` - Eliminar

#### **Usuarios**
- `GET /api/usuarios` - Listar todos
- `GET /api/usuarios/{id}` - Obtener por ID

#### **Dashboard**
- `GET /api/dashboard/stats` - Estadísticas del dashboard

---

## 5. HTTPS

### ✅ **OK** - Render maneja HTTPS automáticamente

**Análisis:**
- Render proporciona HTTPS automáticamente para todos los servicios web
- No se requiere configuración adicional en el backend
- El certificado SSL es gestionado por Render

### ⚠️ **ADVERTENCIA** - React Native y HTTPS

**Problemas potenciales:**

1. **Android Network Security Config:**
   - Android 9+ bloquea HTTP por defecto
   - Si el frontend intenta usar HTTP en lugar de HTTPS, fallará
   - **Solución:** Asegurar que React Native use HTTPS en producción

2. **Certificados SSL:**
   - Render usa certificados válidos, no debería haber problemas
   - Si hay problemas de certificado, verificar configuración de red en Android

**Impacto:** 🟡 **MEDIO** - Solo si el frontend no está configurado para HTTPS

---

## 6. Variables de Entorno

### **Variables Requeridas en Render:**

| Variable | Descripción | Ejemplo | Estado |
|----------|-------------|---------|--------|
| `PORT` | Puerto asignado por Render | `10000` | ✅ Automática (Render) |
| `SPRING_DATASOURCE_URL` | URL de conexión PostgreSQL | `jdbc:postgresql://dpg-xxx:5432/emsx` | ⚠️ **REQUERIDA** |
| `SPRING_DATASOURCE_USERNAME` | Usuario de PostgreSQL | `emsx_user` | ⚠️ **REQUERIDA** |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de PostgreSQL | `password123` | ⚠️ **REQUERIDA** |
| `JWT_SECRET` | Clave secreta para JWT (mínimo 64 caracteres) | `openssl rand -base64 64` | ⚠️ **REQUERIDA** |

### **Verificación en Render:**

1. Ir a Dashboard → Servicio → Environment
2. Verificar que todas las variables estén configuradas
3. **CRÍTICO:** `JWT_SECRET` debe tener al menos 64 caracteres

### **Valores por defecto (NO usar en producción):**

```properties
jwt.secret=${JWT_SECRET:default-secret-key-change-in-production-minimum-64-characters-long-for-hs512-security-please-change-this}
```

**Impacto:** 🔴 **ALTO** - Si faltan variables, la aplicación no iniciará o funcionará incorrectamente

---

## 7. Errores Potenciales

### **"Network request failed" (React Native)**

**Causas posibles:**

1. ❌ **Dockerfile no pasa PORT al proceso Java**
   - **Solución:** Modificar Dockerfile para usar `$PORT`

2. ❌ **URL incorrecta en React Native**
   - Verificar que use HTTPS, no HTTP
   - Verificar que la URL base sea correcta (sin puerto)

3. ❌ **Servicio no está levantado en Render**
   - Verificar logs en Render Dashboard
   - Verificar que el build fue exitoso

4. ❌ **Timeout de red**
   - Render puede tardar en responder en el plan gratuito
   - Considerar aumentar timeout en React Native

### **"403 Forbidden"**

**Causas posibles:**

1. ❌ **Ruta protegida sin JWT**
   - Verificar que el token se envía en header `Authorization: Bearer <token>`
   - Verificar que el token no haya expirado (24 horas)

2. ❌ **Token inválido o malformado**
   - Verificar formato: `Bearer <token>` (con espacio)
   - Verificar que `JWT_SECRET` sea el mismo usado para generar el token

3. ❌ **Ruta no está en permitAll**
   - Verificar que `/api/auth/login` y `/api/auth/register` estén permitidas

### **"Connection refused"**

**Causas posibles:**

1. ❌ **Dockerfile no usa PORT dinámico**
   - **CRÍTICO:** El Dockerfile actual no pasa PORT al proceso Java
   - Render asigna puerto dinámico pero la app escucha en 8080

2. ❌ **Servicio no está corriendo**
   - Verificar logs en Render
   - Verificar que el contenedor Docker se inició correctamente

3. ❌ **Firewall o red**
   - Verificar configuración de red en Android
   - Verificar permisos de internet en AndroidManifest.xml

### **"401 Unauthorized"**

**Causas posibles:**

1. ❌ **Credenciales incorrectas en login**
   - Verificar usuario/contraseña
   - Verificar que el usuario existe en la base de datos

2. ❌ **Token no enviado**
   - Verificar que el header `Authorization` esté presente
   - Verificar formato: `Bearer <token>`

3. ❌ **Token expirado**
   - Los tokens expiran en 24 horas
   - Implementar refresh token o re-login

### **"500 Internal Server Error"**

**Causas posibles:**

1. ❌ **Error de conexión a base de datos**
   - Verificar `SPRING_DATASOURCE_URL`, `USERNAME`, `PASSWORD`
   - Verificar que la base de datos PostgreSQL esté activa en Render

2. ❌ **JWT_SECRET no configurado o inválido**
   - Verificar que `JWT_SECRET` tenga al menos 64 caracteres
   - Verificar que no esté usando el valor por defecto

3. ❌ **Error en el código**
   - Revisar logs en Render Dashboard
   - Verificar stack traces

---

## Riesgos Detectados

### 🔴 **CRÍTICOS (Deben corregirse inmediatamente):**

1. **Dockerfile no usa PORT dinámico**
   - **Archivo:** `Dockerfile` línea 36
   - **Problema:** No pasa `$PORT` al proceso Java
   - **Impacto:** El servicio no responde correctamente en Render
   - **Solución:** Modificar `ENTRYPOINT` para usar `$PORT`

2. **PasswordEncoder en texto plano**
   - **Archivo:** `ApplicationConfig.java` línea 45
   - **Problema:** `NoOpPasswordEncoder` almacena contraseñas sin cifrar
   - **Impacto:** Riesgo de seguridad crítico
   - **Solución:** Cambiar a `BCryptPasswordEncoder`

### 🟡 **MEDIOS (Recomendado corregir):**

3. **Variables de entorno no verificadas**
   - **Problema:** No hay validación de que las variables estén configuradas
   - **Impacto:** La app puede fallar silenciosamente
   - **Solución:** Agregar validación al inicio

4. **Estructura de directorios duplicada**
   - **Problema:** Existe `src/main` y `src/src/main`
   - **Impacto:** Confusión y posibles conflictos
   - **Solución:** Limpiar estructura duplicada

5. **CORS redundante en controladores**
   - **Problema:** `@CrossOrigin` redundante en múltiples controladores
   - **Impacto:** Código innecesario
   - **Solución:** Remover anotaciones redundantes

### 🟢 **BAJOS (Mejoras opcionales):**

6. **Falta de health check robusto**
   - Solo existe `/actuator/health` básico
   - Podría incluir verificación de DB

7. **Falta de logging estructurado**
   - Los logs no están estructurados
   - Dificulta debugging en producción

---

## Conclusión Técnica

### **Resumen Ejecutivo:**

El backend está **técnicamente bien estructurado** pero tiene **un problema crítico** que impide su funcionamiento correcto en Render: **el Dockerfile no utiliza la variable PORT dinámicamente**. Esto causa que Render no pueda enrutar el tráfico al contenedor, resultando en errores de conexión desde React Native.

### **Problema Principal:**

**Dockerfile línea 36:**
```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Debe ser:**
```dockerfile
ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=$PORT app.jar"]
```

### **Estado de Componentes:**

| Componente | Estado | Notas |
|------------|--------|-------|
| Configuración de puerto | ✅ OK | `application.properties` correcto |
| Dockerfile | ❌ **ERROR** | No usa `$PORT` dinámicamente |
| CORS | ✅ OK | Configurado globalmente |
| Spring Security | ✅ OK | Rutas públicas/protegidas correctas |
| JWT | ✅ OK | Implementación correcta |
| HTTPS | ✅ OK | Render maneja automáticamente |
| Variables de entorno | ⚠️ **VERIFICAR** | Requieren configuración manual en Render |

### **Prioridad de Correcciones:**

1. **URGENTE:** Corregir Dockerfile para usar `$PORT`
2. **ALTA:** Cambiar `NoOpPasswordEncoder` a `BCryptPasswordEncoder`
3. **MEDIA:** Verificar variables de entorno en Render
4. **BAJA:** Limpiar código redundante (CORS, estructura duplicada)

### **Recomendaciones Finales:**

1. **Inmediato:** Modificar Dockerfile para pasar `$PORT` al proceso Java
2. **Corto plazo:** Implementar `BCryptPasswordEncoder` para seguridad
3. **Mediano plazo:** Agregar validación de variables de entorno al inicio
4. **Largo plazo:** Implementar refresh tokens para mejor UX

### **Verificación Post-Corrección:**

Después de corregir el Dockerfile, verificar:

1. ✅ El servicio inicia correctamente en Render
2. ✅ Los logs muestran el puerto correcto
3. ✅ `curl https://tu-servicio.onrender.com/api/auth/login` responde
4. ✅ React Native puede conectarse sin errores de red

---

**Generado por:** Análisis técnico completo del proyecto  
**Última actualización:** 2024

