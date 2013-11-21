package net.honeybadgerlabs.calcwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.math.BigDecimal;
import java.math.MathContext;

public class CalcWidgetProvider extends AppWidgetProvider {
  private static final String TAG = "CalcWidgetProvider";

  public static final String NUM0 = "net.honeybadgerlabs.calcwidget.0";
  public static final String NUM1 = "net.honeybadgerlabs.calcwidget.1";
  public static final String NUM2 = "net.honeybadgerlabs.calcwidget.2";
  public static final String NUM3 = "net.honeybadgerlabs.calcwidget.3";
  public static final String NUM4 = "net.honeybadgerlabs.calcwidget.4";
  public static final String NUM5 = "net.honeybadgerlabs.calcwidget.5";
  public static final String NUM6 = "net.honeybadgerlabs.calcwidget.6";
  public static final String NUM7 = "net.honeybadgerlabs.calcwidget.7";
  public static final String NUM8 = "net.honeybadgerlabs.calcwidget.8";
  public static final String NUM9 = "net.honeybadgerlabs.calcwidget.9";

  public static final String POINT = "net.honeybadgerlabs.calcwidget.point";
  public static final String BACKSPACE = "net.honeybadgerlabs.calcwidget.backspace";
  public static final String EQUALS = "net.honeybadgerlabs.calcwidget.equals";

  public static final String PLUS = "net.honeybadgerlabs.calcwidget.plus";
  public static final String MINUS = "net.honeybadgerlabs.calcwidget.minus";
  public static final String TIMES = "net.honeybadgerlabs.calcwidget.times";
  public static final String DIVIDE = "net.honeybadgerlabs.calcwidget.divide";

  public static final String CLEAR = "net.honeybadgerlabs.calcwidget.clear";
  public static final String FIRST = "net.honeybadgerlabs.calcwidget.first";
  public static final String SECOND = "net.honeybadgerlabs.calcwidget.second";
  public static final String OPERATOR = "net.honeybadgerlabs.calcwidget.operator";

