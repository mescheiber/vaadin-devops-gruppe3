package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;

@Route(value = "admin/benutzer", layout = MainLayout.class)
public class AdminUserView extends VerticalLayout {

    public AdminUserView() {
        setPadding(true);
        setSpacing(true);

        Grid<User> grid = new Grid<>(User.class, false);
        grid.addColumn(User::getUsername).setHeader("Benutzername");
        grid.addColumn(u -> u.getRole().name()).setHeader("Rolle");

        // Datensatz auswählen und zur Edit-View navigieren
        grid.asSingleSelect().addValueChangeListener(event -> {
            User ausgewählt = event.getValue();
            if (ausgewählt != null) {
                getUI().ifPresent(ui -> ui.navigate("admin/benutzer/edit/" + ausgewählt.getUsername()));
            }
        });
        grid.addClassName("grid");

        // Daten anzeigen
        grid.setItems(AuthService.getInstance().getAllUsers());

        Button neuerUser = new Button("Neuen Benutzer anlegen", e ->
                getUI().ifPresent(ui -> ui.navigate("admin/benutzer/form"))
        );
        neuerUser.addClassName("button");

//        Button zurück = new Button("Zurück", e ->
//                getUI().ifPresent(ui -> ui.navigate("admin"))
//        );
//        zurück.addClassName("button");

        H3 titel = new H3("Benutzerverwaltung");
        titel.addClassName("title");

        add(
                titel,
                new HorizontalLayout(neuerUser),
                grid
        );
    }
}
