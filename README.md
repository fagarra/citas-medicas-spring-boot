# Citas médicas — Taller de pruebas Spring Boot

Proyecto didáctico basado en la guía de Ingeniería de Software (VI semestre).

## Requisitos
Java 25, Maven 3.9+, IntelliJ IDEA, Chrome para las pruebas UI.

## IntelliJ
File > Open > seleccione la carpeta `citas-medicas` o `pom.xml` > Load Maven Project.
Configure Project SDK = JDK 25 y Maven Runner JRE = JDK 25.

## Ejecución
- `mvn clean test` — pruebas unitarias, MVC, JPA e integración.
- `mvn verify` — pruebas anteriores y cobertura JaCoCo (mínimo 70%).
- `mvn -Dgroups=ui -DexcludedGroups= verify` — ejecutar solo Selenium (requiere Chrome; puede variar según configuración de Surefire).
- `mvn spring-boot:run` — interfaz http://localhost:8080/citas.html
- Reporte: `target/site/jacoco/index.html`.

## Advertencias
- La restricción única protege la duplicación del mismo profesional y horario, pero el límite diario de 20 no está serializado frente a concurrencia extrema; para producción se requiere bloqueo o mecanismo transaccional adicional.
- Selenium requiere Chrome y puede necesitar adaptar la entrada de `datetime-local` al formato de la plataforma/navegador.
- El proyecto se entrega como código fuente y no se ha verificado mediante compilación Maven en este entorno.
