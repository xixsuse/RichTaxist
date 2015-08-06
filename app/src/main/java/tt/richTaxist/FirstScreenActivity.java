package tt.richTaxist;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

import tt.richTaxist.ChatClient.ChatLoginActivity;
import tt.richTaxist.DB.ShiftsStorage;
import tt.richTaxist.gps.GPSHelper;
import tt.richTaxist.gps.RouteActivity;

public class FirstScreenActivity extends AppCompatActivity {
    static AppCompatActivity activity;
    private static final String LOG_TAG = "FirstScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activity = FirstScreenActivity.this;
        MainActivity.context = getApplicationContext();
        GPSHelper.startService(MainActivity.context);

        Storage.init(this);

        try {
            Parse.initialize(this, "PF47pDUAtRLyPmuFEh607NmCOA4NxMHKAODTsAqy", "kax79lUpsVC0S3BN0rBPqvvkqPce4rVtBvNy8d0D");
            ParseAnalytics.trackAppOpened(getIntent());
            ParseUser.enableAutomaticUser();
        }catch (RuntimeException e){Log.d(LOG_TAG, "Parse already launched");}

//        boolean isAnonymous = ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser());
        //Такая проверка может пригодиться, чтобы показать или скрыть кнопку для входа или регистрации.


        TelephonyManager tm = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        Storage.deviceIMEI = tm.getDeviceId();
        Log.d(LOG_TAG, "IMEI: " + Storage.deviceIMEI);

        if (Storage.currentUser == null) {
            // Отправляем данные на Parse.com для проверки только если юзер еще не авторизован
            Log.d(LOG_TAG, "userName: " + Storage.userName);
            Log.d(LOG_TAG, "password: " + Storage.password);
            authorize(this);
        }

        findViewById(R.id.buttonOpenLastShift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.currentShift = ShiftsStorage.getLastShift();
                if (MainActivity.currentShift == null) {
                    Toast.makeText(activity, "сохраненных смен не найдено", Toast.LENGTH_SHORT).show();
                    return;
                }
                MainActivity.ordersStorage.fillOrdersByShift(MainActivity.currentShift);
                startActivity(new Intent(activity, ShiftTotalsActivity.class));
                Log.d(LOG_TAG, "открываю последнюю сохраненную смену");
                finish();
            }
        });
        findViewById(R.id.buttonNewShift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.currentShift = new Shift();
                MainActivity.ordersStorage.clear(false);
                startActivity(new Intent(activity, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                Log.d(LOG_TAG, "открываю новую смену");
                finish();
            }
        });
        findViewById(R.id.buttonOpenShift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.shiftsStorage.clear();
                MainActivity.shiftsStorage.addAll(ShiftsStorage.getShiftsForList());
                startActivity(new Intent(activity, ShiftsListActivity.class));
                Log.d(LOG_TAG, "открываю список сохраненных смен");
            }
        });
        findViewById(R.id.buttonSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, SettingsActivity.class));
                Log.d(LOG_TAG, "открываю настройки");
            }
        });
        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, SignInActivity.class));
                Log.d(LOG_TAG, "открываю экран учетных записей");
            }
        });
        findViewById(R.id.buttonChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, ChatLoginActivity.class));
                Log.d(LOG_TAG, "открываю чат");
            }
        });
        findViewById(R.id.buttonRoute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.currentShift == null){
                    Toast.makeText(activity, "Не выбрана текущая смена!", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(activity, RouteActivity.class));
                Log.d(LOG_TAG, "открываю карту маршрута смены");
            }
        });
        findViewById(R.id.buttonGrandTotals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, GrandTotalsActivity.class));
                Log.d(LOG_TAG, "открываю итоги по зарплате");
            }
        });
        findViewById(R.id.buttonExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Storage.openQuitDialog(activity);
                Log.d(LOG_TAG, "открываю диалог выхода");
            }
        });


//        Узнаем IP-адрес устройства
        WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        Storage.IP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress % 8 & 0xff), (ipAddress % 16 & 0xff), (ipAddress % 24 & 0xff));

////        Узнаем модель смартфона
        Storage.phoneModel = android.os.Build.MODEL;
//        Узнаем версию Android
        Storage.androidVersion = android.os.Build.VERSION.RELEASE;
