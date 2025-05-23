package net.mci.seii.group3.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.router.Route;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.repository.UserRepository;
import net.mci.seii.group3.service.KlassenService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "admin/benutzer/edit/:username", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminUserEditView extends VerticalLayout implements BeforeEnterObserver {

    private final Grid<User> userGrid = new Grid<>(User.class, false);
    private final UserForm userForm;
    private final KlassenService klassenService;
    private final UserRepository userRepository;

    @Autowired
    public AdminUserEditView(KlassenService klassenService, UserRepository userRepository) {
        this.klassenService = klassenService;
        this.userRepository = userRepository;
        this.userForm = new UserForm(klassenService); // korrekt initialisiert

        setSizeFull();
        userForm.setVisible(false);

        userGrid.setItems(userRepository.findAll());
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
            userRepository.save(e.getUser());
            userForm.setVisible(false);
            userGrid.setItems(userRepository.findAll());
        });

        userForm.addListener(UserForm.CancelEvent.class, e -> {
            userForm.setVisible(false);
            userGrid.deselectAll();
        });

        FlexLayout content = new FlexLayout(userGrid, userForm);
        content.setFlexGrow(4, userGrid);
        content.setFlexGrow(1, userForm);
        content.setSizeFull();
        content.getStyle().set("gap", "1em");
        content.getStyle().set("flex-wrap", "wrap");

        add(content);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = event.getRouteParameters().get("username").orElse(null);
        if (username != null) {
            userRepository.findByUsername(username).ifPresentOrElse(user -> {
                userForm.setUser(user);
                userForm.setVisible(true);
            }, () -> {
                Notification.show("Benutzer nicht gefunden");
                event.forwardTo("admin/benutzer");
            });
        } else {
            Notification.show("Kein Benutzername angegeben");
            event.forwardTo("admin/benutzer");
        }
    }
}
