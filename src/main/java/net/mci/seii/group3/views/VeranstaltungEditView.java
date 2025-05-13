package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.PersistenzService;
import net.mci.seii.group3.service.VeranstaltungsService;
import net.mci.seii.group3.utils.PdfExportService;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "veranstaltung-edit/:id", layout = MainLayout.class)
public class VeranstaltungEditView extends VerticalLayout implements BeforeEnterObserver {

    private Veranstaltung veranstaltung;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get("id").orElse(null);
        User currentUser = (User) VaadinSession.getCurrent().getAttribute(User.class);

        veranstaltung = VeranstaltungsService.getInstance()
            .getAlleVeranstaltungen().stream()
            .filter(v -> v.getId().equals(id))
            .findFirst().orElse(null);

        if (veranstaltung == null || currentUser == null) {
            event.forwardTo("");
            return;
        }

        add(new Span("Veranstaltung: " + veranstaltung.getName()));
        add(new Span("Startzeit: " + veranstaltung.getStartzeit()));

        Button back = new Button("Zurück", e -> {
            if (currentUser.getRole() == User.Role.TEACHER) {
                UI.getCurrent().navigate("lehrer");
            } else {
                UI.getCurrent().navigate("student");
            }
        });
        add(back);

        // === STUDENT ===
        if (currentUser.getRole() == User.Role.STUDENT) {
            if (!veranstaltung.getTeilnahmen().containsKey(currentUser.getUsername())) {
                TextField kennwort = new TextField("Kennwort");
                Button teilnehmen = new Button("Teilnehmen", e -> {
                    boolean erlaubt = VeranstaltungsService.getInstance().prüfenTeilnahme(
                        veranstaltung.getId(), kennwort.getValue(), currentUser.getUsername()
                    );
                    if (erlaubt) {
                        Notification.show("Teilnahme erfolgreich!");
                        UI.getCurrent().navigate("student");
                    } else {
                        Notification.show("Teilnahme nicht möglich.");
                    }
                });
                add(kennwort, teilnehmen);
            } else {
                Notification.show("Du hast bereits teilgenommen.");
                UI.getCurrent().navigate("student");
            }
        }

        // === TEACHER ===
        if (currentUser.getRole() == User.Role.TEACHER) {

            // Editfelder für Lehrer
            TextField nameField = new TextField("Titel", veranstaltung.getName());
            DateTimePicker startzeitField = new DateTimePicker("Startzeit", veranstaltung.getStartzeit());

            Button speichern = new Button("Speichern", e -> {
                veranstaltung.setName(nameField.getValue());
                veranstaltung.setStartzeit(startzeitField.getValue());
                PersistenzService.speichernAlles();
                Notification.show("Gespeichert.");
            });

            add(nameField, startzeitField, speichern);

            // Teilnehmerliste mit Status
            add(new Span("Zugewiesene Studenten:"));
            Grid<String> teilnehmerGrid = new Grid<>();
            teilnehmerGrid.addColumn(name -> name).setHeader("Name");
            teilnehmerGrid.addColumn(name -> {
                LocalDateTime t = veranstaltung.getTeilnahmen().get(name);
                return (t != null) ? "✓ " + t.toString() : "⏳ offen";
            }).setHeader("Teilnahme");
            teilnehmerGrid.setItems(veranstaltung.getTeilnehmer());
            add(teilnehmerGrid);

            // PDF Export
            Button pdfExport = new Button("Als PDF exportieren", e -> {
                try {
                    byte[] pdf = PdfExportService.erzeugePdf(veranstaltung);
                    StreamResource res = new StreamResource("teilnehmerliste.pdf", () -> new ByteArrayInputStream(pdf));
                    Anchor download = new Anchor(res, "PDF herunterladen");
                    download.getElement().setAttribute("download", true);
                    add(download);
                } catch (Exception ex) {
                    Notification.show("Fehler beim PDF-Export: " + ex.getMessage());
                }
            });
            add(pdfExport);

            // Neue Studenten zuweisen
            add(new Span("Neue Studenten zuweisen:"));
            List<String> alleStudenten = AuthService.getInstance().getAlleBenutzernamen(User.Role.STUDENT);
            Set<String> bereitsZugewiesen = veranstaltung.getTeilnehmer();
            List<String> verfügbareStudenten = alleStudenten.stream()
                .filter(name -> !bereitsZugewiesen.contains(name))
                .collect(Collectors.toList());

            MultiSelectListBox<String> studentenListe = new MultiSelectListBox<>();
            studentenListe.setItems(verfügbareStudenten);

            Button alleAuswählen = new Button("Alle auswählen", e -> studentenListe.select(verfügbareStudenten));

            Button zuweisen = new Button("Zuweisen", e -> {
                studentenListe.getSelectedItems().forEach(student ->
                    VeranstaltungsService.getInstance().teilnehmerZuweisen(veranstaltung.getId(), student)
                );
                PersistenzService.speichernAlles();
                Notification.show(studentenListe.getSelectedItems().size() + " Studenten zugewiesen.");
                UI.getCurrent().navigate("veranstaltung-edit/" + veranstaltung.getId());
            });

            add(studentenListe, alleAuswählen, zuweisen);
        }
    }
}