//        Узнаем имя оператора сотовой связи
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Storage.operatorName = manager.getNetworkOperatorName();

//        Узнаем размер экрана
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Storage.screenWidth = dm.widthPixels;
        Storage.screenHeight = dm.heightPixels;
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        Storage.screenDiagonal = (Math.rint(screenInches * 10) / 10);//returns the double value that is closest in value to the argument and is equal to a mathematical integer

//    public double screenDiagonal() {
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
//        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
//        double screenInches = Math.sqrt(x + y); Log.d("debug", "Screen inches : " + screenInches);
//
//        return truncate(screenInches, 1);
//    }
//    public static double truncate(double c, int n){
//        int d = (int) Math.pow(10, n);
//        double r = (Math.rint(c*d)/d);
//        return r;
//    }
        Storage.batteryCapacity = getBatteryCapacity();
        Storage.batteryLevel = getBatteryLevel();
    }


//        Узнаем емкость аккумулятора
    public Double getBatteryCapacity(){
        Object powerProfile = null;
        Double batteryCapacity = 0.0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try{
            powerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            batteryCapacity = (Double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(powerProfile, "battery.capacity");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }

//        Узнаем уровень заряда аккумулятора
    public int getBatteryLevel(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return (int) ((level/(float) scale)* 100.0f);
    }

    //пока не могу найти способ не задваивать метод авторизации
    //если выносить его в сторож, происходит рассинхронизация обработки результатов метода done,
    //что приводит к не штатному вызову SignInActivity.showLogInORLogOut()
    public void authorize(final Context context){
        // Отправляем данные на Parse.com для проверки
        LogInCallback logInCallback = new LogInCallback() {
            public void done(ParseUser user, ParseException error) {
                if (user != null) {
                    //пользователь авторизован. загрузим его сохраненные настройки из облака
                    Storage.currentUser = user;
//                    Storage.typeOfDateInput = Storage.stringToTypeOfInput(user.getString("typeOfDateInput"), context);
//                    Storage.typeOfTimeInput = Storage.stringToTypeOfInput(user.getString("typeOfTimeInput"), context);
                    Storage.timePickerStep = user.getInt("timePickerStep");
                    Storage.showListHint = user.getBoolean("showListHint");
                    Storage.youngIsOnTop = user.getBoolean("youngIsOnTop");
                    Storage.singleTapTimePick = user.getBoolean("singleTapTimePick");

                    if (user.getString("IMEI") != null && !user.getString("IMEI").equals("detached")) {
                        //пользователь авторизован и имеет привязанный IMEI. проверим его соответствие
                        Log.d(LOG_TAG, "IMEI OK? " + String.valueOf(Storage.deviceIMEI.equals(user.getString("IMEI"))));
                        if (Storage.deviceIMEI.equals(user.getString("IMEI"))) {
                            //пользователь авторизован, имеет привязанный IMEI и IMEI соответствует текущему устройству
                            Toast.makeText(context, "Здравствуйте, " + user.getUsername() + "\nПриятной работы", Toast.LENGTH_LONG).show();
                            Storage.userHasPaidAccess = true;
                        } else {
                            Toast.makeText(context,
                                    "Здравствуйте, " + user.getUsername() +
                                            "\nВы вошли в систему с другого устройства." +
                                            "\nСейчас платные опции недоступны." +
                                            "\nЧтобы привязать логин к новому устройству" +
                                            "\nперейдите в меню \"Учетные записи\"", Toast.LENGTH_LONG).show();
                            Storage.userHasPaidAccess = false;
                        }
                    } else {
                        //пользователь авторизован, но IMEI еще не привязан или отвязан по запросу
                        Storage.currentUser.put("IMEI", Storage.deviceIMEI);
                        Storage.currentUser.saveInBackground();
                        Toast.makeText(context, "Вы привязали это устройство" +
                                "\nк своей учетной записи." +
                                "\nПриятной работы", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //если логин-пароль "" или не соответствуют какой-либо УЗ на сервере
                    Log.d(LOG_TAG, "error code " + error.getCode());//всегда 101
                    Toast.makeText(context, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ParseUser.logInInBackground(Storage.userName, Storage.password, logInCallback);
    }

    @Override
    public void onBackPressed() {
        Storage.openQuitDialog(this);
    }
}
