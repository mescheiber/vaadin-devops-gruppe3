package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import net.mci.seii.group3.model.Schulklasse;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.repository.SchulklassenRepository;
import net.mci.seii.group3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "home", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "TEACHER", "STUDENT"})
public class LandingPage extends VerticalLayout {

    private final UserRepository userRepository;
    private final SchulklassenRepository klassenRepository;

    @Autowired
    public LandingPage(UserRepository userRepository, SchulklassenRepository klassenRepository) {
        this.userRepository = userRepository;
        this.klassenRepository = klassenRepository;

        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        User currentUser = (User) VaadinSession.getCurrent().getAttribute(User.class);
        if (currentUser == null) {
            UI.getCurrent().navigate("");
            return;
        }

        Optional<User> dbUserOpt = userRepository.findById(currentUser.getUsername());
        if (dbUserOpt.isEmpty()) {
            UI.getCurrent().navigate("");
            return;
        }

        User dbUser = dbUserOpt.get();

        // Avatar
        Image avatar = new Image("images/profile-placeholder.png", "Profilbild");
        avatar.setWidth("100px");
        avatar.getStyle().set("border-radius", "50%")
                .set("border", "2px solid #ccc")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        // Profilfelder
        TextField username = new TextField("Benutzername");
        username.setValue(dbUser.getUsername());
        username.setReadOnly(true);

        TextField role = new TextField("Rolle");
        role.setValue(dbUser.getRole().name());
        role.setReadOnly(true);

        TextField klasse = new TextField("Klasse");
        klasse.setReadOnly(true);
        klasse.setVisible(dbUser.getRole() == User.Role.STUDENT);

        if (dbUser.getKlasse() != null) {
            Optional<Schulklasse> klasseEntity = klassenRepository.findById(dbUser.getKlasse());
            klasseEntity.ifPresent(k -> klasse.setValue(k.getName()));
        }

        // Passwort ändern
        PasswordField oldPass = new PasswordField("Altes Passwort");
        PasswordField newPass = new PasswordField("Neues Passwort");

        Button changePass = new Button("Passwort ändern");
        Paragraph status = new Paragraph();

        changePass.addClickListener(e -> {
            if (!oldPass.getValue().equals(dbUser.getPassword())) {
                status.setText("Altes Passwort stimmt nicht.");
                status.getStyle().set("color", "red");
                return;
            }

            if (newPass.getValue().isBlank()) {
                status.setText("Neues Passwort darf nicht leer sein.");
                status.getStyle().set("color", "red");
                return;
            }

            dbUser.setPassword(newPass.getValue());
            userRepository.save(dbUser);
            status.setText("Passwort erfolgreich geändert.");
            status.getStyle().set("color", "green");
            oldPass.clear();
            newPass.clear();
        });

        FormLayout profilForm = new FormLayout(username, role);
        if (klasse.isVisible()) {
            profilForm.add(klasse);
        }

        FormLayout pwForm = new FormLayout(oldPass, newPass);

        add(
                avatar,
                profilForm,
                pwForm,
                changePass,
                status
        );
    }
}
