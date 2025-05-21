package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.repository.VeranstaltungsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@PermitAll
@Route(value = "student", layout = MainLayout.class)
public class StudentenView extends VerticalLayout {

    private final VeranstaltungsRepository veranstaltungsRepository;

    @Autowired
    public StudentenView(VeranstaltungsRepository veranstaltungsRepository) {
        this.veranstaltungsRepository = veranstaltungsRepository;

        setPadding(true);
        setSpacing(true);

        User currentUser = (User) VaadinSession.getCurrent().getAttribute(User.class);
        if (currentUser == null || currentUser.getRole() != User.Role.STUDENT) {
            UI.getCurrent().navigate("");
            return;
        }

        Grid<Veranstaltung> grid = new Grid<>(Veranstaltung.class, false);
        grid.addColumn(Veranstaltung::getName).setHeader("Veranstaltung");
        grid.addColumn(v -> v.getStartzeit().toString()).setHeader("Startzeit");

        List<Veranstaltung> relevanteVeranstaltungen = veranstaltungsRepository.findAll().stream()
            .filter(v -> v.getTeilnehmer().contains(currentUser.getUsername()))
            .filter(v -> v.getStartzeit().isAfter(LocalDateTime.now().minusMinutes(30)))
            .filter(v -> !v.getTeilnahmen().containsKey(currentUser.getUsername()))
            .toList();

        grid.setItems(relevanteVeranstaltungen);

        grid.asSingleSelect().addValueChangeListener(e -> {
            Veranstaltung v = e.getValue();
            if (v != null) {
                UI.getCurrent().navigate("veranstaltung/" + v.getId());
            }
        });

        add(grid);
    }
}
