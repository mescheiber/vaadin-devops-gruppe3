package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.PersistenzService;
import net.mci.seii.group3.service.VeranstaltungsService;


@Route(value = "lehrer", layout = MainLayout.class)
public class LehrerView extends VerticalLayout {

    private final Grid<Veranstaltung> grid;

    public LehrerView() {
        setPadding(true);
        setSpacing(true);

        String lehrer = AuthService.getInstance().getAngemeldeterBenutzer().getUsername();

        // Grid initialisieren, bevor es verwendet wird!
        grid = new Grid<>(Veranstaltung.class, false);
        grid.addColumn(Veranstaltung::getName).setHeader("Name");
        grid.addColumn(v -> v.getStartzeit().toString()).setHeader("Startzeit");
        grid.addColumn(Veranstaltung::getKennwort).setHeader("Kennwort");
        grid.addColumn(v -> v.getTeilnehmer().size()).setHeader("Teilnehmeranzahl");

        grid.setItems(VeranstaltungsService.getInstance().getVeranstaltungenFürLehrer(lehrer));

        TextField nameField = new TextField("Veranstaltungsname");
        DateTimePicker startzeit = new DateTimePicker("Startzeit");
        TextField klasseFeld = new TextField("Klasse zuweisen");

        Button erstellen = new Button("Veranstaltung anlegen", e -> {
            if (!nameField.isEmpty() && startzeit.getValue() != null) {
                Veranstaltung v = VeranstaltungsService.getInstance()
                        .createVeranstaltung(nameField.getValue(), lehrer, startzeit.getValue());

                if (!klasseFeld.isEmpty()) {
                    KlassenService.getInstance()
                            .getSchuelerEinerKlasse(klasseFeld.getValue())
                            .forEach(s -> VeranstaltungsService.getInstance().teilnehmerZuweisen(v.getId(), s));
                }

                PersistenzService.speichernAlles();
                grid.setItems(VeranstaltungsService.getInstance().getVeranstaltungenFürLehrer(lehrer));
            }
        });

        add(nameField, startzeit, klasseFeld, erstellen, grid);
    }
}
