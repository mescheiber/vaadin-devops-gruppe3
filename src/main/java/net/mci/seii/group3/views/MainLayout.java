package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.*;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null) {
            event.forwardTo("");
        }
    }
    private static boolean datenBereitsGeladen = false;

    public MainLayout() {
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
        createDrawer();
        getElement().setAttribute("overlay", "");
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        // Logout button
        Button logoutButton = new Button("Logout", e -> {
            AuthService.getInstance().logout(); // Encapsulate logout logic here
            UI.getCurrent().navigate("");
        });
        logoutButton.addClassName("logoutButton");

        // Logo + title
        Image logo = new Image("images/logo_app.png", "Logo");
        logo.setHeight("40px");
        logo.getStyle().set("border-radius", "8px");

        H1 title = new H1("AnwesenheitsApp");
        title.addClassNames("text-l", "m-m");

        HorizontalLayout branding = new HorizontalLayout(logo, title);
        branding.setAlignItems(FlexComponent.Alignment.CENTER);
        branding.setSpacing(true);

        // Spacers for layout balance
        Span leftSpacer = new Span();
        Span rightSpacer = new Span();

        // Header layout
        HorizontalLayout header = new HorizontalLayout(toggle, leftSpacer, branding, rightSpacer, logoutButton);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.setPadding(true);
        header.setWidthFull();
        header.setFlexGrow(1, leftSpacer);
        header.setFlexGrow(1, rightSpacer);

        addToNavbar(header);
    }

    private void createDrawer() {
        H1 drawerTitle = new H1("Navigation");
        drawerTitle.addClassName("drawer-title");

        RouterLink benutzer = new RouterLink("Benutzerverwaltung", AdminUserView.class);
        RouterLink klassen = new RouterLink("Klassenverwaltung", AdminKlassenView.class);
        RouterLink veranstaltungen = new RouterLink("Veranstaltungen", AdminVeranstaltungView.class);

        benutzer.setHighlightCondition(HighlightConditions.sameLocation());
        klassen.setHighlightCondition(HighlightConditions.sameLocation());
        veranstaltungen.setHighlightCondition(HighlightConditions.sameLocation());

        benutzer.addClassName("drawer-link");
        klassen.addClassName("drawer-link");
        veranstaltungen.addClassName("drawer-link");

        VerticalLayout drawerContent = new VerticalLayout(drawerTitle, benutzer, klassen, veranstaltungen);
        drawerContent.addClassName("drawer-content");

        addToDrawer(drawerContent);
    }


}
