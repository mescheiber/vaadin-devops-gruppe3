package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.PersistenzService;
import net.mci.seii.group3.service.VeranstaltungsService;

import java.util.Set;

@Route("admin/veranstaltungen/edit/:id")
public class VeranstaltungEditView extends VerticalLayout implements BeforeEnterObserver {

    private Veranstaltung veranstaltung;
    private final Grid<String> teilnehmerGrid = new Grid<>();

    private final ComboBox<String> userBox = new ComboBox<>("Student hinzufügen");
    private final ComboBox<String> klasseBox = new ComboBox<>("Klasse hinzufügen");

    public VeranstaltungEditView() {
        setSpacing(true);
        setPadding(true);

        add(new H2("Veranstaltung bearbeiten"));

        // Teilnehmer-Grid
        teilnehmerGrid.addColumn(String::toString).setHeader("Teilnehmer");

        // Buttons
        Button hinzufuegenUser = new Button("Benutzer zuweisen", e -> {
            if (veranstaltung != null && userBox.getValue() != null) {
                veranstaltung.getTeilnehmer().add(userBox.getValue());
                aktualisieren();
            }
        });

        Button hinzufuegenKlasse = new Button("Klasse zuweisen", e -> {
            if (veranstaltung != null && klasseBox.getValue() != null) {
                Set<String> schueler = KlassenService.getInstance().getSchuelerEinerKlasse(klasseBox.getValue());
                veranstaltung.getTeilnehmer().addAll(schueler);
                aktualisieren();
            }
        });

        Button zurück = new Button("Zurück", e ->
            getUI().ifPresent(ui -> ui.navigate("admin/veranstaltungen"))
        );

        add(
            new HorizontalLayout(userBox, hinzufuegenUser),
            new HorizontalLayout(klasseBox, hinzufuegenKlasse),
            teilnehmerGrid,
            zurück
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get("id").orElse(null);
        veranstaltung = VeranstaltungsService.getInstance().getVeranstaltungById(id);
        if (veranstaltung == null) {
            Notification.show("Veranstaltung nicht gefunden");
            getUI().ifPresent(ui -> ui.navigate("admin/veranstaltungen"));
            return;
        }

        userBox.setItems(AuthService.getInstance().getAlleBenutzernamen(User.Role.STUDENT));
        klasseBox.setItems(KlassenService.getInstance().getAllKlassenNamen());
        aktualisieren();
    }

    private void aktualisieren() {
        teilnehmerGrid.setItems(veranstaltung.getTeilnehmer());
        PersistenzService.speichernAlles();
    }
}
