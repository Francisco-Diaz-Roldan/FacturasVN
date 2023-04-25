package com.example.facturas;

public class Factura {
    private String descEstado;
    private double importeOrdenacion;
    private String fecha;

    public Factura(String descEstado, double importeOrdenacion, String fecha) {
        this.descEstado = descEstado;
        this.importeOrdenacion = importeOrdenacion;
        this.fecha = fecha;
    }

    public String getDescEstado() {
        return descEstado;
    }

    public void setDescEstado(String descEstado) {
        this.descEstado = descEstado;
    }

    public double getImporteOrdenacion() {
        return importeOrdenacion;
    }

    public void setImporteOrdenacion(double importeOrdenacion) {
        this.importeOrdenacion = importeOrdenacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }


}
