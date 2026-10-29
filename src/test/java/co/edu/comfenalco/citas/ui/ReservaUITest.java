package co.edu.comfenalco.citas.ui;
import co.edu.comfenalco.citas.repositorio.CitaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.JavascriptExecutor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("ui")
class ReservaUITest {
 @LocalServerPort int puerto;
 @Autowired CitaRepository repo;
 WebDriver driver;
 @BeforeEach void abrirNavegador(){
  repo.deleteAll();
  ChromeOptions opciones=new ChromeOptions();opciones.addArguments("--headless=new","--no-sandbox","--disable-dev-shm-usage");
  driver=new ChromeDriver(opciones);
 }
 @AfterEach void cerrarNavegador(){if(driver!=null)driver.quit();}


 private void completarFormulario(
         String profesional,
         String fecha,
         String paciente) {

  driver.findElement(By.id("profesional")).clear();
  driver.findElement(By.id("profesional"))
          .sendKeys(profesional);

  //String fechaNormalizada =
          java.time.LocalDateTime.parse(fecha)
                  .toString();

 /* DateTimeFormatter formatoEntrada =
          DateTimeFormatter.ofPattern(
                  "MM/dd/uuuu hh:mma",
                  Locale.US
          );

  LocalDateTime fechaConvertida =
          LocalDateTime.parse(fecha, formatoEntrada);*/

 /* String fechaNormalizada =
          fechaConvertida.format(
                  DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm")
          );*/

  String fechaNormalizada =
          LocalDateTime.parse(fecha)
                  .format(DateTimeFormatter.ofPattern(
                          "uuuu-MM-dd'T'HH:mm"
                  ));


  ((JavascriptExecutor) driver).executeScript("""
        const campo = document.getElementById('fechaHora');
        campo.value = arguments[0];
        campo.dispatchEvent(
            new Event('input', { bubbles: true })
        );
        campo.dispatchEvent(
            new Event('change', { bubbles: true })
        );
        """, fechaNormalizada);

  String fechaIngresada =
          driver.findElement(By.id("fechaHora"))
                  .getDomProperty("value");

  System.out.println(
          "Fecha asignada: " + fechaIngresada
  );

  assertThat(fechaIngresada)
          .as("La fecha debe quedar registrada")
          .isNotBlank();

  driver.findElement(By.id("pacienteId"))
          .sendKeys(paciente);


  // 6. LIMPIAR EL MENSAJE ANTERIOR
  // ===== AGREGAR AQUÍ =====
  ((JavascriptExecutor) driver).executeScript(
          "document.getElementById('mensaje').textContent = '';"
  );

  driver.findElement(By.id("btnReservar"))
          .click();
 }

/* private void completarFormulario(String profesional,String fecha,String paciente){
  driver.findElement(By.id("profesional")).clear();driver.findElement(By.id("profesional")).sendKeys(profesional);
  driver.findElement(By.id("fechaHora")).sendKeys(fecha);
  driver.findElement(By.id("pacienteId")).clear();driver.findElement(By.id("pacienteId")).sendKeys(paciente);
  driver.findElement(By.id("btnReservar")).click();
 }*/

/* private void esperarMensaje(String texto){
  boolean encontrado=new WebDriverWait(driver,Duration.ofSeconds(10)).until(ExpectedConditions.textToBePresentInElementLocated(By.id("mensaje"),texto));
  assertThat(encontrado).isTrue();
 }*/

 private void esperarMensaje(String texto) {

  try {
   new WebDriverWait(driver, Duration.ofSeconds(15))
           .until(
                   ExpectedConditions.textToBePresentInElementLocated(
                           By.id("mensaje"), texto
                   )
           );

  } catch (TimeoutException e) {

   String mensajeActual =
           driver.findElement(By.id("mensaje")).getText();

   String fechaActual =
           driver.findElement(By.id("fechaHora"))
                   .getDomProperty("value");

   System.out.println("===== DIAGNÓSTICO SELENIUM =====");
   System.out.println("Mensaje esperado: " + texto);
   System.out.println("Mensaje obtenido: " + mensajeActual);
   System.out.println("Fecha del formulario: " + fechaActual);
   System.out.println("URL actual: " + driver.getCurrentUrl());

   throw e;
  }
 }


 @Test void debeConfirmarLaReservaAlPaciente(){
  driver.get("http://localhost:"+puerto+"/citas.html");
  completarFormulario("Dra. Gómez","2030-11-05T09:00","paciente-010");
  esperarMensaje("Cita reservada");
  //"11/05/2030 09:00AM"
 }
 @Test void debeRechazarDobleReserva(){
  driver.get("http://localhost:"+puerto+"/citas.html");
  completarFormulario("Dr. López","2030-11-06T10:00","paciente-011");
  esperarMensaje("Cita reservada");
  completarFormulario("Dr. López","2030-11-06T10:00","paciente-012");
  esperarMensaje("Horario no disponible");
  //"11/06/2030 10:00AM"
  // Comprobar que solo existe una reserva
  assertThat(repo.count()).isEqualTo(1);
 }
}
