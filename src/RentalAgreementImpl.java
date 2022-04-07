import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RentalAgreementImpl implements RentalAgreement {
  public RentalAgreementImpl(ResourceBundle rateMap, ResourceBundle displayProperties, Locale locale) {
    this.rateMap = rateMap;
    this.displayProperties = displayProperties;
    this.locale = locale;
  }

  ResourceBundle rateMap;
  ResourceBundle displayProperties;
  Locale locale;

  static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
  static final int SCALE = 2;


  @Override
  public RentAgreementDetails checkout(Tool tool, int rentalDays, LocalDate checkOutDate, double discountPercent) {

    if (tool == null ) {
      throw new NullPointerException("Tool object is null.");
    }

    if (checkOutDate == null) {
      throw new NullPointerException("Checkout Date is null.");
    }

    if(rentalDays < 1) {
      throw new IllegalArgumentException("Rental day count must be greater than zero.");
    }

    if(discountPercent < 0 || discountPercent > 1) {
      throw new IllegalArgumentException("Discount percent must be between 0 and 1");
    }

    RentAgreementDetails rentAgreementDetails = new RentAgreementDetails();
    rentAgreementDetails.setTool(tool);
    rentAgreementDetails.setRentalDays(rentalDays);
    rentAgreementDetails.setCheckOutDate(checkOutDate);
    rentAgreementDetails.setDiscountPercent(discountPercent);

    BigDecimal dailyRate = new BigDecimal(Double.parseDouble(rateMap.getString(tool.getToolType().name()))).setScale(SCALE, ROUNDING_MODE);
    rentAgreementDetails.setDailyRentalCharge(dailyRate);

    LocalDate dueDate = calculateDueDate(checkOutDate, rentalDays);
    rentAgreementDetails.setDueDate(dueDate);
    int chargeDays = calculateChargeDays(checkOutDate, dueDate, tool);
    rentAgreementDetails.setChargeDays(chargeDays);
    BigDecimal preDiscountCharge = dailyRate.multiply( new BigDecimal(chargeDays)).setScale(SCALE, ROUNDING_MODE);
    rentAgreementDetails.setPreDiscountCharge(preDiscountCharge);
    BigDecimal discountAmount = preDiscountCharge.multiply(new BigDecimal(discountPercent)).setScale(SCALE, ROUNDING_MODE);
    rentAgreementDetails.setDiscountAmount(discountAmount);
    rentAgreementDetails.setFinalAmount(preDiscountCharge.subtract(discountAmount).setScale(SCALE, ROUNDING_MODE));

    print(rentAgreementDetails);
    return rentAgreementDetails;
  }

  @Override
  public void print(RentAgreementDetails rentAgreementDetails) {

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(displayProperties.getString("DateFormat"));
    NumberFormat  percentFormatter = NumberFormat.getPercentInstance(locale);
    DecimalFormat currencyFormatter = new DecimalFormat(displayProperties.getString("currencyFormat"));

    Tool tool = rentAgreementDetails.getTool();
    System.out.println(displayProperties.getString("ToolCode") +  ": "  + tool.getToolCode());
    System.out.println(displayProperties.getString("ToolType") +  ": "  + tool.getToolType());
    System.out.println(displayProperties.getString("Brand") +  ": "  + tool.getBrand());
    System.out.println(displayProperties.getString("DailyRentalCharge") +  ": "  + currencyFormatter.format(rentAgreementDetails.getDailyRentalCharge()));
    System.out.println(displayProperties.getString("CheckOutDate") +  ": "  + dateFormatter.format(rentAgreementDetails.getCheckOutDate()));
    System.out.println(displayProperties.getString("DiscountPercent") +  ": "  + percentFormatter.format(rentAgreementDetails.getDiscountPercent()));
    System.out.println(displayProperties.getString("RentalDays") +  ": "  + rentAgreementDetails.getRentalDays());
    System.out.println(displayProperties.getString("ChargeDays") +  ": "  + rentAgreementDetails.getChargeDays());
    System.out.println(displayProperties.getString("prediscountCharge") +  ": "  + currencyFormatter.format(rentAgreementDetails.getPreDiscountCharge()));
    System.out.println(displayProperties.getString("FinalCharge") +  ": "  + currencyFormatter.format(rentAgreementDetails.getFinalAmount()));
  }

  protected LocalDate calculateDueDate( LocalDate  checkout, int rentalDays) {

     return  checkout.plusDays(rentalDays);
  }

  protected int calculateChargeDays( LocalDate  checkout, LocalDate  dueDate, Tool tool ) {

    List<LocalDate> dates = Stream.iterate(checkout, date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(checkout, dueDate)).filter(createDateFilter(tool, checkout,dueDate))
            .collect(Collectors.toList());

    return dates.size();
  }

  protected Predicate<LocalDate> createDateFilter(Tool tool, LocalDate  checkout, LocalDate dueDate) {

    Predicate<LocalDate> predicate = date -> isWeekend(date) ? tool.getToolType().getChargeWeekends() : tool.getToolType().getChargeWeekdays();

    if ( !tool.getToolType().getChargeHolidays()  ) {

      final Set<LocalDate> holidays = new HashSet();
      IntStream.rangeClosed(checkout.getYear(), dueDate.getYear()).forEach(  year ->  holidays.addAll(setHolidays(year)));
      predicate = predicate.and( date -> !holidays.contains(date));
    }

    return predicate;
  }

  protected Set<LocalDate> setHolidays(int year) {

    Set<LocalDate> holidays = new HashSet<>();

    LocalDate independenceDay = LocalDate.of(year, 7, 4);
    if ( isWeekend(independenceDay) ) {
      independenceDay = independenceDay.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }
    holidays.add(independenceDay);

    LocalDate laborDay = LocalDate.of(year, 9, 1).with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    holidays.add(laborDay);

    return holidays;
  }

  protected boolean isWeekend (LocalDate date) {

     return date.get(ChronoField.DAY_OF_WEEK) > DayOfWeek.FRIDAY.getValue();
  }



}
