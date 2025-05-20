package net.mci.seii.group3.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.KlassenService;

public class UserForm extends FormLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (user == null) {
            event.forwardTo("");
        }
    }
    private final TextField username = new TextField("Benutzername");
    private final PasswordField password = new PasswordField("Passwort");
    private final ComboBox<User.Role> rolleBox = new ComboBox<>("Rolle");
    private final ComboBox<String> klasseBox = new ComboBox<>("Klasse (nur für Schüler)");

    private final Button speichern = new Button("Speichern");
    private final Button abbrechen = new Button("Abbrechen");
    private final Button deleteButton = new Button("Löschen");

    private final Binder<User> binder = new Binder<>(User.class);
    private User user;
    private java.util.function.Consumer<String> passwordConsumer;

    public UserForm() {
        addClassName("user-form");

        setResponsiveSteps(
                new ResponsiveStep("0", 1)  // always vertical layout
        );

        rolleBox.setItems(User.Role.values());
        klasseBox.setItems(KlassenService.getInstance().getAllKlassenNamen());

        username.addClassName("form-field");
        rolleBox.addClassName("form-field");
        klasseBox.addClassName("form-field");

        // Secure password input setup
        password.setRevealButtonVisible(false);
        password.getElement().setAttribute("placeholder", "********");
        password.getElement().setAttribute("autocomplete", "new-password");
        password.addClassName("form-field");

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // Bind only username and role to avoid exposing password
        binder.forField(username).bind(User::getUsername, User::setUsername);
        binder.forField(rolleBox).bind(User::getRole, User::setRole);

        speichern.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        abbrechen.addThemeVariants(ButtonVariant.LUMO_TERTIARY);



        add(username, password, rolleBox, klasseBox, createButtonLayout());
    }

    private Component createButtonLayout() {
        speichern.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        abbrechen.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        speichern.addClickListener(e -> validateAndSave());
        deleteButton.addClickListener(e -> fireEvent(new DeleteEvent(this, user)));

        return new HorizontalLayout(speichern, deleteButton, abbrechen);
    }


    public void setPasswordConsumer(java.util.function.Consumer<String> consumer) {
        this.passwordConsumer = consumer;
    }

    public static class DeleteEvent extends UserFormEvent {
        public DeleteEvent(UserForm source, User user) {
            super(source, user);
        }
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;

        if (user != null) {
            binder.readBean(user); // fills fields: username, role

            password.clear();

            // Load class name for STUDENT
            if (user.getRole() == User.Role.STUDENT) {
                String klasse = KlassenService.getInstance().getKlasseVonSchueler(user.getUsername());
                klasseBox.setValue(klasse);
            } else {
                klasseBox.clear(); // no class for TEACHER or ADMIN
            }
        } else {
            this.user = new User(); // initialize empty user
            binder.readBean(this.user);

            // Explicitly clear form inputs
            password.clear();
            klasseBox.clear();
        }
    }


    private void validateAndSave() {
        try {
            binder.writeBean(user);

            if (passwordConsumer != null && !password.isEmpty()) {
                passwordConsumer.accept(password.getValue());
            }

            fireEvent(new SaveEvent(this, user, password.getValue()));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public String getSelectedKlasse() {
        return klasseBox.getValue();
    }

    // Custom Events
    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private final User user;

        protected UserFormEvent(UserForm source, User user) {
            super(source, false);
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class SaveEvent extends UserFormEvent {
        private final String password;

        public SaveEvent(UserForm source, User user, String password) {
            super(source, user);
            this.password = password;
        }

        public String getPassword() {
            return password;
        }
    }



    public static class CancelEvent extends UserFormEvent {
        CancelEvent(UserForm source) {
            super(source, null);
        }
    }



    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
