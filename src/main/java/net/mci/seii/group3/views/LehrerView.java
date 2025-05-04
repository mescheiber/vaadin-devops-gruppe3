package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.VeranstaltungsService;

import java.time.LocalDateTime;
import java.util.List;

@Route("lehrer")
public class LehrerView extends VerticalLayout {

    public LehrerView() {
        User lehrer = (User) VaadinSession.getCurrent().getAttribute(User.class);
        if (lehrer == null || lehrer.getRole() != User.Role.TEACHER) {
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }

        TextField name = new TextField("Veranstaltungsname");
        DateTimePicker zeit = new DateTimePicker("Startzeit");
        Button erstellen = new Button("Veranstaltung erstellen", e -> {
            if (name.isEmpty() || zeit.isEmpty()) {
                Notification.show("Bitte Name und Startzeit angeben!");
                return;
            }
            VeranstaltungsService.getInstance().createVeranstaltung(name.getValue(), lehrer.getUsername(), zeit.getValue());
            getUI().ifPresent(ui -> ui.getPage().reload());
        });

        Grid<Veranstaltung> grid = new Grid<>(Veranstaltung.class, false);
        grid.addColumn(Veranstaltung::getName).setHeader("Veranstaltung");
        grid.addColumn(v -> v.getStartzeit().toString()).setHeader("Startzeit");
        grid.addColumn(Veranstaltung::getKennwort).setHeader("Kennwort");

        MultiSelectListBox<String> studentenListe = new MultiSelectListBox<>();
        List<String> alleStudenten = AuthService.getInstance().getAlleBenutzernamen(User.Role.STUDENT);
        studentenListe.setItems(alleStudenten);

        Button zuweisen = new Button("Zuweisen", e -> {
            Veranstaltung v = grid.asSingleSelect().getValue();
            if (v != null) {
                studentenListe.getSelectedItems().forEach(student ->
                    VeranstaltungsService.getInstance().teilnehmerZuweisen(v.getId(), student)
                );
                Notification.show("Studenten zugewiesen.");
                getUI().ifPresent(ui -> ui.getPage().reload());
            }
        });

        grid.setItems(VeranstaltungsService.getInstance().getVeranstaltungenFürLehrer(lehrer.getUsername()));

        // Richtige Platzierung des Click-Listeners
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                UI.getCurrent().navigate("veranstaltung/" + e.getValue().getId());
            }
        });

        add(name, zeit, erstellen, grid, studentenListe, zuweisen);
    }
}
