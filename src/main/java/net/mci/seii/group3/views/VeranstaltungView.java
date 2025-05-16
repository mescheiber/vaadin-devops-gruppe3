package net.mci.seii.group3.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.service.*;
import net.mci.seii.group3.utils.PdfExportService;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Route(value = "veranstaltung/:id", layout = MainLayout.class)
public class VeranstaltungView extends VerticalLayout implements BeforeEnterObserver {

    private Veranstaltung veranstaltung;
    private Grid<String> teilnehmerGrid;
    private final Span exportStatus = new Span();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get("id").orElse(null);
        User currentUser = (User) VaadinSession.getCurrent().getAttribute(User.class);

        veranstaltung = VeranstaltungsService.getInstance().getAlleVeranstaltungen().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst().orElse(null);

        if (veranstaltung == null || currentUser == null) {
            event.forwardTo("");
            return;
        }

        if (currentUser.getRole() == User.Role.STUDENT) {
            add(new Span("Veranstaltung: " + veranstaltung.getName()));
            add(new Span("Startzeit: " + veranstaltung.getStartzeit().format(FORMATTER)));
            add(new Span("Lehrer: " + String.join(", ", veranstaltung.getZugewieseneLehrer())));
            if (!veranstaltung.getTeilnahmen().containsKey(currentUser.getUsername())) {
                TextField kennwort = new TextField("Kennwort");
                Button teilnehmen = new Button("Teilnehmen", e -> {
                    boolean erlaubt = VeranstaltungsService.getInstance().prüfenTeilnahme(
                            veranstaltung.getId(), kennwort.getValue(), currentUser.getUsername());
                    if (erlaubt) {
                        Notification.show("Teilnahme erfolgreich!");
                        UI.getCurrent().navigate("student");
                    } else {
                        Notification.show("Teilnahme nicht möglich.");
                    }
                });
                add(kennwort, teilnehmen);
            } else {
                Notification.show("Du hast bereits teilgenommen.");
                UI.getCurrent().navigate("student");
            }
            return;
        }

        HorizontalLayout titleRow = new HorizontalLayout();
        titleRow.setAlignItems(Alignment.BASELINE);
        TextField nameField = new TextField("Titel");
        nameField.setValue(veranstaltung.getName());
        DateTimePicker startzeitField = new DateTimePicker("Startzeit");
        startzeitField.setValue(veranstaltung.getStartzeit());
        TextField kennwortField = new TextField("Kennwort");
        kennwortField.setValue(Optional.ofNullable(veranstaltung.getKennwort()).orElse("(leer)"));
        kennwortField.setReadOnly(true);

        Button speichern = new Button("Speichern", e -> {
            veranstaltung.setName(nameField.getValue());
            veranstaltung.setStartzeit(startzeitField.getValue());
            PersistenzService.speichernAlles();
            Notification.show("Gespeichert.");
        });
        titleRow.add(nameField, startzeitField, kennwortField, speichern);
        add(titleRow);

        if (currentUser.getRole() == User.Role.ADMIN || currentUser.getRole() == User.Role.TEACHER) {
            Span lehrerTitel = new Span("Zugewiesene Lehrer:");
            Grid<String> lehrerGrid = new Grid<>();
            lehrerGrid.setHeight("180px");
            lehrerGrid.addColumn(name -> name).setHeader("Name");

            if (currentUser.getRole() == User.Role.ADMIN) {
                lehrerGrid.addColumn(new ComponentRenderer<>(name -> {
                    Button entfernen = new Button("Entfernen", ev -> {
                        veranstaltung.getZugewieseneLehrer().remove(name);
                        PersistenzService.speichernAlles();
                        lehrerGrid.setItems(veranstaltung.getZugewieseneLehrer());
                    });
                    return entfernen;
                })).setHeader("Aktion");
            }

            lehrerGrid.setItems(veranstaltung.getZugewieseneLehrer());
            add(lehrerTitel, lehrerGrid);

            if (currentUser.getRole() == User.Role.ADMIN) {
                MultiSelectComboBox<String> lehrerBox = new MultiSelectComboBox<>();
                lehrerBox.setLabel("Lehrer hinzufügen");

                Button hinzufuegen = new Button("Hinzufügen", ev -> {
                    veranstaltung.getZugewieseneLehrer().addAll(lehrerBox.getSelectedItems());
                    PersistenzService.speichernAlles();
                    lehrerBox.setItems(AuthService.getInstance()
                            .getAlleBenutzernamen(User.Role.TEACHER)
                            .stream().filter(name -> !veranstaltung.getZugewieseneLehrer().contains(name)).toList());
                    lehrerBox.clear();
                    lehrerGrid.setItems(veranstaltung.getZugewieseneLehrer());
                });

                lehrerBox.setItems(AuthService.getInstance()
                        .getAlleBenutzernamen(User.Role.TEACHER)
                        .stream().filter(name -> !veranstaltung.getZugewieseneLehrer().contains(name)).toList());

                HorizontalLayout lehrerControls = new HorizontalLayout(lehrerBox, hinzufuegen);
                lehrerControls.setAlignItems(Alignment.BASELINE);
                add(lehrerControls);
            }
        }

