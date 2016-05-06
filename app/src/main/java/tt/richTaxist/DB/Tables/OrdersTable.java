package tt.richTaxist.DB.Tables;

import android.content.ContentValues;
import android.provider.BaseColumns;
import tt.richTaxist.DB.MySQLHelper;
import tt.richTaxist.Units.Order;
import tt.richTaxist.Util;

public class OrdersTable {
    public static final String TABLE_NAME = "Orders";

    public static final String ARRIVAL_DATE_TIME = "arrivalDateTime";
    public static final String PRICE = "price";
    public static final String TYPE_OF_PAYMENT = "typeOfPayment";
    public static final String SHIFT_ID = "shiftID";
    public static final String DISTANCE = "distance";
    public static final String TRAVEL_TIME = "travelTime";
    public static final String NOTE = "note";
    public static final String TAXOPARK_ID = "taxoparkID";
    public static final String BILLING_ID = "billingID";

    public OrdersTable() { } //table cannot be instantiated

    public static final String FIELDS = MySQLHelper.PRIMARY_KEY
            + ARRIVAL_DATE_TIME + " NUMERIC, "
            + PRICE             + " INTEGER, "
            + TYPE_OF_PAYMENT   + " INTEGER, "
            + SHIFT_ID          + " INTEGER, "
            + DISTANCE          + " INTEGER, "
            + TRAVEL_TIME       + " INTEGER,"
            + NOTE              + " TEXT,"
            + TAXOPARK_ID       + " INTEGER, "
            + BILLING_ID        + " INTEGER";

    public static ContentValues getContentValues(Order order) {
        ContentValues cv = new ContentValues();
        if (order.orderID != -1) {
            cv.put(BaseColumns._ID, order.orderID);
        }
        cv.put(ARRIVAL_DATE_TIME, Util.dateFormat.format(order.arrivalDateTime));
        cv.put(PRICE, order.price);
        cv.put(TYPE_OF_PAYMENT, order.typeOfPayment.id);
        cv.put(SHIFT_ID, order.shiftID);
        cv.put(DISTANCE, order.shiftID);
        cv.put(TRAVEL_TIME, order.shiftID);
        cv.put(NOTE, order.note);
        cv.put(TAXOPARK_ID, order.taxoparkID);
        cv.put(BILLING_ID, order.billingID);
        return cv;
    }
}