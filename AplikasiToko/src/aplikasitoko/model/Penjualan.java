package aplikasitoko.model;

import java.time.LocalDate;

public class Penjualan {
    private int       idJual;
    private LocalDate tglTransaksi;
    private String    idCustomer;
    private String    idBarang;
    private int       jumlahBeli;
    private double    totalBayar;
    private int       idUser;

    public Penjualan() {}

    public Penjualan(int idJual, LocalDate tglTransaksi, String idCustomer,
                     String idBarang, int jumlahBeli, double totalBayar, int idUser) {
        this.idJual       = idJual;
        this.tglTransaksi = tglTransaksi;
        this.idCustomer   = idCustomer;
        this.idBarang     = idBarang;
        this.jumlahBeli   = jumlahBeli;
        this.totalBayar   = totalBayar;
        this.idUser       = idUser;
    }

    public int       getIdJual()       { return idJual; }
    public LocalDate getTglTransaksi() { return tglTransaksi; }
    public String    getIdCustomer()   { return idCustomer; }
    public String    getIdBarang()     { return idBarang; }
    public int       getJumlahBeli()   { return jumlahBeli; }
    public double    getTotalBayar()   { return totalBayar; }
    public int       getIdUser()       { return idUser; }

    public void setIdJual(int idJual)               { this.idJual = idJual; }
    public void setTglTransaksi(LocalDate d)        { this.tglTransaksi = d; }
    public void setIdCustomer(String idCustomer)    { this.idCustomer = idCustomer; }
    public void setIdBarang(String idBarang)        { this.idBarang = idBarang; }
    public void setJumlahBeli(int jumlahBeli)       { this.jumlahBeli = jumlahBeli; }
    public void setTotalBayar(double totalBayar)    { this.totalBayar = totalBayar; }
    public void setIdUser(int idUser)               { this.idUser = idUser; }
}