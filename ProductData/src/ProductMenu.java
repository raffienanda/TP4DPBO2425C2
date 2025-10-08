import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ProductMenu extends JFrame {
    public static void main(String[] args) {
        ProductMenu menu = new ProductMenu();
        menu.setSize(700, 600);
        menu.setLocationRelativeTo(null);
        menu.setContentPane(menu.mainPanel);
        menu.getContentPane().setBackground(Color.WHITE);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private int selectedIndex = -1;
    private ArrayList<Product> listProduct;

    private JPanel mainPanel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField hargaField;
    private JTextField stokField;
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

    public ProductMenu() {
        listProduct = new ArrayList<>();
        populateList();
        productTable.setModel(setTable());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        String[] kategoriData = {"???", "Elektronik", "Makanan", "Minuman", "Pakaian", "Alat Tulis"};
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));
        deleteButton.setVisible(false);

        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Yakin kah mau dihapus?", "Konfirmasi hapus", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteData();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int viewRow = productTable.getSelectedRow();
                if (viewRow == -1) return;

                // ambil data dari model yang sedang tampil
                String curId = productTable.getModel().getValueAt(viewRow, 1).toString();
                String curNama = productTable.getModel().getValueAt(viewRow, 2).toString();
                String curHarga = productTable.getModel().getValueAt(viewRow, 3).toString();
                String curKategori = productTable.getModel().getValueAt(viewRow, 4).toString();
                String curStok = productTable.getModel().getValueAt(viewRow, 5).toString();

                // cari index asli di listProduct berdasarkan ID (agar update/delete benar walau sedang difilter)
                int foundIndex = -1;
                for (int i = 0; i < listProduct.size(); i++) {
                    if (listProduct.get(i).getId().equals(curId)) {
                        foundIndex = i;
                        break;
                    }
                }
                selectedIndex = foundIndex;

                idField.setText(curId);
                namaField.setText(curNama);
                hargaField.setText(curHarga);
                kategoriComboBox.setSelectedItem(curKategori);
                stokField.setText(curStok);

                addUpdateButton.setText("Update");
                deleteButton.setVisible(true);
            }
        });

        // grup radio button yang sudah didefinisikan (Jangan re-inisialisasi)
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

        elektronikRadioButton.addActionListener(filterListener);
        alatTulisRadioButton.addActionListener(filterListener);
        makananRadioButton.addActionListener(filterListener);
        pakaianRadioButton.addActionListener(filterListener);
        minumanRadioButton.addActionListener(filterListener);
        semuaRadioButton.addActionListener(filterListener);

        semuaRadioButton.setSelected(true);
        filterTable("Semua");
    }

    public final DefaultTableModel setTable() {
        Object[] cols = {"No", "ID Produk", "Nama", "Harga", "Kategori", "Stok"};
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        for (int i = 0; i < listProduct.size(); i++) {
            Product p = listProduct.get(i);
            Object[] row = {
                    i + 1,
                    p.getId(),
                    p.getNama(),
                    String.format("%.2f", p.getHarga()),
                    p.getKategori(),
                    p.getStok()
            };
            tmp.addRow(row);
        }
        return tmp;
    }

    public void insertData() {
        try {
            String id = idField.getText();
            String nama = namaField.getText();
            double harga = Double.parseDouble(hargaField.getText());
            String kategori = kategoriComboBox.getSelectedItem().toString();
            int stok = Integer.parseInt(stokField.getText());

            if (harga < 0) {
                JOptionPane.showMessageDialog(null, "Harga tidak boleh negatif!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (stok < 0) {
                JOptionPane.showMessageDialog(null, "Stok tidak boleh negatif!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            listProduct.add(new Product(id, nama, harga, kategori, stok));
            productTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Harga dan stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateData() {
        try {
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(null, "Tidak ada produk yang dipilih untuk diupdate.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String id = idField.getText();
            String nama = namaField.getText();
            double harga = Double.parseDouble(hargaField.getText());
            String kategori = kategoriComboBox.getSelectedItem().toString();
            int stok = Integer.parseInt(stokField.getText());

            if (harga < 0) {
                JOptionPane.showMessageDialog(null, "Harga tidak boleh negatif!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (stok < 0) {
                JOptionPane.showMessageDialog(null, "Stok tidak boleh negatif!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product p = listProduct.get(selectedIndex);
            p.setId(id);
            p.setNama(nama);
            p.setHarga(harga);
            p.setKategori(kategori);
            p.setStok(stok);

            productTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil diubah");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Harga dan stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteData() {
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(null, "Tidak ada produk yang dipilih untuk dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        listProduct.remove(selectedIndex);
        productTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
    }

    public void clearForm() {
        idField.setText("");
        namaField.setText("");
        hargaField.setText("");
        stokField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        selectedIndex = -1;
    }

    private void populateList() {
        // tambah stok default untuk setiap produk
        listProduct.add(new Product("P001", "Laptop Asus", 8500000.0, "Elektronik", 10));
        listProduct.add(new Product("P002", "Mouse Logitech", 350000.0, "Elektronik", 25));
        listProduct.add(new Product("P003", "Keyboard Mechanical", 750000.0, "Elektronik", 15));
        listProduct.add(new Product("P004", "Roti Tawar", 15000.0, "Makanan", 50));
        listProduct.add(new Product("P005", "Susu UHT", 12000.0, "Minuman", 40));
        listProduct.add(new Product("P006", "Kemeja Putih", 125000.0, "Pakaian", 20));
        listProduct.add(new Product("P007", "Celana Jeans", 200000.0, "Pakaian", 12));
        listProduct.add(new Product("P008", "Pensil 2B", 3000.0, "Alat Tulis", 100));
        listProduct.add(new Product("P009", "Buku Tulis", 8000.0, "Alat Tulis", 80));
        listProduct.add(new Product("P010", "Air Mineral", 5000.0, "Minuman", 200));
        listProduct.add(new Product("P011", "Smartphone Samsung", 4500000.0, "Elektronik", 8));
        listProduct.add(new Product("P012", "Kue Brownies", 25000.0, "Makanan", 30));
        listProduct.add(new Product("P013", "Jaket Hoodie", 180000.0, "Pakaian", 14));
        listProduct.add(new Product("P014", "Pulpen Gel", 5000.0, "Alat Tulis", 90));
        listProduct.add(new Product("P015", "Teh Botol", 8000.0, "Minuman", 120));
    }

    private void filterTable(String kategori) {
        Object[] cols = {"No", "ID Produk", "Nama", "Harga", "Kategori", "Stok"};
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        for (int i = 0; i < listProduct.size(); i++) {
            Product p = listProduct.get(i);
            if (kategori.equals("Semua") || p.getKategori().equalsIgnoreCase(kategori)) {
                Object[] row = {
                        tmp.getRowCount() + 1,
                        p.getId(),
                        p.getNama(),
                        String.format("%.2f", p.getHarga()),
                        p.getKategori(),
                        p.getStok()
                };
                tmp.addRow(row);
            }
        }
        productTable.setModel(tmp);
    }
}
