package com.example.application.views;

import com.example.application.data.User;
import com.example.application.services.SecurityUserDetailsService;
import com.example.application.views.list.UserForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Route("register") 
@PageTitle("Register | Vaadin CRM")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
	private final SecurityUserDetailsService service;
	UserForm form;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;


	public RegisterView(SecurityUserDetailsService service) { // <2>
		this.service = service;
		addClassName("register-view");
		setDefaultHorizontalComponentAlignment(Alignment.CENTER); // <3>
		configureForm();
		add(getContent());

	}

	private void configureForm() {
		form = new UserForm();
		form.setUser(new User());

		form.setWidth("25em");
		form.addSaveListener(this::saveUser); // <1>


	}

	private void saveUser(UserForm.SaveEvent event) {
		User user = event.getUser();
		user.setPassword((bCryptPasswordEncoder.encode(user.getPassword())));

		service.createUser(user);
		Notification notification = Notification
				.show("User has been registered successfully.!");
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		form.setUser(new User());
	}

	private HorizontalLayout getContent() {
		HorizontalLayout content = new HorizontalLayout( form);
		content.addClassNames("content");
		content.setSizeFull();
		return content;
	}
}