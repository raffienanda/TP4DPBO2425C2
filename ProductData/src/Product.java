public class Product {
    private String id;
    private String nama;
    private double harga;
    private String kategori;
    private int stok;

    // compatibility constructor (jika ada kode lama yang pakai 4 parameter)
    public Product(String id, String nama, double harga, String kategori) {
        this(id, nama, harga, kategori, 0);
    }

    // constructor lengkap
    public Product(String id, String nama, double harga, String kategori, int stok) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
        this.stok = stok;
    }

    // setter
    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setHarga(double harga) { this.harga = harga; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setStok(int stok) { this.stok = stok; }

    // getter
    public String getId() { return this.id; }
    public String getNama() { return this.nama; }
    public double getHarga() { return this.harga; }
    public String getKategori() { return this.kategori; }
    public int getStok() { return this.stok; }
}
