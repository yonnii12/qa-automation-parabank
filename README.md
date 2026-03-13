# QA Automation - ParaBank Framework

Este proyecto es una solución de automatización de pruebas híbrida (Frontend + Backend) para el sitio demostrativo [ParaBank](https://parabank.parasoft.com/parabank). Implementa patrones de diseño avanzados como Page Object Model (POM) y BDD.

---

## 📂 Estructura del Proyecto

La arquitectura sigue el patrón **Page Object Model (POM)** separado por capas:

```
qa-automation-parabank/
├── documentos/               # Documentación (Estrategia, Casos de Prueba, Defectos)
├── src/test/java/
│   ├── hooks/          # Gestión del ciclo de vida (Browser, Video, Screenshots)
│   ├── models/         # POJOs para datos 
│   ├── pages/          # Page Objects (Selectores y Acciones UI)
│   ├── runners/        # Clase principal para ejecutar tests Cucumber
│   ├── steps/          # Step Definitions (Conexión Gherkin -> Java)
│   └── utils/          # Utilidades (ConfigManager, TestContext)
├── src/test/resources/
│   ├── features/       # Archivos .feature (Escenarios de prueba)
│   └── config.properties # Configuración centralizada (URLs, Credenciales)
├── api-postman/        # Colección Postman y Environment para Newman
├── .github/workflows/  # Pipeline de CI/CD para GitHub Actions
└── target/             # Reportes HTML, Videos y Screenshots generados
```

---

## 📋 Dependencias

Para ejecutar este proyecto, el entorno debe contar con las siguientes tecnologías:

**Software Base:**
*   **Java JDK 17** (Versión LTS requerida).
*   **Maven 3.8+** (Gestor de dependencias y ciclo de vida).
*   **Node.js & npm** (Requerido solo para la ejecución de pruebas Postman/Newman).

**Librerías del Proyecto (Gestionadas por Maven):**
*   **Playwright for Java (v1.49.0):** Automatización de UI.
*   **Cucumber-JVM (v7.20.1):** Framework BDD para Gherkin.
*   **Rest Assured (v5.5.0):** Pruebas de API en Java.
*   **JUnit 5:** Motor de ejecución de pruebas.

---

## ⚙️ Instalación

Sigue estos pasos para configurar el proyecto en una máquina nueva:

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/yonnii12/qa-automation-parabank
    cd qa-automation-parabank
    ```

2.  **Instalar dependencias de Java (Maven):**
    Ejecuta el siguiente comando para descargar todas las librerías necesarias definidas en el `pom.xml`:
    ```bash
    mvn clean install -DskipTests
    ```

3.  **Instalar navegadores de Playwright:**
    Playwright necesita descargar los binarios de los navegadores (Chromium, Firefox, Webkit) para funcionar:
    ```bash
    mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
    ```

4.  **Instalar dependencias de Node.js (Para reportes Postman):**
    Si deseas generar el reporte HTML de las pruebas de API con Newman:
    ```bash
    npm install -g newman newman-reporter-htmlextra
    ```
    *(Si no tienes permisos de administrador, quita el flag `-g` para instalar localmente).*

---

## ▶️ Ejecución

Existen dos formas de ejecutar las pruebas, dependiendo del alcance deseado.

### 1. Ejecución Framework Java (UI y API RestAssured)

El proyecto utiliza Maven para la ejecución. Los resultados se generarán en la carpeta `target/`.

*   **Ejecutar TODAS las pruebas (Regresión completa):**
    ```bash
    mvn test
    ```

*   **Ejecutar solo pruebas de Interfaz de Usuario (UI):**
    ```bash
    mvn test -Dcucumber.filter.tags="@ui"
    ```

*   **Ejecutar solo pruebas de API (Java):**
    ```bash
    mvn test -Dcucumber.filter.tags="@api"
    ```

**Ver Evidencias Java:**
*   Reporte HTML: Abrir `target/cucumber-report.html` en un navegador.
*   Videos de ejecución: Revisar carpeta `target/videos/` (Los archivos tienen el nombre del escenario y el estado PASSED/FAILED).
*   Screenshots: Se adjuntan automáticamente al reporte HTML si el test falla.

### 2. Ejecución Colección Postman (Newman)

Para cumplir con la entrega de la colección de Postman, se utiliza Newman.

*   **Comando de ejecución:**
    ```bash
    npx newman run api-postman/ParaBank_API.postman_collection.json \
      -e api-postman/QA_Environment.postman_environment.json \
      -r "cli,htmlextra" \
      --reporter-htmlextra-export target/newman/api-report.html
    ```

**Ver Evidencias Postman:**
*   Reporte HTML: Abrir `target/newman/api-report.html`.

---

## ⚠️ Problemas Conocidos

Durante la ejecución podrías encontrar los siguientes comportamientos:

1.  **Error "Strict Mode Violation":**
    *   *Síntoma:* Playwright falla diciendo que encontró múltiples elementos.
    *   *Causa:* Selectores CSS ambiguos.
    *   *Solución:* Se han refinado los selectores usando filtros de texto (ej. `.filter(hasText("Overview"))`) para asegurar unicidad.

2.  **Error de versión de Java:**
    *   *Síntoma:* `Fatal error compiling: error: invalid target release: 22`.
    *   *Solución:* Asegúrate de tener JDK 17 instalado y configurado en tu variable de entorno `JAVA_HOME`.

---

## 💡 Recomendaciones

1.  **Configuración Centralizada:**
    No modifiques el código para cambiar usuarios o URLs. Edita el archivo `src/test/resources/config.properties`. Ahí puedes cambiar:
    *   `headless=false`: Para ver el navegador mientras corre la prueba.
    *   `base.url`: Por si cambia el entorno de QA.
    *   `transfer.amount`: Para cambiar el monto de prueba.

2.  **Limpieza de Evidencias:**
    No es necesario borrar manualmente la carpeta `target/videos`. El framework incluye un Hook (`Hooks.java`) que limpia automáticamente los videos de la ejecución anterior al iniciar una nueva suite, manteniendo tu disco limpio.

3.  **Integración Continua (CI/CD):**
    El proyecto incluye un flujo de trabajo de GitHub Actions en `.github/workflows/maven.yml`. Al subir este código a GitHub, las pruebas se ejecutarán automáticamente en la nube.
