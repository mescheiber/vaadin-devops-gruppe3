
package net.mci.seii.group3.views;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLayout;

public class NoDrawerLayout extends AppLayout implements RouterLayout {



    public NoDrawerLayout() {
        setDrawerOpened(false);
        setPrimarySection(Section.NAVBAR);

        createHeader();
    }

    private void createHeader() {
        // Logo image
        Image logo = new Image("images/logo_app.png", "Logo");
        logo.setHeight("40px");
        logo.getStyle().set("border-radius", "8px");

        // Title text
        H1 title = new H1("AnwesenheitsApp");
        title.addClassNames("text-l", "m-m");

        // Group: logo + title
        HorizontalLayout branding = new HorizontalLayout(logo, title);
        branding.setAlignItems(FlexComponent.Alignment.CENTER);
        branding.setSpacing(true);

        // Outer header layout with empty spacers
        Span leftSpacer = new Span();
        Span rightSpacer = new Span();

        HorizontalLayout header = new HorizontalLayout(leftSpacer, rightSpacer);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        header.setPadding(true);
        header.setSpacing(false);

        header.setFlexGrow(1, leftSpacer, rightSpacer); // Push branding to center

        addToNavbar(header);
    }


}