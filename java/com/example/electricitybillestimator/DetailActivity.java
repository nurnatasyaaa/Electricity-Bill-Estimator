package com.example.electricitybillestimator;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    TextView txtMonth, txtUnit, txtTotal, txtRebate, txtFinal;
    Button btnDelete;
    DBHelper dbHelper;
    int billId;   // <-- make it class-level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        txtMonth = findViewById(R.id.txtMonth);
        txtUnit = findViewById(R.id.txtUnit);
        txtTotal = findViewById(R.id.txtTotal);
        txtRebate = findViewById(R.id.txtRebate);
        txtFinal = findViewById(R.id.txtFinal);
        btnDelete = findViewById(R.id.btnDelete);  // <-- IMPORTANT

        dbHelper = new DBHelper(this);

        billId = getIntent().getIntExtra("BILL_ID", -1);

        if (billId != -1) {
            loadBillDetail(billId);

            btnDelete.setOnClickListener(v -> {
                dbHelper.getWritableDatabase().delete(
                        "bill",
                        "id=?",
                        new String[]{String.valueOf(billId)}
                );

                Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK); // tell History to refresh
                finish();             // go back to History page
            });
        }
    }

    private void loadBillDetail(int id) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM bill WHERE id=?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {
            txtMonth.setText("Month: " + cursor.getString(1));
            txtUnit.setText("Unit Used: " + cursor.getDouble(2) + " kWh");
            txtTotal.setText("Total Charges: RM " +
                    String.format("%.2f", cursor.getDouble(3)));
            txtRebate.setText("Rebate: " +
                    cursor.getDouble(4) + "%");
            txtFinal.setText("Final Cost: RM " +
                    String.format("%.2f", cursor.getDouble(5)));
        }

        cursor.close();
    }
}
