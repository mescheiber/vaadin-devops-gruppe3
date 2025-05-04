package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.service.VeranstaltungsService;

import java.time.LocalDateTime;
import java.util.List;

@Route("student")
public class StudentenView extends VerticalLayout implements AfterNavigationObserver {

    public StudentenView() {
        User student = (User) VaadinSession.getCurrent().getAttribute(User.class);
        if (student == null || student.getRole() != User.Role.STUDENT) {
            UI.getCurrent().navigate("");
            return;
        }

        Grid<Veranstaltung> grid = new Grid<>(Veranstaltung.class, false);
        grid.addColumn(Veranstaltung::getName).setHeader("Veranstaltung");
        grid.addColumn(v -> v.getStartzeit().toString()).setHeader("Zeit");

        List<Veranstaltung> veranstaltungen = VeranstaltungsService.getInstance()
            .getAlleVeranstaltungen().stream()
            .filter(v -> v.getTeilnehmer().contains(student.getUsername()))
            .filter(v -> v.getStartzeit().isAfter(LocalDateTime.now().minusMinutes(30)))
            .filter(v -> !v.getTeilnahmen().containsKey(student.getUsername())) // erledigte ausblenden
            .toList();

        grid.setItems(veranstaltungen);

        grid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                UI.getCurrent().navigate("veranstaltung/" + e.getValue().getId());
            }
        });

        add(grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setHeightFull();
    }
}
