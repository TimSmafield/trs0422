import java.time.LocalDate;

public interface RentalAgreement {



    RentAgreementDetails checkout(Tool tool, int rentalDays, LocalDate checkOutDate, double discountPercent);
    void print(RentAgreementDetails rentAgreementDetails);
}
