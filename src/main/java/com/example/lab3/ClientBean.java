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
    private Client selectedClient;
    private Client newClient = new Client();

    @PostConstruct
    public void init() {
        try {
            clients = loadClients();
        } catch (Exception e) {
            System.err.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Client> loadClients() {
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

            newClient = new Client();

            loadClients();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Client added successfully"));
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not add client"));
        }
    }

    public void updateClient(Client client) {
        this.selectedClient = client;
        System.out.println("Selected client for update: " + selectedClient.getName());
    }

    public void saveUpdatedClient(Client selClient) {
        System.out.println("Updating client: " + selClient.getName()); // Debugging statement

        String sql = "UPDATE clients SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, selectedClient.getName());
            pstmt.setString(2, selectedClient.getEmail());
            pstmt.setString(3, selectedClient.getPhone());
            pstmt.setString(4, selectedClient.getAddress());
            pstmt.setObject(5, selectedClient.getId());

            int affectedRows = pstmt.executeUpdate(); // Check how many rows were updated
            System.out.println("Rows affected: " + affectedRows); // Debugging statement

            clients = loadClients(); // Reload clients after update

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Client updated successfully"));
        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not update client"));
        }
    }


    public void deleteClient(Client client) {
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, client.getId());
            pstmt.executeUpdate();

            loadClients();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Client> getClients() {
        return clients;
    }

    public Client getNewClient() {
        return newClient;
    }

    public void setNewClient(Client newClient) {
        this.newClient = newClient;
    }

    public Client getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(Client selectedClient) {
        this.selectedClient = selectedClient;
    }

}
