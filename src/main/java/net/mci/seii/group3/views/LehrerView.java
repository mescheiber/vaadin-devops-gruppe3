package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import net.mci.seii.group3.model.Schulklasse;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.repository.SchulklassenRepository;
import net.mci.seii.group3.repository.VeranstaltungsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "lehrer", layout = MainLayout.class)
@RolesAllowed("TEACHER")
public class LehrerView extends VerticalLayout {

    private final VeranstaltungsRepository veranstaltungsRepository;
    private final SchulklassenRepository klassenRepository;
    private final Grid<Veranstaltung> grid = new Grid<>(Veranstaltung.class, false);
    private final ComboBox<String> klasseBox = new ComboBox<>("Klasse zuweisen");

    @Autowired
    public LehrerView(VeranstaltungsRepository veranstaltungsRepository, SchulklassenRepository klassenRepository) {
        this.veranstaltungsRepository = veranstaltungsRepository;
        this.klassenRepository = klassenRepository;

        setPadding(true);
        setSpacing(true);

        User currentUser = (User) VaadinSession.getCurrent().getAttribute(User.class);
        if (currentUser == null || currentUser.getRole() != User.Role.TEACHER) {
            UI.getCurrent().navigate("");
            return;
        }

        String lehrerName = currentUser.getUsername();

        // Grid für bestehende Veranstaltungen
        grid.addColumn(Veranstaltung::getName).setHeader("Name");
        grid.addColumn(v -> v.getStartzeit().toString()).setHeader("Startzeit");
        grid.addColumn(Veranstaltung::getKennwort).setHeader("Kennwort");
        grid.addColumn(v -> v.getTeilnehmer().size()).setHeader("Teilnehmeranzahl");
        grid.setItems(veranstaltungsRepository.findAll().stream()
                .filter(v -> v.getZugewieseneLehrer().contains(lehrerName))
                .collect(Collectors.toList()));

        grid.asSingleSelect().addValueChangeListener(e -> {
            Veranstaltung v = e.getValue();
            if (v != null) {
                UI.getCurrent().navigate("veranstaltung/" + v.getId());
            }
        });

        // Formular zur Erstellung
        var nameField = new com.vaadin.flow.component.textfield.TextField("Veranstaltungsname");
        var startzeitField = new DateTimePicker("Startzeit");
        klasseBox.setItems(
                klassenRepository.findAll().stream()
                        .map(Schulklasse::getName)
                        .toList()
        );

        Button erstellen = new Button("Veranstaltung anlegen", e -> {
            if (nameField.isEmpty() || startzeitField.getValue() == null) {
                Notification.show("Bitte Name und Startzeit angeben");
                return;
            }

            Veranstaltung veranstaltung = new Veranstaltung(nameField.getValue(), lehrerName, startzeitField.getValue());

            // Klasse zuweisen
            if (klasseBox.getValue() != null) {
                klassenRepository.findById(klasseBox.getValue()).ifPresent(klasse -> {
                    Set<String> schueler = klasse.getSchueler();
                    veranstaltung.getTeilnehmer().addAll(schueler);
                });
            }

            veranstaltungsRepository.save(veranstaltung);
            grid.setItems(veranstaltungsRepository.findAll().stream()
                    .filter(v -> v.getZugewieseneLehrer().contains(lehrerName))
                    .collect(Collectors.toList()));

            nameField.clear();
            startzeitField.clear();
            klasseBox.clear();
            Notification.show("Veranstaltung angelegt");
        });

        add(nameField, startzeitField, klasseBox, erstellen, grid);
    }
}
