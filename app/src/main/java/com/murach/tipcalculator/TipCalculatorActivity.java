package com.murach.tipcalculator;

import java.text.NumberFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class TipCalculatorActivity extends Activity
        implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;
    private Button   percentUpButton;
    private Button   percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;

    // define the SharedPreferences object
    private SharedPreferences savedValues;

    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;
    private String nameString = "";

    //declare a constant for the tag parameter
    private static final String TAG = "TipCalculatorActivity";

    //define rounding constants
    private final int ROUND_NONE = 0;
    private final int ROUND_TIP = 1;
    private final int ROUND_TOTAL = 2;

    //setup the preferences
    private SharedPreferences prefs;
    private boolean rememberTipPercent = true;
    private int rounding =ROUND_NONE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);

        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);

        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        //add a logcat trace
        Log.d(TAG, "onCreate method executed");

        //add a toast
        Toast t = Toast.makeText(this, "onCreate Method", Toast.LENGTH_SHORT);
        t.show();

        //set the default values for the prefs
        PreferenceManager.setDefaultValues(this,R.xml.preferences, false);

        //get the default shared prefs object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();

        super.onPause();

        Log.d(TAG, "onPause executed");

        //create a toast
        Toast t = Toast.makeText(this, "onPause method", Toast.LENGTH_LONG);
        t.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        //get preferences
        rememberTipPercent = prefs.getBoolean("pref_forget_percent", true);
        rounding = Integer.parseInt(prefs.getString("pref_rounding", "0"));

        //get preference for name
        nameString = prefs.getString("edit_text_preference_1"," ");

        // get the instance variables
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);

        // calculate and display
        calculateAndDisplay();

        Log.d(TAG, "onResume executed");

        //create a toast
        Toast t = Toast.makeText(this, "onResume method", Toast.LENGTH_SHORT);
        t.show();
    }

    public void calculateAndDisplay() {

        //get reference to nameTextView
        TextView nameTextView = (TextView) findViewById(R.id.name_output);

        //output the name input to the textView
        nameTextView.setText(nameString);

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }

        float tipAmount = 0;
        float totalAmount = 0;*
        float tipPercentDisplay = 0;

        if(rounding == ROUND_NONE){
            tipAmount = billAmount * tipPercent;
            totalAmount = billAmount + tipAmount;
        } else if (rounding == ROUND_TIP){
            tipAmount = StrictMath.round(billAmount * tipPercent);
            totalAmount = billAmount + tipAmount;
            tipPercentDisplay = tipAmount / billAmount;
        } else if (rounding == ROUND_TOTAL){
            float tipNotRounded = billAmount * tipPercent;
            //tipPercentDisplay = tipAmount - billAmount;
            tipAmount = tipNotRounded;
            totalAmount = StrictMath.round(billAmount + tipNotRounded);
        }

//        // calculate tip and total
//        float tipAmount = billAmount * tipPercent;
//        float totalAmount = billAmount + tipAmount;

        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));

        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu:
                startActivity(new Intent(getApplicationContext(),
                SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.percentDownButton:
                tipPercent = tipPercent - .01f;
                calculateAndDisplay();
                break;
            case R.id.percentUpButton:
                tipPercent = tipPercent + .01f;
                calculateAndDisplay();
                break;
        }
    }
}