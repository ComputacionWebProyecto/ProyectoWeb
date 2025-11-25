# 🔄 Configuración de SonarCloud con GitHub Actions

## ✅ Workflow Creado

Se ha creado el archivo `.github/workflows/sonarcloud.yml` que ejecutará SonarCloud automáticamente en cada push y pull request.

## 📋 Pasos para Activar

### 1. Agregar el Token como Secret en GitHub

1. Ve a tu repositorio en GitHub
2. Ve a **Settings** → **Secrets and variables** → **Actions**
3. Haz clic en **"New repository secret"**
4. Crea un nuevo secret:
   - **Name:** `SONAR_TOKEN`
   - **Value:** `14f5b26c9ece376ece2604292ec50ed81638ddf6`
5. Haz clic en **"Add secret"**

### 2. Verificar que el Workflow Funcione

El workflow se ejecutará automáticamente cuando:
- ✅ Hagas push a las ramas: `main`, `dev`, `master`
- ✅ Crees un Pull Request hacia esas ramas
- ✅ Actualices un Pull Request existente

### 3. Ver los Resultados

1. Ve a la pestaña **"Actions"** en tu repositorio de GitHub
2. Verás el workflow **"SonarCloud Analysis"** ejecutándose
3. Haz clic en el workflow para ver los logs
4. Los resultados también estarán en [sonarcloud.io](https://sonarcloud.io)

## 🔍 Qué Hace el Workflow

```yaml
1. Checkout del código
2. Configura JDK 17
3. Cachea dependencias de Maven (más rápido)
4. Compila el código (sin tests)
5. Ejecuta análisis de SonarCloud
6. Envía resultados a SonarCloud
```

## ⚙️ Configuración Actual

- **Organization:** `computacionwebproyecto`
- **Project Key:** `computacionwebproyecto_proyecto-entrega`
- **Token:** Se usa desde `secrets.SONAR_TOKEN`
- **Branches:** `main`, `dev`, `master`

## 🚀 Ventajas de GitHub Actions

✅ **Automático:** Se ejecuta en cada push/PR
✅ **No necesitas ejecutar comandos manualmente**
✅ **Historial:** Puedes ver todos los análisis en GitHub Actions
✅ **Integración:** Los resultados aparecen en SonarCloud automáticamente
✅ **Badges:** Puedes agregar badges de calidad en tu README

## 📊 Ver Resultados

### En GitHub:
1. Ve a **Actions** → Selecciona el workflow → Ver logs

### En SonarCloud:
1. Ve a [https://sonarcloud.io](https://sonarcloud.io)
2. Busca tu proyecto: **"Proyecto Entrega - BPMN Editor"**
3. Verás el dashboard con todos los análisis

## 🛠️ Solución de Problemas

### El workflow no se ejecuta
- Verifica que el archivo esté en `.github/workflows/sonarcloud.yml`
- Verifica que estés haciendo push a `main`, `dev` o `master`
- Revisa que el secret `SONAR_TOKEN` esté configurado

### Error: "Authentication failed"
- Verifica que el secret `SONAR_TOKEN` tenga el valor correcto
- El token debe ser: `14f5b26c9ece376ece2604292ec50ed81638ddf6`

### Error: "Project not found"
- Verifica que el proyecto exista en SonarCloud
- Confirma que `sonar.projectKey` sea correcto

## 🔄 Ejecución Manual vs Automática

### Manual (lo que hiciste antes):
```bash
mvn clean compile sonar:sonar -DskipTests -Dsonar.login=TU_TOKEN
```
- ✅ Útil para pruebas rápidas
- ❌ Tienes que ejecutarlo manualmente cada vez

### Automático (GitHub Actions):
- ✅ Se ejecuta solo en cada push/PR
- ✅ Historial completo en GitHub
- ✅ No necesitas hacer nada manualmente
- ✅ Todo el equipo ve los resultados

## 📝 Próximos Pasos

1. ✅ Workflow creado
2. ⏳ Agregar `SONAR_TOKEN` como secret en GitHub
3. ⏳ Hacer un push para activar el workflow
4. ⏳ Ver resultados en GitHub Actions y SonarCloud

---

**¿Listo?** Solo falta agregar el secret `SONAR_TOKEN` en GitHub y hacer un push. ¡El workflow se ejecutará automáticamente!

