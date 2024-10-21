package com.example.lab3;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
@RequestScoped
public class ProductBean {
    private List<Product> products;

    @PostConstruct
    public void init() {
        products = new ArrayList<>();
        loadProducts();
    }

    private void loadProducts() {
        String sql = "SELECT * FROM products";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        (UUID) rs.getObject("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("price")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getProducts() {
        return products;
    }
}
