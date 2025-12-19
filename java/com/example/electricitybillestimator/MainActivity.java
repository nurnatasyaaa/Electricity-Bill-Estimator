package com.example.electricitybillestimator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;

    Spinner spinnerMonth;
    EditText edtUnit;
    RadioGroup radioGroupRebate;
    TextView txtTotal, txtFinal;
    Button btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        // Link UI
        spinnerMonth = findViewById(R.id.spinnerMonth);
        edtUnit = findViewById(R.id.edtUnit);
        radioGroupRebate = findViewById(R.id.radioGroupRebate);
        txtTotal = findViewById(R.id.txtTotal);
        txtFinal = findViewById(R.id.txtFinal);
        btnCalculate = findViewById(R.id.btnCalculate);

        // Month spinner data
        String[] months = {
                "Please select the month",
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                months
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validation
                if (edtUnit.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Please enter electricity unit",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (radioGroupRebate.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(MainActivity.this,
                            "Please select rebate percentage",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (spinnerMonth.getSelectedItemPosition() == 0) {
                    Toast.makeText(MainActivity.this,
                            "Please select the month",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                double unit = Double.parseDouble(edtUnit.getText().toString());

                double total = calculateBill(unit);

                int selectedId = radioGroupRebate.getCheckedRadioButtonId();
                RadioButton rb = findViewById(selectedId);
                String rebateText = rb.getText().toString();
                double rebatePercent = Double.parseDouble(
                        rebateText.replace("%", "")
                ) / 100;

                double finalCost = total - (total * rebatePercent);

                DecimalFormat df = new DecimalFormat("0.00");

                txtTotal.setText("Total Charges: RM " + df.format(total));
                txtFinal.setText("Final Cost: RM " + df.format(finalCost));

                String month = spinnerMonth.getSelectedItem().toString();

                boolean inserted = dbHelper.insertBill(
                        month,
                        unit,
                        total,
                        rebatePercent * 100,
                        finalCost
                );

                if (inserted) {
                    Toast.makeText(MainActivity.this,
                            "Data saved successfully",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Failed to save data",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnHistory = findViewById(R.id.btnHistory);

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        Button btnAbout = findViewById(R.id.btnAbout);

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

    }   // ✅ THIS closes onCreate()

    // ✅ Method must be OUTSIDE onCreate
    private double calculateBill(double unit) {
        double total = 0;

        if (unit <= 200) {
            total = unit * 0.218;
        } else if (unit <= 300) {
            total = (200 * 0.218) + ((unit - 200) * 0.334);
        } else if (unit <= 600) {
            total = (200 * 0.218) +
                    (100 * 0.334) +
                    ((unit - 300) * 0.516);
        } else {
            total = (200 * 0.218) +
                    (100 * 0.334) +
                    (300 * 0.516) +
                    ((unit - 600) * 0.546);
        }

        return total;
    }
}
