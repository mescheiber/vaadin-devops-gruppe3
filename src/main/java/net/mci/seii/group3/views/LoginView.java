package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;

@Route("")
public class LoginView extends VerticalLayout {

    public LoginView() {
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Label status = new Label();

        Button login = new Button("Login", e -> {
            User user = AuthService.getInstance().login(username.getValue(), password.getValue());
            if (user != null) {
                VaadinSession.getCurrent().setAttribute(User.class, user);
                if (user.getRole() == User.Role.TEACHER) {
                    getUI().ifPresent(ui -> ui.navigate("lehrer"));
                } else {
                    getUI().ifPresent(ui -> ui.navigate("student"));
                }
            } else {
                status.setText("Login fehlgeschlagen");
            }
        });

        Button register = new Button("Registrieren", e -> {
            getUI().ifPresent(ui -> ui.navigate("register"));
        });

        add(username, password, login, register, status);
    }
}
