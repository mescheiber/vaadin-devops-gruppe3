package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
    }

    private void createHeader() {
        // Logo als Link
        Anchor logo = new Anchor("/", "📚 AnwesenheitsApp");
        logo.addClassName("logo");

        // Zurück-Button basierend auf Rolle
        Button backButton = new Button("Zurück", event -> {
            User user = (User) VaadinSession.getCurrent().getAttribute(User.class);
            if (user != null) {
                if (user.getRole() == User.Role.TEACHER) {
                    UI.getCurrent().navigate("lehrer");
                } else {
                    UI.getCurrent().navigate("student");
                }
            } else {
                UI.getCurrent().navigate("");
            }
        });

        // Logout
        Button logout = new Button("Logout", event -> {
            VaadinSession.getCurrent().setAttribute(User.class, null);
            UI.getCurrent().navigate("login");
        });

        HorizontalLayout header = new HorizontalLayout(logo, backButton, logout);
        header.setWidthFull();
        header.setPadding(true);
        header.setClassName("header");

        addToNavbar(header);
    }
}
