package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TransferPage extends BasePage {

    private static final String LINK_OVERVIEW = "a[href*='overview.htm']";
    private static final String LINK_TRANSFER = "a[href*='transfer.htm']";
    private static final String TABLE_ROWS = "#accountTable tbody tr";
    private static final String INPUT_AMOUNT = "#amount";
    private static final String SELECT_FROM = "#fromAccountId";
    private static final String SELECT_TO = "#toAccountId";
    private static final String BTN_TRANSFER = "input[value='Transfer']";
    private static final String MSG_SUCCESS = "h1.title";

    public TransferPage(Page page) {
        super(page);
    }

    public void navigateToOverview() {
        page.click(LINK_OVERVIEW);
        page.waitForSelector("#accountTable");
    }

    public void navigateToTransfer() {
        page.click(LINK_TRANSFER);
        page.waitForSelector(INPUT_AMOUNT);
    }

    public String findAccountWithBalance(double minAmount) {
        for (int retry = 0; retry < 10; retry++) {
            Locator rows = page.locator(TABLE_ROWS);
            int count = rows.count();

            if (count <= 2) {
                page.waitForTimeout(1000);
                continue;
            }

            for (int i = 0; i < count; i++) {
                Locator row = rows.nth(i);
                // Validar fila de datos
                if (row.locator("td").count() > 1 && row.locator("a").count() > 0) {
                    String id = row.locator("a").first().textContent().trim();
                    String balanceText = row.locator("td").nth(1).textContent().trim();
                    
                    try {
                        String clean = balanceText.replaceAll("[^0-9.-]", "");
                        if (balanceText.contains("-") && !clean.startsWith("-")) {
                            clean = "-" + clean;
                        }
                        double balance = Double.parseDouble(clean);

                        if (balance >= minAmount && balance > 0) {
                            System.out.println("Page: Cuenta Origen encontrada: " + id + " ($" + balance + ")");
                            return id;
                        }
                    } catch (Exception e) { }
                }
            }
            page.waitForTimeout(1000);
        }
        return null;
    }

    public String findDifferentAccount(String excludedId) {
        Locator rows = page.locator(TABLE_ROWS);
        int count = rows.count();

        for (int i = 0; i < count; i++) {
            Locator row = rows.nth(i);
            if (row.locator("td").count() > 1 && row.locator("a").count() > 0) {
                String id = row.locator("a").first().textContent().trim();
                if (!id.equals(excludedId)) {
                    System.out.println("Page: Cuenta Destino encontrada: " + id);
                    return id;
                }
            }
        }
        return null;
    }

    public void fillTransferForm(String amount, String fromAccount, String toAccount) {

        page.waitForFunction("document.querySelector('" + SELECT_FROM + "') && document.querySelector('" + SELECT_FROM + "').options.length > 0");
        
        page.fill(INPUT_AMOUNT, amount);
        page.selectOption(SELECT_FROM, fromAccount);
        page.waitForTimeout(500);
        page.selectOption(SELECT_TO, toAccount);
        page.click(BTN_TRANSFER);
    }

    public void validateSuccessMessage() {
        Locator title = page.locator(MSG_SUCCESS).filter(new Locator.FilterOptions().setHasText("Transfer Complete"));
        assertThat(title).isVisible();
    }
}