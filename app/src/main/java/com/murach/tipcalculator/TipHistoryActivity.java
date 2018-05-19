package com.murach.tipcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.murach.tipcalculator.R.layout.activity_tip_history;

public class TipHistoryActivity extends Activity {

    private ListView tipListView;
    private tipDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_tip_history);
        Toast.makeText(getApplicationContext(),"Tip History onCreate", Toast.LENGTH_SHORT).show();

        tipListView = (ListView) findViewById(R.id.tipListView);
        db = new tipDB(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        //get the tips from the database
        ArrayList<Tip> tips = db.getTips();
        TipListAdapter adapter = new TipListAdapter(this, tips);
        tipListView.setAdapter(adapter);

    }
}
