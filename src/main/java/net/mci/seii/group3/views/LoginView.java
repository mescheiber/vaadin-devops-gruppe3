package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Route(value = "login", layout = NoDrawerLayout.class)
@PageTitle("Login")
@PermitAll
public class LoginView extends VerticalLayout {

    private final AuthService authService;

    @Autowired
    public LoginView(AuthService authService) {
        this.authService = authService;

        addClassName("login-view");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Image logo = new Image("images/MCI_Logo.png", "MCI Logo");
        logo.addClassName("logo");
        H1 title = new H1("AnwesenheitsApp");
        title.addClassName("title");

        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Paragraph status = new Paragraph();

        Button login = new Button("Login", e -> {
            User user = authService.login(username.getValue(), password.getValue());
            System.out.println("Login attempt: " + username.getValue());

            if (user != null) {
                System.out.println("Logged in: " + user.getUsername() + ", Role: " + user.getRole());
                VaadinSession.getCurrent().setAttribute(User.class, user);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                        );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                VaadinSession.getCurrent().getSession()
                        .setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());



                getUI().ifPresent(ui -> ui.navigate("home"));

            } else {
                status.setText("Login fehlgeschlagen");
                System.out.println("Login failed");
            }
        });

        VerticalLayout branding = new VerticalLayout(logo, title);
        branding.setAlignItems(FlexComponent.Alignment.CENTER);

        login.addClassName("button");

        VerticalLayout form = new VerticalLayout(branding, username, password, login, status);
        form.setWidth("300px");
        form.setAlignItems(FlexComponent.Alignment.STRETCH);
        form.getStyle().set("box-shadow", "0 2px 10px rgba(0,0,0,0.1)");
        form.getStyle().set("border-radius", "8px");
        form.getStyle().set("background", "white");
        form.getStyle().set("padding", "2rem");

        add(form);
    }
}
