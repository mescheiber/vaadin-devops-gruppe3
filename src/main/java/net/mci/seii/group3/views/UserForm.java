package net.mci.seii.group3.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.KlassenService;

import java.util.List;

public class UserForm extends FormLayout {

    private final TextField username = new TextField("Benutzername");
    private final ComboBox<User.Role> rolleBox = new ComboBox<>("Rolle");
    private final ComboBox<String> klasseBox = new ComboBox<>("Klasse (nur für Schüler)");

    private final Button speichern = new Button("Speichern");
    private final Button abbrechen = new Button("Abbrechen");

    private final Binder<User> binder = new Binder<>(User.class);
    private User user;
    
    private final KlassenService klassenService;


    public UserForm(KlassenService klassenService) {
        this.klassenService = klassenService;
        addClassName("user-form");

        setResponsiveSteps(
            new ResponsiveStep("0", 1)  // Always vertical
        );

        rolleBox.setItems(User.Role.values());

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
        return new HorizontalLayout(speichern, abbrechen);
    }

    public void setUser(User user) {
    this.user = user;

    // Rolle und Username setzen (optional, falls nicht über Binder)
    rolleBox.setValue(user.getRole());
    username.setValue(user.getUsername());

    // Items in klasseBox setzen, falls nicht gesetzt
    if (klasseBox.getDataProvider().isInMemory()) {
        // Du kannst hier dynamisch aus dem Service laden
        klasseBox.setItems(klassenService.getAllKlassenNamen());
    }

    // Nur wenn Rolle STUDENT ist, setze den Wert
    if (user.getRole() == User.Role.STUDENT) {
        klasseBox.setVisible(true);
        klasseBox.setValue(user.getKlasse());
    } else {
        klasseBox.clear();
        klasseBox.setVisible(false);
    }

    // Sichtbarkeit auch bei Änderung der Rolle dynamisch anpassen
    rolleBox.addValueChangeListener(event -> {
        boolean istSchueler = event.getValue() == User.Role.STUDENT;
        klasseBox.setVisible(istSchueler);
        if (!istSchueler) {
            klasseBox.clear();
        }
    });

    // Bind bean to fields (falls du Binder verwendest)
    binder.readBean(user);
    
    }

    public void setKlassenListe(List<String> klassenNamen) {
        klasseBox.setItems(klassenNamen);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(user);
            if (user.getRole() == User.Role.STUDENT) {
                user.setKlasse(klasseBox.getValue());
            } else {
                user.setKlasse(null);
            }
            fireEvent(new SaveEvent(this, user));
        } catch (ValidationException e) {
            e.printStackTrace(); // Optional: Log error properly
        }
    }

    // Event-Struktur
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
