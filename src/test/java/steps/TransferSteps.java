package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.LoginPage;
import pages.TransferPage;
import utils.ConfigManager;
import utils.TestContext;

public class TransferSteps {

    private LoginPage loginPage;
    private TransferPage transferPage;

    private String sourceAccountId;
    private String destAccountId;
    private double transferAmount;

    private void initPages() {
        if (loginPage == null) {
            this.loginPage = new LoginPage(TestContext.getPage());
            this.transferPage = new TransferPage(TestContext.getPage());
        }
    }

    @Given("que el usuario inicia sesión correctamente")
    public void queElUsuarioIniciaSesionCorrectamente() {
        initPages();
        TestContext.getPage().navigate(ConfigManager.getProperty("base.url"));
        loginPage.login(
                ConfigManager.getProperty("test.username"),
                ConfigManager.getProperty("test.password")
        );
        TestContext.getPage().waitForSelector("a[href*='logout.htm']");
    }

    @And("tiene una cuenta origen con saldo suficiente")
    public void tieneUnaCuentaOrigenConSaldoSuficiente() {
        initPages();
        transferPage.navigateToOverview();
        
        String amountStr = ConfigManager.getProperty("transfer.amount");
        this.transferAmount = Double.parseDouble(amountStr);
        
        this.sourceAccountId = transferPage.findAccountWithBalance(transferAmount);
        
        Assertions.assertNotNull(this.sourceAccountId, 
            "No se encontró ninguna cuenta con saldo suficiente ($" + transferAmount + ") en la página de Overview.");
    }

    @And("tiene una cuenta destino activa")
    public void tieneUnaCuentaDestinoActiva() {
        initPages();
        this.destAccountId = transferPage.findDifferentAccount(this.sourceAccountId);
        
        if (this.destAccountId == null) {
            System.out.println("ADVERTENCIA: No se encontró una segunda cuenta. Se usará la misma cuenta de origen para autotransferencia.");
            this.destAccountId = this.sourceAccountId;
        }
    }

    @When("realiza una transferencia con un monto válido")
    public void realizaUnaTransferenciaConUnMontoValido() {
        initPages();
        transferPage.navigateToTransfer();
        
        transferPage.fillTransferForm(
            String.valueOf((int)transferAmount), 
            this.sourceAccountId, 
            this.destAccountId
        );
    }

    @Then("el sistema debe confirmar la transferencia exitosamente")
    public void elSistemaDebeConfirmarLaTransferenciaExitosamente() {
        initPages();
        transferPage.validateSuccessMessage();
    }
}