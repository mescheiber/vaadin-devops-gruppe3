package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.PersistenzService;
import net.mci.seii.group3.service.VeranstaltungsService;

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
        // Logo mit Bild + Titel
        Image logoImage = new Image("images/MCI_Logo_Subtitle.png", "Logo");
        logoImage.setWidth("100px");
        logoImage.getStyle().set("margin-right", "10px");

        H1 logoText = new H1("AnwesenheitsApp");
        logoText.addClassName("title");

        HorizontalLayout logoLayout = new HorizontalLayout(logoImage, logoText);
        logoLayout.setAlignItems(Alignment.CENTER);
        logoLayout.addClassName("logo-layout");

        // Zurück-Button mit Rollenlogik
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
        backButton.addClassName("button");

        // Logout-Button
        Button logout = new Button("Logout", event -> {
            VaadinSession.getCurrent().setAttribute(User.class, null);
            UI.getCurrent().navigate("login");
        });
        logout.addClassName("button");

        // Rechte Button-Gruppe
        HorizontalLayout buttonLayout = new HorizontalLayout(backButton, logout);
        buttonLayout.setSpacing(true);
        buttonLayout.setAlignItems(Alignment.CENTER);

        // Header mit Styling
        HorizontalLayout header = new HorizontalLayout(logoLayout, buttonLayout);
        header.setWidthFull();
        header.setPadding(true);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.setClassName("header");

        addToNavbar(header);
    }
}
