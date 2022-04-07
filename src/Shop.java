import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *
 * This is a driver for RentalAggreement
 *
 */
public class Shop {

    private static final String COMMA_DELIMITER = ",";
    private static DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("M/d/yyyy").withLocale(Locale.US);

    public static void main(String[] args) {

        String toolCode = args[0];
        int rentalDays = Integer.parseInt(args[1]);
        LocalDate rentalDate = LocalDate.parse(args[2], formatter);
        double discountPercent = Double.parseDouble(args[3]);


        // get display properties and rates
        ResourceBundle displayProperties = ResourceBundle.getBundle("display");
        ResourceBundle rates = ResourceBundle.getBundle("rate");

        Map<String, Tool> inventoryMap = getInventory();

        RentalAgreement rentalAgreement = new RentalAgreementImpl(rates, displayProperties, Locale.US);

        rentalAgreement.checkout(inventoryMap.get(toolCode), rentalDays, rentalDate, discountPercent);
    }


    /**
     *
     *
     * @return map of tool code to tools in inventory
     */
    static Map<String, Tool> getInventory(){

        Map map = new HashMap();

        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader( new InputStreamReader(Shop.class.getClassLoader().getResourceAsStream("inventory.csv")))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                map.put(values[0], new Tool(values[0], ToolType.valueOf(values[1]), Brand.valueOf(values[2]), Boolean.parseBoolean(values[3])));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return map;
    }

}
