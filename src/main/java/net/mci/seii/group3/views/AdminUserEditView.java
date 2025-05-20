package net.mci.seii.group3.views;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;
import com.vaadin.flow.router.BeforeEnterEvent;

@Route(value = "admin/benutzer/edit/:username", layout = MainLayout.class)
public class AdminUserEditView extends VerticalLayout implements BeforeEnterObserver {

    private final Grid<User> userGrid = new Grid<>(User.class, false);
    private final UserForm userForm = new UserForm();

    public AdminUserEditView() {
        setSizeFull();
        userForm.setVisible(false); // Start hidden until a user is selected

        // Setup grid
        userGrid.setItems(AuthService.getInstance().getAllUsers());
        userGrid.addColumn(User::getUsername).setHeader("Benutzername");
        userGrid.addColumn(User::getRole).setHeader("Rolle");

        userGrid.asSingleSelect().addValueChangeListener(event -> {
            User selected = event.getValue();
            if (selected != null) {
                userForm.setUser(selected);
                userForm.setVisible(true);
            } else {
                userForm.setVisible(false);
            }
        });

        userForm.addListener(UserForm.SaveEvent.class, e -> {
            userForm.setVisible(false);
            userGrid.getDataProvider().refreshAll();
        });

        userForm.addListener(UserForm.CancelEvent.class, e -> {
            userForm.setVisible(false);
            userGrid.deselectAll();
        });

        // Responsive layout container
        FlexLayout content = new FlexLayout(userGrid, userForm);
        content.setFlexGrow(4, userGrid);
        content.setFlexGrow(1, userForm);
        content.setSizeFull();
        content.getStyle().set("gap", "1em");
        content.getStyle().set("flex-wrap", "wrap"); // This enables responsive stacking

        add(content);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = event.getRouteParameters().get("username").orElse(null);

        if (username != null) {
            User user = AuthService.getInstance().getUserByName(username);
            if (user != null) {
                userForm.setUser(user);
                userForm.setVisible(true);
            } else {
                Notification.show("Benutzer nicht gefunden");
                event.forwardTo("admin/benutzer");
            }
        } else {
            Notification.show("Kein Benutzername angegeben");
            event.forwardTo("admin/benutzer");
        }
    }

}
