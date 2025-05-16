package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.PersistenzService;
import net.mci.seii.group3.service.VeranstaltungsService;

@Route(value = "lehrer", layout = MainLayout.class)
public class LehrerView extends VerticalLayout {

    private final Grid<Veranstaltung> grid;
    private final ComboBox<String> klasseBox;

    public LehrerView() {
        setPadding(true);
        setSpacing(true);

        User current = AuthService.getInstance().getAngemeldeterBenutzer();
        String lehrer = current.getUsername();

        // Grid initialisieren
        grid = new Grid<>(Veranstaltung.class, false);
        grid.addColumn(Veranstaltung::getName).setHeader("Name");
        grid.addColumn(v -> v.getStartzeit().toString()).setHeader("Startzeit");
        grid.addColumn(Veranstaltung::getKennwort).setHeader("Kennwort");
        grid.addColumn(v -> v.getTeilnehmer().size()).setHeader("Teilnehmeranzahl");
        grid.setItems(VeranstaltungsService.getInstance().getVeranstaltungenFürLehrer(lehrer));

        // Bei Klick in Zeile in Edit-View springen
        grid.asSingleSelect().addValueChangeListener(e -> {
            Veranstaltung v = e.getValue();
            if (v != null) {
                UI.getCurrent().navigate("veranstaltung/" + v.getId());
            }
        });

        // Formular zum Anlegen
        var nameField = new com.vaadin.flow.component.textfield.TextField("Veranstaltungsname");
        var startField = new DateTimePicker("Startzeit");
        klasseBox = new ComboBox<>("Klasse zuweisen");
        klasseBox.setItems(KlassenService.getInstance().getAllKlassenNamen());

        Button erstellen = new Button("Veranstaltung anlegen", e -> {
            if (nameField.isEmpty() || startField.getValue() == null) {
                Notification.show("Bitte Name und Startzeit angeben");
                return;
            }
            Veranstaltung v = VeranstaltungsService.getInstance()
                    .createVeranstaltung(nameField.getValue(), lehrer, startField.getValue());
            if (klasseBox.getValue() != null) {
                KlassenService.getInstance()
                        .getSchuelerEinerKlasse(klasseBox.getValue())
                        .forEach(s -> VeranstaltungsService.getInstance().teilnehmerZuweisen(v.getId(), s));
            }
            PersistenzService.speichernAlles();
            grid.setItems(VeranstaltungsService.getInstance().getVeranstaltungenFürLehrer(lehrer));
            nameField.clear();
            startField.clear();
            klasseBox.clear();
            Notification.show("Veranstaltung angelegt");
        });

        add(
            nameField, startField, klasseBox, erstellen,
            grid
        );
    }
}
