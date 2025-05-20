package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.KlassenService;

@Route(value = "home", layout = MainLayout.class)
public class LandingPage extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null) {
            event.forwardTo("");
        }
    }

    public LandingPage() {
        getStyle().set("padding", "1rem"); // replaces setPadding(true)
        setSpacing(true); // Optional: You may use a CSS class instead
        setAlignItems(Alignment.CENTER);

        User currentUser = VaadinSession.getCurrent().getAttribute(User.class);
        if (currentUser == null) {
            UI.getCurrent().navigate("");
            return;
        }

        // Profile Picture
        Image avatar = new Image("images/profile-placeholder.png", "Profile Picture");
        avatar.setWidth("100px");
        avatar.getStyle()
                .set("border-radius", "50%")
                .set("border", "2px solid #ccc")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        // Profile Info
        TextField username = new TextField("Benutzername");
        username.setValue(currentUser.getUsername());
        username.setReadOnly(true);

        TextField role = new TextField("Rolle");
        role.setValue(currentUser.getRole().name());
        role.setReadOnly(true);

        TextField klasseField = new TextField("Klasse");
        String klasse = KlassenService.getInstance().getKlasseVonSchueler(currentUser.getUsername());
        klasseField.setValue(klasse != null ? klasse : "");
        klasseField.setReadOnly(true);
        klasseField.setVisible(currentUser.getRole() == User.Role.STUDENT);

        username.addClassName("form-field");
        role.addClassName("form-field");
        klasseField.addClassName("form-field");

        FormLayout profileForm = new FormLayout(username, role);
        if (currentUser.getRole() == User.Role.STUDENT) {
            profileForm.add(klasseField);
        }

        // Password Change Fields
        PasswordField oldPassword = new PasswordField("Altes Passwort");
        PasswordField newPassword = new PasswordField("Neues Passwort");
        oldPassword.addClassName("form-field");
        newPassword.addClassName("form-field");

        FormLayout passwordForm = new FormLayout(oldPassword, newPassword);

        Button changePassword = new Button("Passwort ändern");
        changePassword.addClassName("button");

        Paragraph status = new Paragraph();

        changePassword.addClickListener(e -> {
            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                status.setText("Bitte beide Passwortfelder ausfüllen.");
                status.getStyle().set("color", "red");
                return;
            }

            boolean success = AuthService.getInstance().changePassword(
                    currentUser.getUsername(),
                    oldPassword.getValue(),
                    newPassword.getValue()
            );

            if (success) {
                status.setText("Passwort erfolgreich geändert.");
                status.getStyle().set("color", "green");
                oldPassword.clear();
                newPassword.clear();
            } else {
                status.setText("Fehler: Passwort ungültig.");
                status.getStyle().set("color", "red");
            }
        });

        VerticalLayout container = new VerticalLayout(
                avatar,
                profileForm,
                passwordForm,
                changePassword,
                status
        );
        container.setAlignItems(Alignment.CENTER);
        container.setWidth("100%");
        container.setMaxWidth("400px");
        container.getStyle().set("margin", "0 auto");

        add(container);
    }
}

