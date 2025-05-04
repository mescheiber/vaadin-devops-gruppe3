package com.example.application.views.list;

import com.example.application.data.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.shared.Registration;

public class UserForm extends FormLayout {
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");

    PasswordField password = new PasswordField("Password");
    TextField username = new TextField("User name");
    EmailField email = new EmailField("Email");
    TextField role = new TextField("Role");

    Button save = new Button("Save");
    Button close = new Button("Cancel");
    // Other fields omitted
    Binder<User> binder = new BeanValidationBinder<>(User.class);

    public UserForm() {
        addClassName("user-form");
        binder.bindInstanceFields(this);

        add(firstName,
                lastName,
                username,
                password,
                email,
                role,
                createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickShortcut(Key.ENTER);

        save.addClickListener(event -> validateAndSave()); // <1>

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); // <4>
        return new HorizontalLayout(save);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
         /*   String user = VaadinService.getCurrentRequest().getWrappedSession()
                    .getAttribute("username").toString();
            System.out.println("logged in user:" + user);*/
            fireEvent(new SaveEvent(this, binder.getBean())); // <6>
        }
    }


    public void setUser(User user) {

        binder.setBean(user); // <1>
    }

    // Events
    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private User user;

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



    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }



}
