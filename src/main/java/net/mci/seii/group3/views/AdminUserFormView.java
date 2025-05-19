package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.PersistenzService;

@Route(value = "admin/benutzer/form", layout = MainLayout.class)
public class AdminUserFormView extends VerticalLayout {

    public AdminUserFormView() {
        setPadding(true);
        setSpacing(true);

        TextField username = new TextField("Benutzername");
        PasswordField passwort = new PasswordField("Passwort");

        ComboBox<User.Role> rolleBox = new ComboBox<>("Rolle");
        rolleBox.setItems(User.Role.values());

        ComboBox<String> klasseBox = new ComboBox<>("Klasse (nur für Schüler)");
        klasseBox.setItems(KlassenService.getInstance().getAllKlassenNamen());

        Button speichern = new Button("Speichern", e -> {
            String name = username.getValue();
            String pass = passwort.getValue();
            User.Role rolle = rolleBox.getValue();

            if (name.isBlank() || pass.isBlank() || rolle == null) {
                Notification.show("Bitte alle Felder ausfüllen");
                return;
            }

            boolean ok = AuthService.getInstance().register(name, pass, rolle);
            if (ok) {
                if (rolle == User.Role.STUDENT && klasseBox.getValue() != null) {
                    KlassenService.getInstance().schuelerZurKlasse(klasseBox.getValue(), name);
                }
                PersistenzService.speichernAlles();
                getUI().ifPresent(ui -> ui.navigate("admin/benutzer"));
            } else {
                Notification.show("Benutzername bereits vergeben");
            }
        });
        speichern.addClassName("button");

        Button abbrechen = new Button("Zurück", e ->
                getUI().ifPresent(ui -> ui.navigate("admin/benutzer"))
        );
        abbrechen.addClassName("button");

        H3 ueberschrift = new H3("Benutzer hinzufügen");
        ueberschrift.addClassName("title");

        add(
                ueberschrift,
                username,
                passwort,
                rolleBox,
                klasseBox,
                speichern,
                abbrechen
        );
    }
}
