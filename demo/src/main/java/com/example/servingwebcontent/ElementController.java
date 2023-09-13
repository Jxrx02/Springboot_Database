package com.example.servingwebcontent;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ElementController  {

    private List<String> elements = new ArrayList<>();
    private List<String> id = new ArrayList<>();
    private List<String> vorname = new ArrayList<>();
    private List<String> nachname = new ArrayList<>();
    String url = "jdbc:sqlite:C:\\Users\\I569045\\Documents\\GitHub\\Springboot_Database\\demo\\src\\main\\resources\\database\\benutzer";

    @GetMapping("/t")
    public String showList(Model model) throws SQLException {
        model.addAttribute("elements", elements);
        loadDatabaseToList("SELECT * FROM nutzer");

        model.addAttribute("ids", id);
        model.addAttribute("vornames", vorname);
        model.addAttribute("nachnames", nachname);

        return "list_page";
    }

    private void loadDatabaseToList(String sqlStatement) {
        id.clear();
        vorname.clear();
        nachname.clear();
        // JDBC-Verbindungsdaten

        Connection conn = null;

        try {
            // JDBC-Treiber explizit laden
            Class.forName("org.sqlite.JDBC");

            // Verbindung zur Datenbank herstellen
            conn = DriverManager.getConnection(url);

            System.out.println("Daten geladen.");

            // SQL-Abfrage ausführen
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatement);

            // Daten in Liste
            while (rs.next()) {
                id.add(String.valueOf(rs.getInt("id")));
                vorname.add(rs.getString("vorname"));
                nachname.add(rs.getString("nachname"));
            }

            // Verbindung schließen
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/addElement")
    public String processFormDataAddElement(
            @RequestParam("idInput") String id,
            @RequestParam("vornameInput") String vorname,
            @RequestParam("nachnameInput") String nachname,
            Model model) {

        this.id.add(id);
        this.vorname.add(vorname);
        this.nachname.add(nachname);

        updateDatabase();
        return "redirect:/t";
    }
    private void updateDatabase () {
        // JDBC-Verbindungsdaten
        Connection conn = null;
        try {
            // JDBC-Treiber explizit laden
            Class.forName("org.sqlite.JDBC");

            // Verbindung zur Datenbank herstellen
            conn = DriverManager.getConnection(url);

            // SQL-Abfrage ausführen
            Statement stmt = conn.createStatement();
            //ResultSet rs = stmt.executeQuery(sqlStatement);

            //tabelle leeren
            stmt.execute("DELETE FROM nutzer;");

            String sqlStatement = "INSERT INTO nutzer (id, vorname, nachname) VALUES ";

            for (int i = 0; i < id.size(); i++) {
                sqlStatement += "(" + id.get(i) + ", '" + vorname.get(i) + "', '" + nachname.get(i) + "')";

                // Füge ein Komma und Leerzeichen hinzu, wenn es nicht das letzte Element ist
                if (i < id.size() - 1) {
                    sqlStatement += ", ";
                }
            }
            stmt.executeUpdate(sqlStatement);

            System.out.println("Daten überschrieben.");
            // Verbindung schließen
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/deleteAll")
    public String deleteAllElements() {
        this.id.clear();
        this.vorname.clear();
        this.nachname.clear();
        Connection conn = null;
        try {
            // JDBC-Treiber explizit laden
            Class.forName("org.sqlite.JDBC");

            // Verbindung zur Datenbank herstellen
            conn = DriverManager.getConnection(url);

            // SQL-Abfrage ausführen
            Statement stmt = conn.createStatement();
            //ResultSet rs = stmt.executeQuery(sqlStatement);

            //tabelle leeren
            stmt.execute("DELETE FROM nutzer;");
            System.out.println("Daten gelöscht.");

            stmt.execute("INSERT INTO nutzer (vorname, nachname) VALUES (null, null);");

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/t";
    }
}
