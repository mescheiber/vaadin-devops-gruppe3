package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.repository.UserRepository;

@Route(value = "admin/benutzer", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminUserView extends VerticalLayout {

    private final UserRepository userRepository;

    public AdminUserView(UserRepository userRepository) {
        this.userRepository = userRepository;

        setPadding(true);
        setSpacing(true);

        Grid<User> grid = new Grid<>(User.class, false);
        grid.addColumn(User::getUsername).setHeader("Benutzername");
        grid.addColumn(u -> u.getRole().name()).setHeader("Rolle");

        grid.asSingleSelect().addValueChangeListener(event -> {
            User ausgewaehlt = event.getValue();
            if (ausgewaehlt != null) {
                getUI().ifPresent(ui -> ui.navigate("admin/benutzer/edit/" + ausgewaehlt.getUsername()));
            }
        });

        grid.setItems(userRepository.findAll());

        Button neuerUser = new Button("Neuen Benutzer anlegen", e ->
                getUI().ifPresent(ui -> ui.navigate("admin/benutzer/form"))
        );
        neuerUser.addClassName("button");

        H3 titel = new H3("Benutzerverwaltung");
        titel.addClassName("title");

        add(titel, new HorizontalLayout(neuerUser), grid);
    }
}
