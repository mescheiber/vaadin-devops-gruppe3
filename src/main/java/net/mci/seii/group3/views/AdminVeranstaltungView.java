package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.repository.UserRepository;
import net.mci.seii.group3.repository.VeranstaltungsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Route(value = "admin/veranstaltungen", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminVeranstaltungView extends VerticalLayout {

    private final Grid<Veranstaltung> veranstaltungenGrid = new Grid<>();
    private final VeranstaltungsRepository veranstaltungsRepository;
    private final UserRepository userRepository;

    public AdminVeranstaltungView(VeranstaltungsRepository veranstaltungsRepository,
                                  UserRepository userRepository) {
        this.veranstaltungsRepository = veranstaltungsRepository;
        this.userRepository = userRepository;

        setPadding(true);
        setSpacing(true);

        veranstaltungenGrid.addClassName("grid");

        // Überschrift
        H3 ueberschrift = new H3("Veranstaltungsverwaltung");
        ueberschrift.addClassName("title");

        // Eingabefelder
        TextField name = new TextField();
        name.setPlaceholder("Veranstaltungsname");
        name.addClassName("form-field");

        ComboBox<String> lehrerBox = new ComboBox<>();
        lehrerBox.setPlaceholder("Lehrer");
        List<String> lehrer = userRepository.findByRole(User.Role.TEACHER).stream()
                .map(User::getUsername)
                .toList();
        lehrerBox.setItems(lehrer);
        lehrerBox.addClassName("form-field");

        DateTimePicker startzeit = new DateTimePicker();
        startzeit.addClassNames("form-field", "form-wide");

        Button erstellen = new Button("Veranstaltung anlegen", e -> {
            if (!name.isEmpty() && lehrerBox.getValue() != null && startzeit.getValue() != null) {
                Veranstaltung v = new Veranstaltung(name.getValue(), lehrerBox.getValue(), startzeit.getValue());
                veranstaltungsRepository.save(v);
                refreshGrid();
                name.clear();
                startzeit.clear();
                Notification.show("Veranstaltung angelegt");
            } else {
                Notification.show("Bitte alle Felder ausfüllen");
            }
        });
        erstellen.setHeight("40px");
        erstellen.addClassName("button");

        HorizontalLayout formularLayout = new HorizontalLayout(name, lehrerBox, startzeit, erstellen);
        formularLayout.setAlignItems(Alignment.BASELINE);
        formularLayout.setSpacing(true);

        // Grid konfigurieren
        veranstaltungenGrid.addColumn(Veranstaltung::getName).setHeader("Titel");
        veranstaltungenGrid.addColumn(Veranstaltung::getLehrer).setHeader("Lehrer");
        veranstaltungenGrid.addColumn(Veranstaltung::getKennwort).setHeader("Kennwort");
        veranstaltungenGrid.addColumn(v -> v.getStartzeit().toString()).setHeader("Startzeit");
        veranstaltungenGrid.addColumn(v -> v.getTeilnehmer().size()).setHeader("Teilnehmer");

        veranstaltungenGrid.asSingleSelect().addValueChangeListener(e -> {
            Veranstaltung v = e.getValue();
            if (v != null) {
                getUI().ifPresent(ui -> ui.navigate("veranstaltung/" + v.getId()));
            }
        });

        refreshGrid();

        Button zurück = new Button("Zurück", e -> getUI().ifPresent(ui -> ui.navigate("admin")));
        zurück.addClassName("button");

        add(ueberschrift, formularLayout, veranstaltungenGrid, zurück);
    }

    private void refreshGrid() {
        veranstaltungenGrid.setItems(veranstaltungsRepository.findAll());
    }
}
