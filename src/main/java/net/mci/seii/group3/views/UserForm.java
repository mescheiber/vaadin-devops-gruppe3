package net.mci.seii.group3.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.KlassenService;


public class UserForm extends FormLayout {

    private final TextField username = new TextField("Benutzername");
    private final ComboBox<User.Role> rolleBox = new ComboBox<>("Rolle");
    private final ComboBox<String> klasseBox = new ComboBox<>("Klasse (nur für Schüler)");

    private final Button speichern = new Button("Speichern");
    private final Button abbrechen = new Button("Abbrechen");

    private final Binder<User> binder = new Binder<>(User.class);
    private User user;

    public UserForm() {
        addClassName("user-form");

        setResponsiveSteps(
                new ResponsiveStep("0", 1)  // always vertical
        );

        rolleBox.setItems(User.Role.values());
        klasseBox.setItems(KlassenService.getInstance().getAllKlassenNamen());

        username.addClassName("form-field");
        rolleBox.addClassName("form-field");
        klasseBox.addClassName("form-field");

        binder.bindInstanceFields(this);

        speichern.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        abbrechen.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        speichern.addClickListener(e -> validateAndSave());
        abbrechen.addClickListener(e -> fireEvent(new CancelEvent(this)));

        add(username, rolleBox, klasseBox, createButtonLayout());

    }



    private Component createButtonLayout() {
        return new com.vaadin.flow.component.orderedlayout.HorizontalLayout(speichern, abbrechen);
    }

    public void setUser(User user) {
        this.user = user;
        binder.readBean(user);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(user);
            fireEvent(new SaveEvent(this, user));
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
        SaveEvent(UserForm source, User user) {
            super(source, user);
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