        teilnehmerGrid = new Grid<>();
        teilnehmerGrid.addColumn(name -> name).setHeader("Name");
        teilnehmerGrid.addColumn(name -> {
            LocalDateTime t = veranstaltung.getTeilnahmen().get(name);
            return (t != null) ? "✓ " + t.format(FORMATTER) : "⏳ offen";
        }).setHeader("Teilnahme");
        teilnehmerGrid.addColumn(new ComponentRenderer<>(name -> {
            LocalDateTime teilnahme = veranstaltung.getTeilnahmen().get(name);
            if (teilnahme != null) {
                return new Span("✓ Teilnahme");
            }
            Button entfernen = new Button("Entfernen", e -> {
                veranstaltung.getTeilnehmer().remove(name);
                veranstaltung.getTeilnahmen().remove(name);
                updateGrid();
                PersistenzService.speichernAlles();
            });
            return entfernen;
        })).setHeader("Aktion");

        add(new Span("Zugewiesene Studenten:"), teilnehmerGrid);
        updateGrid();

        Button zuweisenDialogButton = new Button("Studenten/Klassen zuweisen", e -> openZuweisDialog());
        add(zuweisenDialogButton);

        Button pdfExport = new Button("PDF exportieren", ev -> {
            try {
                byte[] pdf = PdfExportService.erzeugePdf(veranstaltung);
                StreamResource res = new StreamResource("teilnehmerliste.pdf", () -> new ByteArrayInputStream(pdf));
                Anchor download = new Anchor(res, "Herunterladen");
                download.getElement().setAttribute("download", true);
                exportStatus.setText("Letzter Export: " + LocalDateTime.now().format(FORMATTER));
                add(new HorizontalLayout(download, exportStatus));
            } catch (Exception ex) {
                Notification.show("Export fehlgeschlagen");
            }
        });
        add(pdfExport);
    }

    private void updateGrid() {
        teilnehmerGrid.setItems(veranstaltung.getTeilnehmer());
    }

    private void openZuweisDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Zuweisung von Klassen oder Studenten");

        MultiSelectComboBox<String> klassenBox = new MultiSelectComboBox<>("Klassen");
        klassenBox.setItems(KlassenService.getInstance().getAllKlassenNamen());

        MultiSelectComboBox<String> studentenBox = new MultiSelectComboBox<>("Studenten");
        Set<String> bereits = veranstaltung.getTeilnehmer();
        List<String> studenten = AuthService.getInstance()
                .getAlleBenutzernamen(User.Role.STUDENT)
                .stream().filter(s -> !bereits.contains(s)).toList();
        studentenBox.setItems(studenten);

        Button zuweisen = new Button("Zuweisen", ev -> {
            studentenBox.getSelectedItems().forEach(s
                    -> VeranstaltungsService.getInstance().teilnehmerZuweisen(veranstaltung.getId(), s));
            klassenBox.getSelectedItems().forEach(k -> {
                Set<String> schueler = KlassenService.getInstance().getSchuelerEinerKlasse(k);
                schueler.forEach(s
                        -> VeranstaltungsService.getInstance().teilnehmerZuweisen(veranstaltung.getId(), s));
            });

            PersistenzService.speichernAlles();
            dialog.close();
            updateGrid();
            Notification.show("Zuweisung erfolgreich");
        });

        dialog.add(klassenBox, studentenBox, new HorizontalLayout(zuweisen));
        dialog.open();
    }
}
