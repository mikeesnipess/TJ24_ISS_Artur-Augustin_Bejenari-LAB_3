package com.example.lab3;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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
public class ClientBean {
    private List<Client> clients;
    private Client newClient = new Client(); // To capture form inputs for new or edited clients

    @PostConstruct
    public void init() {
        try {
            clients = loadClients(); // Ensure this method works correctly
        } catch (Exception e) {
            // Log the exception, e.g., using Logger
            System.err.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to load clients from the database
    private List<Client> loadClients() {
        // Initialize the clients list
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Client client = new Client(
                        (UUID) rs.getObject("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return the list of clients (will be empty if an error occurred)
        return clients;
    }


    public void addClient() {
        System.out.println("New Client: " + newClient);

        String sql = "INSERT INTO clients (id, name, email, phone, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            UUID clientId = UUID.randomUUID();
            pstmt.setObject(1, clientId);
            pstmt.setString(2, newClient.getName());
            pstmt.setString(3, newClient.getEmail());
            pstmt.setString(4, newClient.getPhone());
            pstmt.setString(5, newClient.getAddress());
            pstmt.executeUpdate();

            // Clear the newClient object for future input
            newClient = new Client();

            // Reload the clients list
            loadClients();

            // Display success message
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Client added successfully"));
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not add client"));
        }
    }



    // Method to update an existing client in the database
    public void updateClient(Client client) {
        String sql = "UPDATE clients SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getEmail());
            pstmt.setString(3, client.getPhone());
            pstmt.setString(4, client.getAddress());
            pstmt.setObject(5, client.getId());
            pstmt.executeUpdate();

            // Reload clients after update
            loadClients();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a client from the database
    public void deleteClient(Client client) {
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, client.getId());
            pstmt.executeUpdate();

            // Reload clients after deletion
            loadClients();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getters for the client list and the new client instance
    public List<Client> getClients() {
        return clients;
    }

    public Client getNewClient() {
        return newClient;
    }

    public void setNewClient(Client newClient) {
        this.newClient = newClient;
    }
}
