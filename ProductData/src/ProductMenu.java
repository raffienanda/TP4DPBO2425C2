import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ProductMenu extends JFrame {
    public static void main(String[] args) {
        // buat object window
        ProductMenu menu = new ProductMenu();

        // atur ukuran window
        menu.setSize(700, 600);

        // letakkan window di tengah layar
        menu.setLocationRelativeTo(null);

        // isi window
        menu.setContentPane(menu.mainPanel);

        // ubah warna background
        menu.getContentPane().setBackground(Color.WHITE);

        // tampilkan window
        menu.setVisible(true);

        // agar program ikut berhenti saat window diclose
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua produk
    private ArrayList<Product> listProduct;

    private JPanel mainPanel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField hargaField;
    private JTable productTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox<String> kategoriComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel idLabel;
    private JLabel namaLabel;
    private JLabel hargaLabel;
    private JLabel kategoriLabel;
    private JRadioButton elektronikRadioButton;
    private JRadioButton alatTulisRadioButton;
    private JRadioButton makananRadioButton;
    private JRadioButton pakaianRadioButton;
    private JRadioButton minumanRadioButton;
    private JRadioButton semuaRadioButton;

    // constructor
    public ProductMenu() {
        // inisialisasi listProduct
        listProduct = new ArrayList<>();

        // isi listProduct
        populateList();

        // isi tabel produk
        productTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] kategoriData = {"???", "Elektronik", "Makanan", "Minuman", "Pakaian", "Alat Tulis"};
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedIndex == -1){
                    insertData();
                }else{
                    updateData();
                }
            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: tambahkan konfirmasi sebelum menghapus data
                int confirm = JOptionPane.showConfirmDialog(null, "Yakin kah mau dihapus?", "Konfirmasi hapus", JOptionPane.YES_NO_OPTION);
                if(confirm == JOptionPane.YES_NO_OPTION){
                    deleteData();
                }
            }
        });
        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        // saat salah satu baris tabel ditekan
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = productTable.getSelectedRow();

                // simpan value textfield dan combo box
                String curId = productTable.getModel().getValueAt(selectedIndex, 1).toString();
                String curNama = productTable.getModel().getValueAt(selectedIndex, 2).toString();
                String curHarga = productTable.getModel().getValueAt(selectedIndex, 3).toString();
                String curKategori = productTable.getModel().getValueAt(selectedIndex, 4).toString();

                // ubah isi textfield dan combo box
                idField.setText(curId);
                namaField.setText((curNama));
                hargaField.setText((curHarga));
                kategoriComboBox.setSelectedItem(curKategori);

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");

                // tampilkan button delete
                deleteButton.setVisible(true);

            }
        });

        // grupkan semua radio button
        ButtonGroup group = new ButtonGroup();
        group.add(elektronikRadioButton);
        group.add(alatTulisRadioButton);
        group.add(makananRadioButton);
        group.add(pakaianRadioButton);
        group.add(minumanRadioButton);
        group.add(semuaRadioButton);

        ActionListener filterListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String kategoriDipilih = ((JRadioButton) e.getSource()).getText();
                filterTable(kategoriDipilih);
            }
        };

        // pasangkan listener ke semua radio button
        elektronikRadioButton.addActionListener(filterListener);
        alatTulisRadioButton.addActionListener(filterListener);
        makananRadioButton.addActionListener(filterListener);
        pakaianRadioButton.addActionListener(filterListener);
        minumanRadioButton.addActionListener(filterListener);

        // jika ada tombol "Semua"
        semuaRadioButton.addActionListener(filterListener);

        semuaRadioButton.setSelected(true);
        filterTable("Semua");

    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] cols = {"No", "ID Produk", "Nama", "Harga", "Kategori"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        // isi tabel dengan listProduct
        for(int i = 0; i < listProduct.size(); i++){
            Object[] row = {i + 1,
                    listProduct.get(i).getId(),
                    listProduct.get(i).getNama(),
                    String.format("%.2f", listProduct.get(i).getHarga()),
                    listProduct.get(i).getKategori()
            };
            tmp.addRow(row);
        }

        return tmp; // return juga harus diganti
    }

    public void insertData() {
        try{
            // ambil value dari textfield dan combobox
            String id = idField.getText();
            String nama = namaField.getText();
            double harga = Double.parseDouble(hargaField.getText());
            String kategori = kategoriComboBox.getSelectedItem().toString();

            // tambahkan data ke dalam list
            listProduct.add(new Product(id, nama, harga, kategori));

            // update tabel
            productTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Insert berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
        }catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void updateData() {
        try{
            // ambil data dari form
            String id = idField.getText();
            String nama = namaField.getText();
            double harga = Double.parseDouble(hargaField.getText());
            String kategori = kategoriComboBox.getSelectedItem().toString();

            // ubah data produk di list
            listProduct.get(selectedIndex).setId(id);
            listProduct.get(selectedIndex).setNama(nama);
            listProduct.get(selectedIndex).setHarga(harga);
            listProduct.get(selectedIndex).setKategori(kategori);

            // update tabel
            productTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println(("Update berhasil"));
            JOptionPane.showMessageDialog(null, "Data berhasil diubah");
        }catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(null, "Harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void deleteData() {
        // hapus data dari list
        listProduct.remove(selectedIndex);

        // update tabel
        productTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Delete berhasil");
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
    }

    public void clearForm() {
        // kosongkan semua texfield dan combo box
        idField.setText("");
        namaField.setText("");
        hargaField.setText("");
        kategoriComboBox.setSelectedIndex(0);

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }

    // panggil prosedur ini untuk mengisi list produk
    private void populateList() {
        listProduct.add(new Product("P001", "Laptop Asus", 8500000.0, "Elektronik"));
        listProduct.add(new Product("P002", "Mouse Logitech", 350000.0, "Elektronik"));
        listProduct.add(new Product("P003", "Keyboard Mechanical", 750000.0, "Elektronik"));
        listProduct.add(new Product("P004", "Roti Tawar", 15000.0, "Makanan"));
        listProduct.add(new Product("P005", "Susu UHT", 12000.0, "Minuman"));
        listProduct.add(new Product("P006", "Kemeja Putih", 125000.0, "Pakaian"));
        listProduct.add(new Product("P007", "Celana Jeans", 200000.0, "Pakaian"));
        listProduct.add(new Product("P008", "Pensil 2B", 3000.0, "Alat Tulis"));
        listProduct.add(new Product("P009", "Buku Tulis", 8000.0, "Alat Tulis"));
        listProduct.add(new Product("P010", "Air Mineral", 5000.0, "Minuman"));
        listProduct.add(new Product("P011", "Smartphone Samsung", 4500000.0, "Elektronik"));
        listProduct.add(new Product("P012", "Kue Brownies", 25000.0, "Makanan"));
        listProduct.add(new Product("P013", "Jaket Hoodie", 180000.0, "Pakaian"));
        listProduct.add(new Product("P014", "Pulpen Gel", 5000.0, "Alat Tulis"));
        listProduct.add(new Product("P015", "Teh Botol", 8000.0, "Minuman"));
    }

    private void filterTable(String kategori) {
        Object[] cols = {"No", "ID Produk", "Nama", "Harga", "Kategori"};
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        // Kalau kategori = "Semua", tampilkan semua produk
        for (int i = 0; i < listProduct.size(); i++) {
            Product p = listProduct.get(i);
            if (kategori.equals("Semua") || p.getKategori().equalsIgnoreCase(kategori)) {
                Object[] row = {
                        tmp.getRowCount() + 1,
                        p.getId(),
                        p.getNama(),
                        String.format("%.2f", p.getHarga()),
                        p.getKategori()
                };
                tmp.addRow(row);
            }
        }

        productTable.setModel(tmp);
    }
}