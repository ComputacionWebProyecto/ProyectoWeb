# 🚀 Cómo Ejecutar SonarCloud

## ⚠️ IMPORTANTE: Solo analiza el BACKEND

**SonarCloud analiza SOLO el código Java del backend (Spring Boot).**
- ✅ **Backend**: `ProyectoWeb/` - Se analiza con SonarCloud
- ❌ **Frontend**: `ProyectoWebFront/` - NO se analiza (es TypeScript/Angular)

---

## 🚨 Si tienes tests fallando (como ahora)

**SonarCloud puede analizar el código incluso si los tests fallan.** Solo necesita que el código compile.

### Opción A: Compilar sin tests (Más rápido)

```bash
cd /home/sistemas/Escritorio/ProyectoWebCompleto/ProyectoWeb

# 1. Compilar sin ejecutar tests
mvn clean compile

# 2. Ejecutar SonarCloud (analizará el código aunque no haya cobertura)
mvn sonar:sonar -Dsonar.login=14f5b26c9ece376ece2604292ec50ed81638ddf6
```

### Opción B: Compilar y ejecutar tests (aunque fallen)

```bash
cd /home/sistemas/Escritorio/ProyectoWebCompleto/ProyectoWeb

# 1. Compilar y ejecutar tests (aunque fallen, generará cobertura parcial)
mvn clean test

# 2. Generar reporte de cobertura
mvn jacoco:report

# 3. Ejecutar SonarCloud
mvn sonar:sonar -Dsonar.login=14f5b26c9ece376ece2604292ec50ed81638ddf6
```

### Opción C: Saltar tests completamente

```bash
cd /home/sistemas/Escritorio/ProyectoWebCompleto/ProyectoWeb

# Compilar y ejecutar SonarCloud saltando tests
mvn clean compile sonar:sonar -DskipTests -Dsonar.login=14f5b26c9ece376ece2604292ec50ed81638ddf6
```

---

## ✅ Comando Recomendado (Si tienes tests fallando)

**Usa esta opción si tus tests están fallando:**

```bash
cd /home/sistemas/Escritorio/ProyectoWebCompleto/ProyectoWeb && \
mvn clean compile sonar:sonar -DskipTests -Dsonar.login=14f5b26c9ece376ece2604292ec50ed81638ddf6
```

**Esto:**
- ✅ Compila el código
- ✅ Salta los tests (no los ejecuta)
- ✅ Analiza el código con SonarCloud
- ⚠️ No tendrás cobertura de código (pero sí análisis de calidad)

---

## 📊 Paso 3: Ver los Resultados

1. Ve a [https://sonarcloud.io](https://sonarcloud.io)
2. Inicia sesión
3. Busca tu proyecto: **"Proyecto Entrega - BPMN Editor"**
4. Verás el dashboard con:
   - 🐛 **Bugs** encontrados
   - 🔒 **Vulnerabilidades** de seguridad
   - 💡 **Code Smells** (malas prácticas)
   - 📈 **Cobertura de código** (solo si ejecutaste tests)
   - 📝 **Duplicación** de código
   - ⏱️ **Deuda técnica**

---

## 🔍 Qué se Analiza

SonarCloud analiza **SOLO** el código Java en:
- ✅ `src/main/java/` - Código fuente
- ✅ `src/test/java/` - Tests (si se ejecutan)
- ❌ `src/main/resources/` - No se analiza (solo configuración)
- ❌ `ProyectoWebFront/` - No se analiza (es frontend)

**Archivos analizados:**
- Controladores (`controladores/`)
- Servicios (`service/`)
- Entidades (`entity/`)
- DTOs (`dto/`)
- Repositorios (`repository/`)
- Seguridad (`security/`)
- Configuración (`config/`)
- Excepciones (`exception/`)

---

## ⚙️ Configuración Actual

**Organization Key:** `computacionwebproyecto`
**Project Key:** `computacionwebproyecto_proyecto-entrega`
**Token:** `14f5b26c9ece376ece2604292ec50ed81638ddf6`

---

## 🛠️ Solución de Problemas

### Error: "Authentication failed"
- Verifica que el token sea correcto
- Asegúrate de copiar el token completo (sin espacios)

### Error: "Project not found"
- Verifica que el proyecto exista en SonarCloud
- Confirma que `sonar.projectKey` en `pom.xml` coincida exactamente
- El formato debe ser: `organizacion_nombre-proyecto`

### No aparece cobertura de código
- Es normal si ejecutaste con `-DskipTests`
- Para tener cobertura, necesitas ejecutar los tests (aunque fallen algunos)
- Verifica que exista el archivo: `target/site/jacoco/jacoco.xml`

### El análisis tarda mucho
- Es normal, SonarCloud procesa en la nube (2-5 minutos)
- No cierres la terminal hasta que termine

### Tests fallando
- **No es un problema para SonarCloud**
- SonarCloud puede analizar el código aunque los tests fallen
- Solo necesita que el código compile
- Usa `-DskipTests` si quieres saltar los tests

---

## 📝 Comandos Rápidos

### Si tienes tests fallando (tu caso actual):
```bash
cd /home/sistemas/Escritorio/ProyectoWebCompleto/ProyectoWeb && \
mvn clean compile sonar:sonar -DskipTests -Dsonar.login=14f5b26c9ece376ece2604292ec50ed81638ddf6
```

### Si todos los tests pasan:
```bash
cd /home/sistemas/Escritorio/ProyectoWebCompleto/ProyectoWeb && \
mvn clean verify && \
mvn sonar:sonar -Dsonar.login=14f5b26c9ece376ece2604292ec50ed81638ddf6
```

---

## ✅ Checklist

- [x] Token de SonarCloud obtenido
- [ ] Estoy en la carpeta `ProyectoWeb/` (backend)
- [ ] Ejecuté el comando con `-DskipTests` (porque los tests fallan)
- [ ] Veo los resultados en [sonarcloud.io](https://sonarcloud.io)

---

## 📌 Nota sobre Tests Fallando

**Los tests están fallando por:**
- Tests esperan excepciones específicas (`IllegalArgumentException`, `EntityNotFoundException`)
- Pero el código lanza excepciones personalizadas (`ValidationException`, `ResourceNotFoundException`)
- Algunos tests tienen dependencias no mockeadas (`NullPointerException`)

**Esto NO impide que SonarCloud analice tu código.** SonarCloud analiza:
- ✅ Calidad del código fuente
- ✅ Bugs potenciales
- ✅ Vulnerabilidades
- ✅ Code smells
- ✅ Duplicación
- ⚠️ Cobertura (solo si ejecutas tests)

---

**¿Problemas?** Revisa los logs de Maven o consulta la documentación de SonarCloud.
