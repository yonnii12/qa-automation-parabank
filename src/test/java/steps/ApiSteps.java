package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import utils.ConfigManager;
import utils.TestContext;

import java.util.List;

public class ApiSteps {

    private String username;
    private String password;
    private String customerId;
    private Response response;
    // Quitamos URL por default
    private final String BASE_URL = ConfigManager.getProperty("api.base.url");

    private void logResponse(String stepName, Response response) {
        String log = String.format("Step: %s\nStatus: %d\nBody: %s",
                stepName,
                response.getStatusCode(),
                response.getBody().asPrettyString());
        
        if (TestContext.getScenario() != null) {
            TestContext.getScenario().attach(log.getBytes(), "text/plain", stepName + " Log");
        }
        
        System.out.println(log);
    }

    @Given("que existe un usuario válido para consumir la API")
    public void queExisteUnUsuarioValido() {
        this.username = ConfigManager.getProperty("test.username");
        this.password = ConfigManager.getProperty("test.password");
    }

    @When("consulto el servicio de login")
    public void consultoElServicioDeLogin() {
        response = RestAssured.given()
                .baseUri(BASE_URL)
                .pathParam("username", username)
                .pathParam("password", password)
                .header("Accept", "application/xml") 
                .log().all()
                .when()
                .get("/login/{username}/{password}");
        
        logResponse("Login Service", response);

        Assertions.assertEquals(200, response.getStatusCode(), "El login falló con código: " + response.getStatusCode());
        customerId = response.xmlPath().getString("login.id");
        Assertions.assertNotNull(customerId, "No se pudo recuperar el Customer ID de la respuesta");
    }

    @And("consulto el servicio getAccounts con el customerId obtenido")
    public void consultoElServicioGetAccounts() {
        response = RestAssured.given()
                .baseUri(BASE_URL)
                .pathParam("customerId", customerId)
                .header("Accept", "application/xml")
                .log().all()
                .when()
                .get("/customers/{customerId}/accounts");
        
        logResponse("Get Accounts Service", response);
    }

    @Then("la respuesta debe ser exitosa")
    public void laRespuestaDebeSerExitosa() {
        response.then().statusCode(200);
    }

    @And("la lista de cuentas no debe estar vacía")
    public void laListaDeCuentasNoDebeEstarVacia() {
        List<String> accountIds = response.xmlPath().getList("accounts.account.id");
        Assertions.assertFalse(accountIds.isEmpty(), "El cliente no tiene cuentas asociadas");
    }
}