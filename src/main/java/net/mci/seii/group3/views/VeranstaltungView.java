package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.VeranstaltungsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "veranstaltung/:id", layout = MainLayout.class)
public class VeranstaltungView extends VerticalLayout implements BeforeEnterObserver {

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

        if (currentUser.getRole() == User.Role.TEACHER) {
            add(new Span("Zugewiesene Studenten:"));
            veranstaltung.getTeilnehmer().forEach(name -> {
                LocalDateTime teilnahmeZeit = veranstaltung.getTeilnahmen().get(name);
                String status = (teilnahmeZeit != null) ? "✓ um " + teilnahmeZeit.toString() : "⏳ offen";
                add(new Span("• " + name + " - " + status));
            });

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
                Notification.show(studentenListe.getSelectedItems().size() + " Studenten zugewiesen.");
                UI.getCurrent().navigate("lehrer");
            });

            add(studentenListe, alleAuswählen, zuweisen);
        }
    }
}
