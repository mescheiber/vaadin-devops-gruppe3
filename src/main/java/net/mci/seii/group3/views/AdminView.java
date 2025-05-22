package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import net.mci.seii.group3.model.Schulklasse;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.repository.SchulklassenRepository;
import net.mci.seii.group3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "admin_old", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {

    private final UserRepository userRepository;
    private final SchulklassenRepository schulklassenRepository;

    private final ComboBox<String> klasseBox;
    private final Grid<String> klassenGrid;
    private final Grid<User> nutzerGrid;

    @Autowired
    public AdminView(UserRepository userRepository, SchulklassenRepository schulklassenRepository) {
        this.userRepository = userRepository;
        this.schulklassenRepository = schulklassenRepository;

        setSpacing(true);
        setPadding(true);

        TextField usernameField = new TextField("Benutzername");
        TextField passwordField = new TextField("Passwort");

        klasseBox = new ComboBox<>("Klasse");
        klasseBox.setItems(schulklassenRepository.findAll().stream().map(Schulklasse::getName).toList());

        Button addStudent = new Button("Student anlegen", e -> {
            if (!usernameField.isEmpty() && !passwordField.isEmpty()) {
                if (userRepository.existsById(usernameField.getValue())) return;

                User neuerUser = new User(usernameField.getValue(), passwordField.getValue(), User.Role.STUDENT);
                if (klasseBox.getValue() != null) {
                    schulklassenRepository.findById(klasseBox.getValue()).ifPresent(klasse -> {
                        neuerUser.setKlasse(klasse.getName());
                        klasse.getSchueler().add(neuerUser.getUsername());
                        schulklassenRepository.save(klasse);
                    });
                }
                userRepository.save(neuerUser);
                refreshGrids();
            }
        });

        Button addTeacher = new Button("Lehrer anlegen", e -> {
            if (!usernameField.isEmpty() && !passwordField.isEmpty()) {
                if (userRepository.existsById(usernameField.getValue())) return;

                User lehrer = new User(usernameField.getValue(), passwordField.getValue(), User.Role.TEACHER);
                userRepository.save(lehrer);
                refreshGrids();
            }
        });

        TextField neueKlasse = new TextField("Neue Klasse");
        Button addClass = new Button("Klasse erstellen", e -> {
            if (!neueKlasse.isEmpty()) {
                Schulklasse klasse = new Schulklasse(neueKlasse.getValue());
                schulklassenRepository.save(klasse);
                neueKlasse.clear();
                refreshGrids();
            }
        });

        klassenGrid = new Grid<>();
        klassenGrid.addColumn(String::toString).setHeader("Klassen");

        nutzerGrid = new Grid<>(User.class, false);
        nutzerGrid.addColumn(User::getUsername).setHeader("Username");
        nutzerGrid.addColumn(u -> u.getRole().name()).setHeader("Rolle");

        refreshGrids();

        add(usernameField, passwordField, klasseBox, addStudent, addTeacher,
                neueKlasse, addClass, klassenGrid, nutzerGrid);
    }

    private void refreshGrids() {
        List<Schulklasse> klassen = schulklassenRepository.findAll();
        List<String> namen = klassen.stream().map(Schulklasse::getName).toList();
        klassenGrid.setItems(namen);
        klasseBox.setItems(namen);
        nutzerGrid.setItems(userRepository.findAll());
    }
}
