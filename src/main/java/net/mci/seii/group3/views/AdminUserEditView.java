package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.PersistenzService;

@Route(value = "admin/benutzer/edit/:username", layout = MainLayout.class)
public class AdminUserEditView extends VerticalLayout implements BeforeEnterObserver {

    private final TextField usernameField = new TextField();
    private final ComboBox<User.Role> rolleBox = new ComboBox<>();
    private final ComboBox<String> klasseBox = new ComboBox<>();

    private User currentUser;

    public AdminUserEditView() {
        setSpacing(true);
        setPadding(true);

        H3 titel = new H3("Benutzer bearbeiten");
        titel.addClassName("title");
        add(titel);

        rolleBox.setItems(User.Role.values());
        klasseBox.setItems(KlassenService.getInstance().getAllKlassenNamen());

        Button speichern = new Button("Änderungen speichern", e -> {
            if (currentUser == null) {
                return;
            }

            String alterName = currentUser.getUsername();
            String neuerName = usernameField.getValue();

            currentUser.setUsername(neuerName);
            currentUser.setRole(rolleBox.getValue());

            // Benutzername in Klassen aktualisieren
            if (!alterName.equals(neuerName)) {
                KlassenService.getInstance().aktualisiereBenutzername(alterName, neuerName);
            }

            // Klasse ggf. neu zuweisen (wenn Student)
            KlassenService.getInstance().removeBenutzerAusAllenKlassen(neuerName);
            if (rolleBox.getValue() == User.Role.STUDENT && klasseBox.getValue() != null) {
                KlassenService.getInstance().schuelerZurKlasse(klasseBox.getValue(), neuerName);
            }

            PersistenzService.speichernAlles();
            getUI().ifPresent(ui -> ui.navigate("admin/benutzer"));
        });
        speichern.addClassName("button");

        Button abbrechen = new Button("Abbrechen", e
                -> getUI().ifPresent(ui -> ui.navigate("admin/benutzer"))
        );
        abbrechen.addClassName("button");

        usernameField.setPlaceholder("Benutzername");
        rolleBox.setPlaceholder("Rolle");
        klasseBox.setPlaceholder("Klasse (nur für Schüler)");
        usernameField.addClassName("form-field");
        rolleBox.addClassName("form-field");
        klasseBox.addClassName("form-field");
        add(usernameField, rolleBox, klasseBox, speichern, abbrechen);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = event.getRouteParameters().get("username").orElse(null);
        if (username != null) {
            currentUser = AuthService.getInstance().getUserByName(username);
            if (currentUser != null) {
                usernameField.setValue(currentUser.getUsername());
                rolleBox.setValue(currentUser.getRole());
                klasseBox.setValue(KlassenService.getInstance().getKlasseVonSchueler(username));
                return;
            }
        }
        Notification.show("Benutzer nicht gefunden");
        getUI().ifPresent(ui -> ui.navigate("admin/benutzer"));
    }
}
