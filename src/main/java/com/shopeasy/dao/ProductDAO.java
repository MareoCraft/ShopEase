
package com.shopeasy.dao;

import com.shopeasy.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ADD PRODUCT
    public int addProduct(Connection con, Product product)
            throws Exception {

        int productId = 0;

        String sequenceSql =
                "SELECT PRODUCT_SEQ.NEXTVAL FROM DUAL";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sequenceSql)) {

            if (rs.next()) {
                productId = rs.getInt(1);
            }
        }

        String sql = "INSERT INTO PRODUCTS (PRODUCT_ID, SHOP_ID, CATEGORY_ID, PRODUCT_NAME, SKU, BARCODE, PURCHASE_PRICE, SELLING_PRICE, STOCK_QUANTITY, MIN_STOCK_LEVEL, UNIT, DESCRIPTION, CREATED_DATE, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, 'ACTIVE')";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setInt(2, product.getShopId());
            ps.setInt(3, product.getCategoryId());
            ps.setString(4, product.getProductName());
            ps.setString(5, product.getSku());
            ps.setString(6, product.getBarcode());
            ps.setDouble(7, product.getPurchasePrice());
            ps.setDouble(8, product.getSellingPrice());
            ps.setDouble(9, product.getStockQuantity());
            ps.setDouble(10, product.getMinStockLevel());
            ps.setString(11, product.getUnit());
            ps.setString(12, product.getDescription());

            int rows = ps.executeUpdate();

            return rows > 0 ? productId : 0;
        }
    }

    // UPDATE PRODUCT
    public boolean updateProduct(Connection con, Product product)
            throws Exception {

        String sql = "UPDATE PRODUCTS SET CATEGORY_ID = ?, PRODUCT_NAME = ?, SKU = ?, BARCODE = ?, PURCHASE_PRICE = ?, SELLING_PRICE = ?, MIN_STOCK_LEVEL = ?, UNIT = ?, DESCRIPTION = ?, UPDATED_DATE = SYSDATE WHERE PRODUCT_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getSku());
            ps.setString(4, product.getBarcode());
            ps.setDouble(5, product.getPurchasePrice());
            ps.setDouble(6, product.getSellingPrice());
            ps.setDouble(7, product.getMinStockLevel());
            ps.setString(8, product.getUnit());
            ps.setString(9, product.getDescription());
            ps.setInt(10, product.getProductId());
            ps.setInt(11, product.getShopId());

            return ps.executeUpdate() > 0;
        }
    }

    // SOFT DELETE PRODUCT
    public boolean deleteProduct(Connection con, int productId, int shopId)
            throws Exception {

        String sql = "UPDATE PRODUCTS SET STATUS = 'INACTIVE', UPDATED_DATE = SYSDATE WHERE PRODUCT_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setInt(2, shopId);

            return ps.executeUpdate() > 0;
        }
    }

    // GET ALL PRODUCTS BY SHOP
    public List<Product> getProductsByShopId(Connection con, int shopId)
            throws Exception {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT P.*, C.CATEGORY_NAME FROM PRODUCTS P INNER JOIN CATEGORIES C ON P.CATEGORY_ID = C.CATEGORY_ID AND P.SHOP_ID = C.SHOP_ID WHERE P.SHOP_ID = ? AND P.STATUS = 'ACTIVE' AND C.STATUS = 'ACTIVE' ORDER BY P.PRODUCT_ID DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Product product = mapProduct(rs);

                    products.add(product);
                }
            }
        }

        return products;
    }

    // GET PRODUCT BY ID
    public Product getProductById(Connection con, int productId, int shopId)
            throws Exception {

        String sql = "SELECT P.*, C.CATEGORY_NAME FROM PRODUCTS P INNER JOIN CATEGORIES C ON P.CATEGORY_ID = C.CATEGORY_ID AND P.SHOP_ID = C.SHOP_ID WHERE P.PRODUCT_ID = ? AND P.SHOP_ID = ? AND P.STATUS = 'ACTIVE' AND C.STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        }

        return null;
    }

    // CHECK CATEGORY BELONGS TO SHOP
    public boolean isCategoryValid(Connection con, int categoryId, int shopId)
            throws Exception {

        String sql = "SELECT COUNT(*) FROM CATEGORIES WHERE CATEGORY_ID = ? AND SHOP_ID = ? AND STATUS = 'ACTIVE'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setInt(2, shopId);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // CHECK DUPLICATE SKU
    public boolean isSkuExists(Connection con, int shopId, String sku,
                               int excludeProductId)
            throws Exception {

        if (sku == null || sku.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM PRODUCTS WHERE SHOP_ID = ? AND UPPER(SKU) = UPPER(?) AND STATUS = 'ACTIVE' AND PRODUCT_ID <> ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setString(2, sku.trim());
            ps.setInt(3, excludeProductId);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // CHECK DUPLICATE BARCODE
    public boolean isBarcodeExists(Connection con, int shopId, String barcode,
                                   int excludeProductId)
            throws Exception {

        if (barcode == null || barcode.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM PRODUCTS WHERE SHOP_ID = ? AND UPPER(BARCODE) = UPPER(?) AND STATUS = 'ACTIVE' AND PRODUCT_ID <> ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, shopId);
            ps.setString(2, barcode.trim());
            ps.setInt(3, excludeProductId);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // MAP RESULTSET TO PRODUCT MODEL
    private Product mapProduct(ResultSet rs) throws Exception {

        Product product = new Product();

        product.setCategoryName(rs.getString("CATEGORY_NAME"));
        product.setProductId(rs.getInt("PRODUCT_ID"));
        product.setShopId(rs.getInt("SHOP_ID"));
        product.setCategoryId(rs.getInt("CATEGORY_ID"));
        product.setProductName(rs.getString("PRODUCT_NAME"));
        product.setSku(rs.getString("SKU"));
        product.setBarcode(rs.getString("BARCODE"));
        product.setPurchasePrice(rs.getDouble("PURCHASE_PRICE"));
        product.setSellingPrice(rs.getDouble("SELLING_PRICE"));
        product.setStockQuantity(rs.getDouble("STOCK_QUANTITY"));
        product.setMinStockLevel(rs.getDouble("MIN_STOCK_LEVEL"));
        product.setUnit(rs.getString("UNIT"));
        product.setDescription(rs.getString("DESCRIPTION"));
        product.setStatus(rs.getString("STATUS"));
        product.setCreatedDate(rs.getDate("CREATED_DATE"));
        product.setUpdatedDate(rs.getDate("UPDATED_DATE"));

        return product;
    }
}
