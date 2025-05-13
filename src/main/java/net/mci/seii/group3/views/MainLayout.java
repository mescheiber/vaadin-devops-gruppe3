package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.*;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    private static boolean datenBereitsGeladen = false;

    public MainLayout() {
        // Initial-Laden der Daten beim ersten Aufruf
        if (!datenBereitsGeladen) {
            var daten = PersistenzService.laden();
            if (daten != null) {
                AuthService.getInstance().setAll(daten.users);
                VeranstaltungsService.getInstance().setAll(daten.veranstaltungen);
                KlassenService.getInstance().setAlleKlassen(daten.klassen);
            }
            datenBereitsGeladen = true;
        }

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
                switch (user.getRole()) {
                    case ADMIN -> UI.getCurrent().navigate("admin");
                    case TEACHER -> UI.getCurrent().navigate("lehrer");
                    case STUDENT -> UI.getCurrent().navigate("student");
                    default -> UI.getCurrent().navigate("");
                }
            } else {
                UI.getCurrent().navigate("");
            }
        });

        // Logout
        Button logout = new Button("Logout", event -> {
            VaadinSession.getCurrent().setAttribute(User.class, null);
            UI.getCurrent().navigate("");
        });

        HorizontalLayout header = new HorizontalLayout(logo, backButton, logout);
        header.setWidthFull();
        header.setPadding(true);
        header.setClassName("header");

        addToNavbar(header);
    }
}
