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
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.PersistenzService;
import net.mci.seii.group3.service.VeranstaltungsService;

import java.time.LocalDateTime;

@Route(value = "admin/veranstaltungen", layout = MainLayout.class)
public class AdminVeranstaltungView extends VerticalLayout {

    private final Grid<Veranstaltung> veranstaltungenGrid = new Grid<>();

    public AdminVeranstaltungView() {
        setPadding(true);
        setSpacing(true);

        veranstaltungenGrid.addClassName("grid");

        // Titel
        H3 ueberschrift = new H3("Veranstaltungsverwaltung");
        ueberschrift.addClassName("title");

        // Eingabefelder mit Placeholder
        TextField name = new TextField();
        name.setPlaceholder("Veranstaltungsname");
        name.setWidth("200px");
        name.setHeight("40px");

        ComboBox<String> lehrerBox = new ComboBox<>();
        lehrerBox.setPlaceholder("Lehrer");
        lehrerBox.setItems(AuthService.getInstance().getAlleBenutzernamen(User.Role.TEACHER));
        lehrerBox.setWidth("200px");
        lehrerBox.setHeight("40px");

        DateTimePicker startzeit = new DateTimePicker(); // Kein setPlaceholder möglich
        startzeit.setWidth("250px");
        startzeit.setHeight("40px");

        // Button zum Anlegen
        Button erstellen = new Button("Veranstaltung anlegen", e -> {
            if (!name.isEmpty() && lehrerBox.getValue() != null && startzeit.getValue() != null) {
                VeranstaltungsService.getInstance()
                        .createVeranstaltung(name.getValue(), lehrerBox.getValue(), startzeit.getValue());

                PersistenzService.speichernAlles();
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

        // Horizontales Formularlayout
        HorizontalLayout formularLayout = new HorizontalLayout(name, lehrerBox, startzeit, erstellen);
        formularLayout.setAlignItems(Alignment.BASELINE);
        formularLayout.setSpacing(true);

        // Tabelle für bestehende Veranstaltungen
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

        // Zurück-Button
        Button zurück = new Button("Zurück", e ->
                getUI().ifPresent(ui -> ui.navigate("admin"))
        );
        zurück.addClassName("button");

        // Gesamtaufbau
        add(
                ueberschrift,
                formularLayout,
                veranstaltungenGrid,
                zurück
        );
    }

    private void refreshGrid() {
        veranstaltungenGrid.setItems(VeranstaltungsService.getInstance().getAlleVeranstaltungen());
    }
}
