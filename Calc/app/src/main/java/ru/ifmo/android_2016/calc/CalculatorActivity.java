package ru.ifmo.android_2016.calc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


/**
 * Created by alexey.nikitin on 13.09.16.
 */

public final class CalculatorActivity extends Activity {

    private double num;
    private TextView screen;
    private String op = "OP";
    private String second = "SECOND";
    private String first = "FIRST";
    private State state;
    private static final String TAG = CalculatorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        num = Double.NaN;
        state = State.EMPTY;
        screen = (TextView) findViewById(R.id.result);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(op, state.toString());
        outState.putString(second, screen.getText().toString());
        if (num != Double.NaN) {
            outState.putDouble(first, num);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        double val = savedInstanceState.getDouble(first, Double.NaN);
        if (val != Double.NaN) {
            num = val;
        }
        state = State.valueOf((String) savedInstanceState.get(op));
        screen.setText(savedInstanceState.getString(second));
    }

    public void clearClick(View view) {
        screen.setText("0");
        num = Double.NaN;
        state = State.EMPTY;
    }

    public void digitClick(View view) {
        if (state == State.RES) {
            this.clearClick(view);
        }
        StringBuilder s = new StringBuilder(screen.getText().toString());
        String digit = ((TextView) view).getText().toString();
        if (digit.equals(".") && s.indexOf(".") != -1) {
            return;
        }
        if (s.length() >= 10) {
            return;
        }
        s.append(digit);
        if (s.charAt(0) == '0' && s.length() > 1 && s.charAt(1) != '.') {
            s.deleteCharAt(0);
        }
        screen.setText(s.toString());
    }

    public void opClick(View view) {
        Double parsed = Double.parseDouble(screen.getText().toString());
        State op = State.getOp(((TextView) view).getText().toString());
        if (!state.isOp()) {
            num = parsed;
            state = op;
        } else {
            num = calc(parsed, num, state);
            state = op;
        }
        screen.setText("0");
    }

    private Double calc(Double a, Double b, State op) {
        switch (op) {
            case ADD:
                return b + a;
            case SUB:
                return b - a;
            case MUL:
                return b * a;
            case DIV:
                return b / a;
            default:
                return Double.NaN;
        }
    }

    public void resClick(View view) {
        if (!state.isOp()) {
            return;
        }
        double res = Double.parseDouble(screen.getText().toString());
        res = calc(res, num, state);

        String ans = Double.toString(res);
        if (ans.length() > 10)
        {
            if (res < 1) {
                ans = new DecimalFormat("#.#####E0", DecimalFormatSymbols.getInstance(Locale.UK)).format(res);
            } else {
                ans = new DecimalFormat("0.#######", DecimalFormatSymbols.getInstance(Locale.UK)).format(res);
            }
        }

        screen.setText(ans);
        state = State.RES;
    }

    private enum State {
        EMPTY, ADD, SUB, MUL, DIV, RES;

        public static State getOp(String op) {
            switch (op) {
                case "+":
                    return ADD;
                case "-":
                    return SUB;
                case "*":
                    return MUL;
                case "/":
                    return DIV;
                default:
                    return EMPTY;
            }
        }

        public boolean isOp() {
            return this != RES && this != EMPTY;
        }

    }
}