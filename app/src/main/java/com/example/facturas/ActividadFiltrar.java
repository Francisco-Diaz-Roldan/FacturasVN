package com.example.facturas;


import static com.example.facturas.MainActivity.maxImporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.util.Calendar;
import java.util.Comparator;

public class ActividadFiltrar extends AppCompatActivity {
    private ActividadFiltrar instance=this;

    private Activity filtrosActivity = this;

    TextView main_sliderVal;
    SeekBar main_slider;

    Button fechaDesde;
    Button fechaHasta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrar);

        main_slider=findViewById(R.id.main_slider);
        main_sliderVal=findViewById(R.id.main_sliderVal);
        main_slider.setMax((int) Math.floor(Double.parseDouble(maxImporte.isEmpty()?"0": maxImporte))+1);
        main_slider.setProgress(main_slider.getMax());
        main_sliderVal.setText(String.valueOf(main_slider.getMax()));
        TextView tvMaxSeekbar = (TextView) findViewById(R.id.tvMaxSeekbar);
        tvMaxSeekbar.setText(String.valueOf((int)Math.floor(Double.parseDouble(maxImporte))+1));

        main_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                main_sliderVal.setText((seekBar.getProgress())+ "€");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Para hacer que la X cierre
        MenuHost menu = this;
        menu.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_close, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menuClose:
                        Intent intent = new Intent(filtrosActivity, MainActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        fechaDesde = findViewById(R.id.fechaDesde);
        fechaHasta = findViewById(R.id.fechaHasta);
        //Para hacer que los botones de fecha funcionen

        fechaDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(ActividadFiltrar.this, (view, year1, monthofyear, dayofmonth) ->
                        fechaDesde.setText(dayofmonth + "/" + (monthofyear+1) + "/" + year1), year, month, day);
                dpd.show();
            }
        });

        fechaHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(ActividadFiltrar.this, (view, year1, monthofyear, dayofmonth) -> fechaHasta.setText(dayofmonth + "/" + (monthofyear+1) + "/" + year1), year, month, day);
                dpd.show();
            }
        });

        Button botonFiltrar = findViewById(R.id.aplicarFiltros);
        //Button botonEliminar = findViewById(R.id.eliminarFiltros);
        CheckBox chbxPagadas = (CheckBox) findViewById(R.id.chbxPagadas);
        CheckBox chbxAnuladas = (CheckBox) findViewById(R.id.chbxAnuladas);
        CheckBox chbxCuotaFija = (CheckBox) findViewById(R.id.chbxCuotaFija);
        CheckBox chbxPlanPago = (CheckBox) findViewById(R.id.chbxPlanPago);
        CheckBox chbxPendientesPago = (CheckBox) findViewById(R.id.chbxPendientesPago);


        botonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(instance, MainActivity.class);
                intent.putExtra("importe", (double) main_slider.getProgress());
                intent.putExtra("pagada", chbxPagadas.isChecked());
                intent.putExtra("anulada", chbxAnuladas.isChecked());
                intent.putExtra("cuotaFija", chbxCuotaFija.isChecked());
                intent.putExtra("planPago", chbxPlanPago.isChecked());
                intent.putExtra("pendientePago", chbxPendientesPago.isChecked());
                intent.putExtra("fechaDesde", fechaDesde.getText().toString());
                intent.putExtra("fechaHasta", fechaHasta.getText().toString());

                startActivity(intent);
            }
        });

        Button resetFiltrosButton = findViewById(R.id.eliminarFiltros);
        resetFiltrosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFiltros();
            }
        });
    }
        private void resetFiltros() {
// Restablecer valores de fecha
            Button fechaDesde = findViewById(R.id.fechaDesde);
            fechaDesde.setText("Desde");
            Button fechaHasta = findViewById(R.id.fechaHasta);
            fechaHasta.setText("Hasta");

// Restablecer valor de seekBar
            SeekBar seekBar = findViewById(R.id.main_slider);
            int maxImporte = ((int) Double.parseDouble(MainActivity.maxImporte)) + 1;
            seekBar.setMax(maxImporte);
            seekBar.setProgress(maxImporte);
            TextView tvValorImporte = findViewById(R.id.tvImporte);
            tvValorImporte.setText(String.valueOf(maxImporte));

// Restablecer valores de checkboxes
            CheckBox pagadas = findViewById(R.id.chbxPagadas);
            pagadas.setChecked(false);
            CheckBox anuladas = findViewById(R.id.chbxAnuladas);
            anuladas.setChecked(false);
            CheckBox cuotaFija = findViewById(R.id.chbxCuotaFija);
            cuotaFija.setChecked(false);
            CheckBox pendientesPago = findViewById(R.id.chbxPendientesPago);
            pendientesPago.setChecked(false);
            CheckBox planPago = findViewById(R.id.chbxPlanPago);
            planPago.setChecked(false);
        }

    }






