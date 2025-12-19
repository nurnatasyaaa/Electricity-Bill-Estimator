package com.example.electricitybillestimator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listViewBills;
    DBHelper dbHelper;

    ArrayList<String> billList;
    ArrayList<Integer> billIdList;
    ArrayAdapter<String> adapter;   // ✅ MUST EXIST

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewBills = findViewById(R.id.listViewBills);
        dbHelper = new DBHelper(this);

        billList = new ArrayList<>();
        billIdList = new ArrayList<>();

        // ✅ CREATE ADAPTER
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                billList
        );

        listViewBills.setAdapter(adapter);

        // ✅ LOAD DATA
        loadBills();

        // ✅ CLICK ITEM → OPEN DETAIL
        listViewBills.setOnItemClickListener((parent, view, position, id) -> {
            int billId = billIdList.get(position);

            Intent intent = new Intent(
                    HistoryActivity.this,
                    DetailActivity.class
            );
            intent.putExtra("BILL_ID", billId);
            startActivityForResult(intent, 1);
        });
    }

    private void loadBills() {

        billList.clear();
        billIdList.clear();

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT id, month, final FROM bill ORDER BY id DESC",
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String month = cursor.getString(1);
            double finalCost = cursor.getDouble(2);

            billList.add(month + " - RM " + String.format("%.2f", finalCost));
            billIdList.add(id);
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            loadBills(); // ✅ reload after delete
        }
    }
}
