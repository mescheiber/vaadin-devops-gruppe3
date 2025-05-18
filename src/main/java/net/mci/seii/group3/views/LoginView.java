package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;

@Route(value = "", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Logo
        Image logo = new Image("images/MCI_Logo.png", "MCI Logo");
        logo.addClassName("logo");

        // Titel
        H1 title = new H1("AnwesenheitsApp");
        title.addClassName("title");

        // Felder
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Label status = new Label();

        // Button
        Button login = new Button("Login", e -> {
            User user = AuthService.getInstance().login(username.getValue(), password.getValue());

            if (user != null) {
                VaadinSession.getCurrent().setAttribute(User.class, user);
                AuthService.getInstance().setAngemeldeterBenutzer(user);

                switch (user.getRole()) {
                    case ADMIN -> getUI().ifPresent(ui -> ui.navigate("admin"));
                    case TEACHER -> getUI().ifPresent(ui -> ui.navigate("lehrer"));
                    case STUDENT -> getUI().ifPresent(ui -> ui.navigate("student"));
                }
            } else {
                status.setText("Login fehlgeschlagen");
            }
        });

        login.addClassName("button");

        // Login-Form
        VerticalLayout loginForm = new VerticalLayout(logo, title, username, password, login, status);
        loginForm.setAlignItems(FlexComponent.Alignment.CENTER);
        loginForm.addClassName("login-form");

        add(loginForm);
    }
}
