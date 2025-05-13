package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;

@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout {

    public RegisterView() {
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        ComboBox<User.Role> role = new ComboBox<>("Rolle");
        role.setItems(User.Role.values());
        Label status = new Label();

        Button submit = new Button("Registrieren", e -> {
            boolean ok = AuthService.getInstance().register(username.getValue(), password.getValue(), role.getValue());
            status.setText(ok ? "Registrierung erfolgreich" : "Benutzername existiert bereits");
        });

        add(username, password, role, submit, status);
    }
}
