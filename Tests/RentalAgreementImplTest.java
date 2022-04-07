import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RentalAgreementImplTest {

    RentalAgreement rentalAgreement;
    Map<String, Tool> inventoryMap;
    DateTimeFormatter formatter;

    //formatter.withLocale( Locale.US );
    @org.junit.jupiter.api.BeforeEach
    void setUp() {

        ResourceBundle displayProperties =  ResourceBundle.getBundle("display");
        ResourceBundle rates =  ResourceBundle.getBundle("rate");

        rentalAgreement = new RentalAgreementImpl(rates, displayProperties, Locale.US);
        inventoryMap = Shop.getInventory();
        formatter = DateTimeFormatter.ofPattern("M/d/yyyy").withLocale(Locale.US);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "happyPathData.csv", numLinesToSkip = 1)
    void testCheckoutHappyPath(String toolCode, String rentalDays, String checkOutDate, String discountPercent, String expectedChargeDays, String expectedFinalCharge ) {

        RentAgreementDetails details = rentalAgreement.checkout( inventoryMap.get(toolCode) , Integer.parseInt(rentalDays),  LocalDate.parse(checkOutDate, formatter), Double.parseDouble(discountPercent));

        assertEquals(Integer.parseInt(expectedChargeDays), details.getChargeDays());
        assertEquals(Double.parseDouble(expectedFinalCharge), details.getFinalAmount().doubleValue());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "badDiscountInput.csv", numLinesToSkip = 1)
    void testCheckoutBadDiscountInput(String toolCode, String rentalDays, String checkOutDate, String discountPercent) {


        RuntimeException thrown = assertThrows( IllegalArgumentException.class,() -> {
            RentAgreementDetails details = rentalAgreement.checkout( inventoryMap.get(toolCode) , Integer.parseInt(rentalDays),  LocalDate.parse(checkOutDate, formatter), Double.parseDouble(discountPercent));
        });
        assertEquals("Discount percent must be between 0 and 1", thrown.getMessage());
    }

    @Test
    void testCheckoutBadRentalDays() {

        String toolCode = "CHNS";
        String rentalDays = "-3";
        String checkOutDate = "7/2/2015";
        String discountPercent = ".1";
        RuntimeException thrown = assertThrows( IllegalArgumentException.class,() -> {
            RentAgreementDetails details = rentalAgreement.checkout( inventoryMap.get(toolCode) , Integer.parseInt(rentalDays),  LocalDate.parse(checkOutDate, formatter), Double.parseDouble(discountPercent));
        });
        assertEquals("Rental day count must be greater than zero.", thrown.getMessage());
    }


}