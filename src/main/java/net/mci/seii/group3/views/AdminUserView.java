package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.AuthService;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.PersistenzService;

@Route(value = "admin/benutzer", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
public class AdminUserView extends VerticalLayout  implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null) {
            event.forwardTo("");
        }
    }
    private final Grid<User> userGrid = new Grid<>(User.class, false);
    private final UserForm userForm = new UserForm();
    private final Button addUserButton = new Button("Benutzer hinzufügen");
    private boolean isNewUser = false;





    public AdminUserView() {
        setSizeFull();
        setSpacing(true);
        setPadding(true);
        userGrid.addClassName("grid");

        userForm.setVisible(false);


        configureGrid();
        configureForm();
        configureAddButton();

        FlexLayout content = new FlexLayout(userGrid, userForm);
        content.setFlexGrow(4, userGrid);
        content.setFlexGrow(1, userForm);
        content.setSizeFull();
        content.getStyle().set("gap", "1em");
        content.getStyle().set("flex-wrap", "wrap");

        add(addUserButton, content);
    }

    private void configureGrid() {
        userGrid.addColumn(User::getUsername).setHeader("Benutzername");
        userGrid.addColumn(User::getRole).setHeader("Rolle");
        userGrid.setItems(AuthService.getInstance().getAllUsers());

        userGrid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                isNewUser = false;
                userForm.setUser(e.getValue());
                userForm.setVisible(true);
            } else {
                userForm.setVisible(false);
            }
        });

    }

    private void configureForm() {
        userForm.addListener(UserForm.SaveEvent.class, e -> {
            User savedUser = e.getUser();

            if (isNewUser) {
                boolean success = AuthService.getInstance().register(
                        savedUser.getUsername(),
                        e.getPassword(),
                        savedUser.getRole());

                if (success && savedUser.getRole() == User.Role.STUDENT && userForm.getSelectedKlasse() != null) {
                    KlassenService.getInstance().schuelerZurKlasse(userForm.getSelectedKlasse(), savedUser.getUsername());
                }

            } else {
                AuthService.getInstance().updateUser(savedUser);
                KlassenService.getInstance().removeBenutzerAusAllenKlassen(savedUser.getUsername());
                if (savedUser.getRole() == User.Role.STUDENT && userForm.getSelectedKlasse() != null) {
                    KlassenService.getInstance().schuelerZurKlasse(userForm.getSelectedKlasse(), savedUser.getUsername());
                }
            }

            PersistenzService.speichernAlles();
            userGrid.setItems(AuthService.getInstance().getAllUsers());
            userForm.setVisible(false);
        });

        userForm.addListener(UserForm.CancelEvent.class, e -> {
            userForm.setVisible(false);
            userGrid.deselectAll();
        });

        userForm.addListener(UserForm.DeleteEvent.class, deleteEvent -> {
            User userToDelete = deleteEvent.getUser();

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Benutzer löschen?");
            dialog.setText("Möchten Sie den Benutzer wirklich löschen?");

            dialog.setCancelable(true);
            dialog.setConfirmText("Ja, löschen");
            dialog.setCancelText("Abbrechen");

            dialog.addConfirmListener(event -> {
                AuthService.getInstance().deleteUser(userToDelete.getUsername());
                KlassenService.getInstance().removeBenutzerAusAllenKlassen(userToDelete.getUsername());

                PersistenzService.speichernAlles();
                userGrid.setItems(AuthService.getInstance().getAllUsers());
                System.out.println("closed confirm popup");
                userForm.setVisible(false);
            });

            dialog.open();
        });
    }



    private void configureAddButton() {
        addUserButton.addClickListener(e -> {
            userGrid.deselectAll();
            userForm.setUser(new User()); // new blank user
            isNewUser = true;
            userForm.setVisible(true);
        });
    }

}

