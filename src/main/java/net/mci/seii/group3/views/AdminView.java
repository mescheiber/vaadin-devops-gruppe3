package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.PersistenzService;

@Route(value = "admin_old", layout = MainLayout.class)
public class AdminView extends VerticalLayout {

    private final ComboBox<String> klasseBox;
    private Grid<String> klassenGrid;

    public AdminView() {
        setSpacing(true);
        setPadding(true);

        TextField usernameField = new TextField("Benutzername");
        TextField passwordField = new TextField("Passwort");

        klasseBox = new ComboBox<>("Klasse");
        klasseBox.setItems(KlassenService.getInstance().getAllKlassenNamen());

        Button addStudent = new Button("Student anlegen", e -> {
            if (AuthService.getInstance().register(usernameField.getValue(), passwordField.getValue(), User.Role.STUDENT)) {
                if (klasseBox.getValue() != null) {
                    KlassenService.getInstance().schuelerZurKlasse(klasseBox.getValue(), usernameField.getValue());
                }
                PersistenzService.speichernAlles();
                refreshNutzerGrid();
            }
        });

        Button addTeacher = new Button("Lehrer anlegen", e -> {
            if (AuthService.getInstance().register(usernameField.getValue(), passwordField.getValue(), User.Role.TEACHER)) {
                PersistenzService.speichernAlles();
                refreshNutzerGrid();
            }
        });

        TextField neueKlasse = new TextField("Neue Klasse");
        Button addClass = new Button("Klasse erstellen", e -> {
            KlassenService.getInstance().addKlasse(neueKlasse.getValue());
            PersistenzService.speichernAlles();
            klasseBox.setItems(KlassenService.getInstance().getAllKlassenNamen());
            klassenGrid.setItems(KlassenService.getInstance().getAllKlassenNamen());
            neueKlasse.clear();
        });

        klassenGrid = new Grid<>();
        klassenGrid.addColumn(String::toString).setHeader("Klassen");
        klassenGrid.setItems(KlassenService.getInstance().getAllKlassenNamen());

        Grid<User> nutzerGrid = new Grid<>(User.class, false);
        nutzerGrid.addColumn(User::getUsername).setHeader("Username");
        nutzerGrid.addColumn(u -> u.getRole().name()).setHeader("Rolle");
        nutzerGrid.setItems(AuthService.getInstance().getAllUsers());

        add(usernameField, passwordField, klasseBox, addStudent, addTeacher,
            neueKlasse, addClass, klassenGrid, nutzerGrid);
    }

    private void refreshNutzerGrid() {
        klasseBox.setItems(KlassenService.getInstance().getAllKlassenNamen());
    }
}
