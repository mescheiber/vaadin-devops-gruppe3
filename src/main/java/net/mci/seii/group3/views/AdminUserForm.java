package net.mci.seii.group3.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.UserService;

public class AdminUserForm extends FormLayout {

    private final TextField username = new TextField("Benutzername");
    private final PasswordField password = new PasswordField("Passwort");
    private final ComboBox<User.Role> role = new ComboBox<>("Rolle");
    private final ComboBox<String> klasse = new ComboBox<>("Klasse (optional)");

    private final Button save = new Button("Speichern");
    private final Button cancel = new Button("Abbrechen");
    private final Button delete = new Button("Löschen");

    private User user;
    private final Binder<User> binder = new Binder<>(User.class);

    public interface SaveListener extends ComponentEventListener<SaveEvent> {}
    public interface CancelListener extends ComponentEventListener<CancelEvent> {}
    public interface DeleteListener extends ComponentEventListener<DeleteEvent> {}

    public static class SaveEvent extends ComponentEvent<AdminUserForm> {
        private final User user;
        public SaveEvent(AdminUserForm source, User user) {
            super(source, false);
            this.user = user;
        }
        public User getUser() { return user; }
    }

    public static class CancelEvent extends ComponentEvent<AdminUserForm> {
        public CancelEvent(AdminUserForm source) {
            super(source, false);
        }
    }

    public static class DeleteEvent extends ComponentEvent<AdminUserForm> {
        private final User user;
        public DeleteEvent(AdminUserForm source, User user) {
            super(source, false);
            this.user = user;
        }
        public User getUser() { return user; }
    }

    public AdminUserForm(UserService userService, KlassenService klassenService) {
        role.setItems(User.Role.values());
        klasse.setItems(klassenService.getAllKlassenNamen());


        binder.bindInstanceFields(this);

        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                User user = binder.getBean();
                userService.saveUser(user);
                fireEvent(new SaveEvent(this, user));
            }
        });

        delete.addClickListener(e -> {
            if (user != null && user.getUsername() != null) {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Benutzer löschen?");
                dialog.setText("Möchtest du diesen Benutzer wirklich löschen?");
                dialog.setConfirmText("Löschen");
                dialog.setCancelText("Abbrechen");
                dialog.addConfirmListener(event -> fireEvent(new DeleteEvent(this, user)));
                dialog.open();
            }
        });

        cancel.addClickListener(e -> fireEvent(new CancelEvent(this)));

        save.addClassName("button");
        cancel.addClassName("button");
        delete.addClassName("button");
        HorizontalLayout buttons = new HorizontalLayout(save, cancel, delete);
        add(username, password, role, klasse, buttons);
        delete.setVisible(false); // Hide by default
    }

    public void setUser(User user) {
        this.user = user;
        binder.setBean(user);

        boolean isEditingExisting = user != null && user.getUsername() != null && !user.getUsername().isBlank();
        username.setReadOnly(isEditingExisting);
        delete.setVisible(isEditingExisting);

        System.out.println("Setting user: " + user + ", delete visible: " + delete.isVisible());
    }


    public void addSaveListener(SaveListener listener) {
        addListener(SaveEvent.class, listener);
    }

    public void addCancelListener(CancelListener listener) {
        addListener(CancelEvent.class, listener);
    }

    public void addDeleteListener(DeleteListener listener) {
        addListener(DeleteEvent.class, listener);
    }
}
