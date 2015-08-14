package tt.richTaxist.DB.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by AlexShredder on 10.07.2015.
 */
public class SQLHelper extends SQLiteOpenHelper {
    static final String DB_NAME = "taxiDB";
    static final int DB_VERSION = 19;
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
    private static final String LOG_TAG = "SQLHelper";

    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

//    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
//        super(context, name, factory, version, errorHandler);
//    }

    SQLHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OrdersSQLHelper.CREATE_TABLE);
        db.execSQL(ShiftsSQLHelper.CREATE_TABLE);
        db.execSQL(LocationsSqlHelper.CREATE_TABLE);
    }

    //запускается, когда скуль видит, что сохраненная версия БД имеет другой номер, чем текущий
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Found new DB version. About to update to: " + String.valueOf(DB_VERSION));
        //очень странная проверка версии
//        if (newVersion == 14) {
//            db.execSQL("DROP TABLE IF EXISTS " + LocationsSqlHelper.TABLE_NAME);
//            db.execSQL(LocationsSqlHelper.CREATE_TABLE);
//        }
//        else {
        //TODO: найти способ сохранять старую базу и импортировать ее в новую при смене DB_VERSION
            db.execSQL("DROP TABLE IF EXISTS " + OrdersSQLHelper.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ShiftsSQLHelper.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LocationsSqlHelper.TABLE_NAME);
            // Create tables again
            onCreate(db);
//        }
    }

//    Date getStartOfDay(Date day) {
//        return getStartOfDay(day, Calendar.getInstance());
//    }
//
//    private Date getStartOfDay(Date day, Calendar cal) {
//        if (day == null) day = new Date();
//        cal.setTime(day);
//        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
//        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
//        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
//        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
//        return cal.getTime();
//    }
//
//    Date getEndOfDay(Date day) {
//        return getEndOfDay(day, Calendar.getInstance());
//    }
//
//    private Date getEndOfDay(Date day, Calendar cal) {
//        if (day == null)
//            day = new Date();
//        cal.setTime(day);
//        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
//        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
//        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
//        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
//        return cal.getTime();
//    }
}