  @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
    for (int id : ids) {
      updateWidget(context, manager, id);
    }
  }

  @Override public void onReceive(Context context, Intent intent) {
    int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);

    Log.d(TAG, "Pressed " + intent.getAction() + " on widget #" + id + ", clear: " + getClear(context, id));

    if (intent.getAction().equals(NUM0)) {
      addDigit(context, id, 0);
    } else if (intent.getAction().equals(NUM1)) {
      addDigit(context, id, 1);
    } else if (intent.getAction().equals(NUM2)) {
      addDigit(context, id, 2);
    } else if (intent.getAction().equals(NUM3)) {
      addDigit(context, id, 3);
    } else if (intent.getAction().equals(NUM4)) {
      addDigit(context, id, 4);
    } else if (intent.getAction().equals(NUM5)) {
      addDigit(context, id, 5);
    } else if (intent.getAction().equals(NUM6)) {
      addDigit(context, id, 6);
    } else if (intent.getAction().equals(NUM7)) {
      addDigit(context, id, 7);
    } else if (intent.getAction().equals(NUM8)) {
      addDigit(context, id, 8);
    } else if (intent.getAction().equals(NUM9)) {
      addDigit(context, id, 9);
    } else if (intent.getAction().equals(POINT)) {
      addDecimalPoint(context, id);
    } else if (intent.getAction().equals(BACKSPACE)) {
      removeLast(context, id);
    } else if (intent.getAction().equals(EQUALS)) {
      showResult(context, id);
    } else if (intent.getAction().equals(PLUS)) {
      setOperation(context, id, PLUS);
    } else if (intent.getAction().equals(MINUS)) {
      setOperation(context, id, MINUS);
    } else if (intent.getAction().equals(TIMES)) {
      setOperation(context, id, TIMES);
    } else if (intent.getAction().equals(DIVIDE)) {
      setOperation(context, id, DIVIDE);
    }

    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    int[] ids = manager.getAppWidgetIds(new ComponentName(context, CalcWidgetProvider.class));
    for (int id2: ids) {
      updateWidget(context, manager, id2);
    }

    super.onReceive(context, intent);
  }

  private void updateWidget(Context context, AppWidgetManager manager, int id) {
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calculator);

    String a = getValue(context, id, FIRST);
    String b = getValue(context, id, SECOND);
    String op = getValue(context, id, OPERATOR);

    if (!a.equals("")) {
      if (op.equals(PLUS)) {
        views.setTextViewText(R.id.expression, a + " +");
      } else if (op.equals(MINUS)) {
        views.setTextViewText(R.id.expression, a + " -");
      } else if (op.equals(TIMES)) {
        views.setTextViewText(R.id.expression, a + " ×");
      } else if (op.equals(DIVIDE)) {
        views.setTextViewText(R.id.expression, a + " ÷");
      } else {
        views.setTextViewText(R.id.expression, a);
      }
    } else {
      views.setTextViewText(R.id.expression, "");
    }

    views.setTextViewText(R.id.value, b);

    setListeners(context, id, views);

    manager.updateAppWidget(id, views);
  }

  private void addDigit(Context context, int id, int digit) {
    String value = getClear(context, id) ? "0" : getValue(context, id, SECOND);

    if (value.equals("0")) {
      if (digit > 0) {
        setValue(context, id, SECOND, "" + digit);
      } else {
        setValue(context, id, SECOND, "0");
      }
    } else if (value.equals("-") && digit == 0) {
      // do nothing
    } else {
      setValue(context, id, SECOND, value + digit);
    }

    setClear(context, id, false);
  }

  private void addDecimalPoint(Context context, int id) {
    String value = getClear(context, id) ? "0" : getValue(context, id, SECOND);

    if (!value.contains(".")) {
      setValue(context, id, SECOND, value + ".");
    }

    setClear(context, id, false);
  }

  private void removeLast(Context context, int id) {
    String value = getClear(context, id) ? "0" : getValue(context, id, SECOND);

    if (value.length() == 1) {
      setValue(context, id, SECOND, "0");
    } else {
      setValue(context, id, SECOND, value.substring(0, value.length() - 1));
    }

    setClear(context, id, false);
  }

  private void showResult(Context context, int id) {
    try {
      BigDecimal a = new BigDecimal(getValue(context, id, FIRST));
      BigDecimal b = new BigDecimal(getValue(context, id, SECOND));
      String op = getValue(context, id, OPERATOR);

      String result = "";
      if (op.equals(PLUS)) {
        result = a.add(b, MathContext.DECIMAL64).toString();
      } else if (op.equals(MINUS)) {
        result = a.subtract(b, MathContext.DECIMAL64).toString();
      } else if (op.equals(TIMES)) {
        result = a.multiply(b, MathContext.DECIMAL64).toString();
      } else if (op.equals(DIVIDE)) {
        result = a.divide(b, MathContext.DECIMAL64).toString();
      }

      setValue(context, id, SECOND, result);
    } catch (Exception e) {
      e.printStackTrace();
    }

    setValue(context, id, FIRST, "");
    setValue(context, id, OPERATOR, "");

    setClear(context, id, true);
  }

  private void setOperation(Context context, int id, String operation) {
    String op = getValue(context, id, OPERATOR);
    String b = getValue(context, id, SECOND);

    if (operation.equals(MINUS) && b.equals("0")) {
      setValue(context, id, SECOND, "-");
      setClear(context, id, false);
      return;
    }

    showResult(context, id);

    setValue(context, id, OPERATOR, operation);
    setValue(context, id, FIRST, b);
    setValue(context, id, SECOND, "0");
  }

  private void setListeners(Context context, int id, RemoteViews views) {
    final Intent intent = new Intent(context, CalcWidgetProvider.class);
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);

    int sid = id << 5;

    intent.setAction(NUM0);
    views.setOnClickPendingIntent(R.id.num0, PendingIntent.getBroadcast(context, sid + 0, intent, 0));

    intent.setAction(NUM1);
    views.setOnClickPendingIntent(R.id.num1, PendingIntent.getBroadcast(context, sid + 1, intent, 0));

    intent.setAction(NUM2);
    views.setOnClickPendingIntent(R.id.num2, PendingIntent.getBroadcast(context, sid + 2, intent, 0));

    intent.setAction(NUM3);
    views.setOnClickPendingIntent(R.id.num3, PendingIntent.getBroadcast(context, sid + 3, intent, 0));

    intent.setAction(NUM4);
    views.setOnClickPendingIntent(R.id.num4, PendingIntent.getBroadcast(context, sid + 4, intent, 0));

    intent.setAction(NUM5);
    views.setOnClickPendingIntent(R.id.num5, PendingIntent.getBroadcast(context, sid + 5, intent, 0));

    intent.setAction(NUM6);
    views.setOnClickPendingIntent(R.id.num6, PendingIntent.getBroadcast(context, sid + 6, intent, 0));

    intent.setAction(NUM7);
    views.setOnClickPendingIntent(R.id.num7, PendingIntent.getBroadcast(context, sid + 7, intent, 0));

    intent.setAction(NUM8);
    views.setOnClickPendingIntent(R.id.num8, PendingIntent.getBroadcast(context, sid + 8, intent, 0));

    intent.setAction(NUM9);
    views.setOnClickPendingIntent(R.id.num9, PendingIntent.getBroadcast(context, sid + 9, intent, 0));

    intent.setAction(POINT);
    views.setOnClickPendingIntent(R.id.point, PendingIntent.getBroadcast(context, sid + 10, intent, 0));

    intent.setAction(BACKSPACE);
    views.setOnClickPendingIntent(R.id.backspace, PendingIntent.getBroadcast(context, sid + 11, intent, 0));

    intent.setAction(EQUALS);
    views.setOnClickPendingIntent(R.id.equals, PendingIntent.getBroadcast(context, sid + 12, intent, 0));

    intent.setAction(PLUS);
    views.setOnClickPendingIntent(R.id.plus, PendingIntent.getBroadcast(context, sid + 13, intent, 0));

    intent.setAction(MINUS);
    views.setOnClickPendingIntent(R.id.minus, PendingIntent.getBroadcast(context, sid + 14, intent, 0));

    intent.setAction(TIMES);
    views.setOnClickPendingIntent(R.id.times, PendingIntent.getBroadcast(context, sid + 15, intent, 0));

    intent.setAction(DIVIDE);
    views.setOnClickPendingIntent(R.id.divide, PendingIntent.getBroadcast(context, sid + 16, intent, 0));
  }

  private String getValue(Context context, int id, String name) {
    return PreferenceManager.getDefaultSharedPreferences(context).getString(name + id, "");
  }

  private void setValue(Context context, int id, String name, String value) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(name + id, value).commit();
  }

  private boolean getClear(Context context, int id) {
    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(CLEAR + id, false);
  }

  private void setClear(Context context, int id, boolean value) {
    Log.d(TAG, "Setting clear to " + value + " for widget #" + id);
    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(CLEAR + id, value).commit();
  }
}
