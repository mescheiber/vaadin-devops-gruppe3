package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "admin/benutzer", layout = MainLayout.class)
@PageTitle("Benutzerverwaltung")
@RolesAllowed("ADMIN")
public class AdminUserView extends VerticalLayout {

    private final UserService userService;
    private final Grid<User> grid = new Grid<>(User.class, false);
    private final AdminUserForm form;

    @Autowired
    public AdminUserView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        grid.addColumn(User::getUsername).setHeader("Benutzername");
        grid.addColumn(user -> user.getRole().name()).setHeader("Rolle");
        grid.addColumn(User::getKlasse).setHeader("Klasse");
        grid.setItems(userService.findAllUsers());
        grid.addClassName("grid");

        grid.asSingleSelect().addValueChangeListener(event ->
                editUser(event.getValue())
        );

        Button addUser = new Button("Add Student", event -> addNewUser());
        addUser.addClassName("button");
        addUser.getStyle().set("margin-bottom", "1rem");

        form = new AdminUserForm(userService);
        form.setWidth("400px");
        form.setVisible(false);
        form.addSaveListener(this::saveUser);
        form.addCancelListener(e -> closeEditor());
        form.addDeleteListener(this::deleteUser);

        HorizontalLayout layout = new HorizontalLayout(grid, form);
        layout.setSizeFull();
        layout.setFlexGrow(2, grid);
        layout.setFlexGrow(1, form);

        add(addUser, layout);
    }

    private void editUser(User user) {
        if (user == null) {
            closeEditor();
        } else {
            form.setUser(user);
            form.setVisible(true);
        }
    }

    private void addNewUser() {
        form.setUser(new User());
        form.setVisible(true);
    }

    private void saveUser(AdminUserForm.SaveEvent event) {
        userService.saveUser(event.getUser());
        grid.setItems(userService.findAllUsers());
        closeEditor();
    }

    private void closeEditor() {
        form.setVisible(false);
        grid.asSingleSelect().clear();
    }

    private void deleteUser(AdminUserForm.DeleteEvent event) {
        userService.deleteUser(event.getUser());
        grid.setItems(userService.findAllUsers());
        closeEditor();
    }


}

