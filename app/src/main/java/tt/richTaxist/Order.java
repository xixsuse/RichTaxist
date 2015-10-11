package tt.richTaxist;

import java.util.Calendar;
import java.util.Date;
import tt.richTaxist.DB.BillingsSQLHelper;
import tt.richTaxist.DB.TaxoparksSQLHelper;
import tt.richTaxist.Enums.TypeOfPayment;

/**
 * Created by Tau on 27.06.2015.
 */
public class Order {
    public static final String PARAM_DISTANCE = "tt.richTaxist.Order.DISTANCE";
    public static final String PARAM_TRAVEL_TIME = "tt.richTaxist.Order.TRAVEL_TIME";
    public Date arrivalDateTime;
    public int price;
    public TypeOfPayment typeOfPayment;
    public Shift shift;
    public String note;
    public int distance;
    public long travelTime;
    public int taxoparkID;
    public int billingID;

    public Order(Date arrivalDateTime, int price, TypeOfPayment typeOfPayment, Shift shift, String note,
                 int distance, long travelTime, int taxoparkID, int billingID) {
        this.arrivalDateTime = arrivalDateTime;
        this.price           = price;
        this.typeOfPayment   = typeOfPayment;
        this.shift           = shift;
        this.note            = note;
        this.distance        = distance;
        this.travelTime      = travelTime;
        this.taxoparkID      = taxoparkID;
        this.billingID       = billingID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || arrivalDateTime == null) return false;
        Order that = (Order) o;
        if (that.arrivalDateTime == null) return false;
        return arrivalDateTime.equals(that.arrivalDateTime);
    }

    @Override
    public int hashCode() {
        return arrivalDateTime != null ? 31 * Integer.parseInt(arrivalDateTime.toString()) : 0;
    }

    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(arrivalDateTime);
        String text = String.format("подача: %02d.%02d.%02d %02d:%02d," +
                        "\nцена: %d, тип оплаты: %s," +
                        "\nпарк: %s, расчёты: %s",
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), price, typeOfPayment.toString(),
                TaxoparksSQLHelper.dbOpenHelper.getTaxoparkByID(taxoparkID),
                BillingsSQLHelper.dbOpenHelper.getBillingByID(billingID));
        if (!"".equals(note)) text += String.format(",\nзаметка: %s", note);
        return text;
    }
}
