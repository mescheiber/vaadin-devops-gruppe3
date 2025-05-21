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
import jakarta.annotation.security.PermitAll;
import net.mci.seii.group3.model.User;

@CssImport("./styles/shared-styles.css")
@PermitAll
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    public MainLayout() {
        createHeader();
        createDrawer();
        getElement().setAttribute("overlay", "");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null) {
            event.forwardTo("");
        }
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        Button logoutButton = new Button("Logout", e -> {
            VaadinSession.getCurrent().close(); // Benutzer-Session invalidieren
            UI.getCurrent().navigate("");
        });
        logoutButton.addClassName("logoutButton");

        Image logo = new Image("images/logo_app.png", "Logo");
        logo.setHeight("40px");
        logo.getStyle().set("border-radius", "8px");

        H1 title = new H1("AnwesenheitsApp");
        title.addClassNames("text-l", "m-m");

        HorizontalLayout branding = new HorizontalLayout(logo, title);
        branding.setAlignItems(FlexComponent.Alignment.CENTER);
        branding.setSpacing(true);

        Span leftSpacer = new Span();
        Span rightSpacer = new Span();

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
