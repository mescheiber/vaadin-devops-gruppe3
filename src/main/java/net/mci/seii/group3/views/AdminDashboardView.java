package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "admin", layout = MainLayout.class)
public class AdminDashboardView extends VerticalLayout {

    public AdminDashboardView() {
        setPadding(true);
        setSpacing(true);

        Button benutzer = new Button("Benutzerverwaltung", e ->
            getUI().ifPresent(ui -> ui.navigate("admin/benutzer"))
        );

        Button klassen = new Button("Klassenverwaltung", e ->
            getUI().ifPresent(ui -> ui.navigate("admin/klassen"))
        );

        Button veranstaltungen = new Button("Veranstaltungsverwaltung", e ->
            getUI().ifPresent(ui -> ui.navigate("admin/veranstaltungen"))
        );

        add(benutzer, klassen, veranstaltungen);
    }
}
