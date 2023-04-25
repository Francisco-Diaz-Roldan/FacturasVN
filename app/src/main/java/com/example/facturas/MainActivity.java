package com.example.facturas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.slider.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Factura> listaFacturas;
    private RequestQueue rq;
    private RecyclerView rv1;
    private AdaptadorFactura adaptadorFactura;
    private MainActivity Instance=this;

    public static String maxImporte = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        MenuHost menu=this;

        menu.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.menuFiltros:
                        Intent intent = new Intent(Instance,ActividadFiltrar.class);
                        startActivity(intent);
                        return true;
                }

                return false;
            }
        });

        listaFacturas = new ArrayList<>();
        rq= Volley.newRequestQueue(this);
            cargarFactura();
        rv1=findViewById(R.id.rv1);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        rv1.setLayoutManager(linearLayoutManager);
        adaptadorFactura = new AdaptadorFactura();
        rv1.setAdapter(adaptadorFactura);

    }



    private void cargarFactura() {
        String url = "https://viewnextandroid.wiremockapi.cloud/facturas";
        JsonObjectRequest requerimiento = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String valor = response.get("facturas").toString();
                            JSONArray arreglo = new JSONArray(valor);
                            for (int f=0; f<arreglo.length(); f++) {
                                JSONObject objeto = new JSONObject(arreglo.get(f).toString());
                                String descEstado = objeto.getString("descEstado");
                                Double importeOrdenacion = objeto.getDouble("importeOrdenacion");
                                String fecha = objeto.getString("fecha");
                                Factura factura = new Factura(descEstado, importeOrdenacion, fecha);
                                listaFacturas.add(factura);
                            }
                                maxImporte = String.valueOf(listaFacturas.stream().max(Comparator.comparing(Factura::getImporteOrdenacion)).get().getImporteOrdenacion());

                            Bundle extras = getIntent().getExtras();

                            if (extras != null) {
                                ArrayList<Factura> listFiltro = new ArrayList<>();

                                double importeFiltro = getIntent().getDoubleExtra("importe", Double.parseDouble(maxImporte));

                                for (Factura factura : listaFacturas) {
                                    if (factura.getImporteOrdenacion() < importeFiltro) {
                                        listFiltro.add(factura);
                                    }
                                }

                                boolean chbxPagadas = getIntent().getBooleanExtra("pagada", false);
                                boolean chbxAnuladas = getIntent().getBooleanExtra("anulada", false);
                                boolean chbxPendientesPago = getIntent().getBooleanExtra("pendientePago", false);
                                boolean chbxCuotaFija = getIntent().getBooleanExtra("cuotaFija", false);
                                boolean chbxPlanPago = getIntent().getBooleanExtra("planPago", false);

                                //Log.d("prueba", chbxPendientesPago+"");
                                //lista solo para checkbox

                                if(chbxPagadas || chbxAnuladas || chbxPendientesPago || chbxCuotaFija || chbxPlanPago){
                                    ArrayList<Factura> listFiltro2 = new ArrayList<>();

                                    for (Factura factura : listFiltro) {

                                        if (factura.getDescEstado().equals("Pagada") && chbxPagadas) {
                                            listFiltro2.add(factura);
                                        }
                                        if (factura.getDescEstado().equals("Anuladas") && chbxAnuladas){
                                            listFiltro2.add(factura);
                                        }
                                        if (factura.getDescEstado().equals("Pendiente de pago") && chbxPendientesPago){
                                            listFiltro2.add(factura);
                                        }
                                        if (factura.getDescEstado().equals("Cuota fija") && chbxCuotaFija){
                                            listFiltro2.add(factura);
                                        }
                                        if (factura.getDescEstado().equals("Plan de pago") && chbxPlanPago){
                                            listFiltro2.add(factura);
                                        }
                                    }
                                    listFiltro = listFiltro2;
                                }

                                if (!getIntent().getStringExtra("fechaDesde").equals("Desde") && !getIntent().getStringExtra("fechaHasta").equals("Hasta")) {
                                    ArrayList<Factura> facturasFiltradas = new ArrayList<>();


                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyyy");
                                    Date fechaDesde = null;
                                    Date fechaHasta = null;

                                    fechaDesde = sdf.parse(getIntent().getStringExtra("fechaDesde"));
                                    fechaHasta = sdf.parse(getIntent().getStringExtra("fechaHasta"));


                                    for (Factura factura : listFiltro) {
                                        Date fechaFactura = sdf.parse(factura.getFecha());
                                        if (fechaFactura.after(fechaDesde) && fechaFactura.before(fechaHasta)) {
                                            facturasFiltradas.add(factura);
                                        }
                                    }
                                    listFiltro = facturasFiltradas;
                                }

                                listaFacturas = listFiltro;

                                TextView textView = new TextView(Instance);
                                textView.setText("No hay facturas");
                                textView.setTextSize(24);
                                textView.setVisibility(View.INVISIBLE);

                                if (listaFacturas.isEmpty()) {
                                    textView.setVisibility(View.VISIBLE);
                                    RelativeLayout relativeLayout = new RelativeLayout(Instance);
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                    relativeLayout.addView(textView, params);
                                    setContentView(relativeLayout);
                                }
                            }
                            adaptadorFactura.notifyItemRangeInserted(listaFacturas.size(), 1);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                error -> {
                });
        rq.add(requerimiento);
    }

    private class AdaptadorFactura extends RecyclerView.Adapter<AdaptadorFactura.AdaptadorFacturaHolder> {
        @NonNull
        @Override
        public AdaptadorFacturaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AdaptadorFacturaHolder(getLayoutInflater().inflate(R.layout.layout_factura, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorFacturaHolder holder, int position) {
            holder.imprimir(position);
        }

        @Override
        public int getItemCount() {
            return listaFacturas.size();
        }

        class AdaptadorFacturaHolder extends RecyclerView.ViewHolder implements com.example.facturas.AdaptadorFacturaHolder, View.OnClickListener {
            Dialog mDialog;

            TextView tvDescEstado, tvFecha, tvImporteOrdenacion;

            public AdaptadorFacturaHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                tvDescEstado = itemView.findViewById(R.id.tvDescEstado);
                tvFecha = itemView.findViewById(R.id.tvFecha);
                tvImporteOrdenacion = itemView.findViewById(R.id.tvImporteOrdenacion);
                mDialog = new Dialog(itemView.getContext());
            }

            public void imprimir(int position) {
                String estado = listaFacturas.get(position).getDescEstado();
                tvDescEstado.setText(estado);

                if (estado.equals("Pagada")) {
                    tvDescEstado.setTextColor(Color.GREEN);
                } else if (estado.equals("Pendiente de pago")) {
                    tvDescEstado.setTextColor(Color.RED);
                } else {
                    tvDescEstado.setTextColor(Color.BLACK);
                }

                tvFecha.setText(listaFacturas.get(position).getFecha());
                tvImporteOrdenacion.setText(listaFacturas.get(position).getImporteOrdenacion() + "€");
            }

            @Override
            public void onClick(View v) {
                mDialog.setContentView(R.layout.popup_main);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView mensajePopup = mDialog.findViewById(R.id.mensajePopup);
                mensajePopup.setText("Esta funcionalidad aún no está disponible");
                mDialog.show();
                Button cerrarButton = mDialog.findViewById(R.id.botonCerrar);
                cerrarButton.setOnClickListener(new View.OnClickListener() {

                    // Para cerrar el diálogo al pulsar el botón "CERRAR"
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
            }
        }
    }
}



