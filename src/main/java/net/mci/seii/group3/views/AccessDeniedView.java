package net.mci.seii.group3.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("access-denied")
@PermitAll
public class AccessDeniedView extends VerticalLayout {
    public AccessDeniedView() {
        add(new H2("Zugriff verweigert"));
        add(new Paragraph("Sie haben keine Berechtigung, um diese Seite zu sehen."));
    }
}
