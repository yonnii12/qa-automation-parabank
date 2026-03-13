package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.User;
import pages.RegisterPage;
import utils.ConfigManager;
import utils.TestContext;
import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RegisterSteps {
    private RegisterPage registerPage;

    private void initPage() {
        if (registerPage == null) {
            this.registerPage = new RegisterPage(TestContext.getPage());
        }
    }

    @Given("que el usuario ingresa a la página de registro")
    public void queElUsuarioIngresaALaPaginaDeRegistro() {
        initPage();
        TestContext.getPage().navigate(ConfigManager.getProperty("base.url"));
        registerPage.openRegisterForm();
    }

    @When("diligencia el formulario con datos válidos")
    public void diligenciaElFormularioConDatosValidos() {
        initPage();
        long timestamp = System.currentTimeMillis();
        String prefix = ConfigManager.getProperty("register.username.prefix");
        if (prefix == null) prefix = "user"; // Un pequeño fallback de seguridad por si acaso
        
        User user = new User(
                ConfigManager.getProperty("register.firstname"),
                ConfigManager.getProperty("register.lastname"),
                ConfigManager.getProperty("register.street"),
                ConfigManager.getProperty("register.city"),
                ConfigManager.getProperty("register.state"),
                ConfigManager.getProperty("register.zip"),
                ConfigManager.getProperty("register.phone"),
                ConfigManager.getProperty("register.ssn"),
                prefix + timestamp, 
                ConfigManager.getProperty("register.password")
        );
        registerPage.fillForm(user);
    }

    @And("envía el formulario de registro")
    public void enviaElFormularioDeRegistro() {
        initPage();
        registerPage.submit();
    }

    @Then("el sistema debe crear la cuenta correctamente")
    public void elSistemaDebeCrearLaCuentaCorrectamente() {
        initPage();
        Locator title = TestContext.getPage().locator("h1.title");
        assertThat(title).containsText("Welcome");
    }
}