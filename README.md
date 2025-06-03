# AnwesenheitsApp - Gruppe 3 (Vibecoder)

Dieses Projekt wurde im Rahmen der Lehrveranstaltung Software Engineering II am Management Center Innsbruck (MCI) im Sommersemester 2025 entwickelt.
Ziel ist die Entwicklung einer Webanwendung zur digitalen Anwesenheitskontrolle bei Präsenzvorlesungen.

## Funktion & Zielgruppe

Die Anwendung ermöglicht es Studierenden, sich bei Lehrveranstaltungen mit einem Kennwort als anwesend zu registrieren.
Lehrende und Administratoren verwalten Veranstaltungen und Teilnehmer:innen.
Der Fokus liegt auf einer rollenbasierten Nutzerführung, einem einfachen UI und einer sicheren Datenhaltung.

## Voraussetzungen

- Java 21
- Maven
- IDE mit Vaadin-Unterstützung (z.B. IntelliJ IDEA)
- Internetverbindung (zum Laden der Vaadin-Abhängigkeiten)

## Starten des Projekts

1. Repository klonen: git clone https://github.com/mescheiber/vaadin-devops-gruppe3

2. Projekt in IntelliJ (oder anderer IDE) öffnen

3. Starte die Anwendung über die Datei: src/main/java/net/mci/seii/group3/Application.java

4. Öffne im Browser: http://localhost:8080


## Benutzerrollen

- `ADMIN`: Benutzer- und Veranstaltungsverwaltung
- `STUDENT`: Veranstaltungen verwalten
- `TEACHER`: Teilnahme über Kennwort

## Ordnerstruktur

- `src/main/java`: Java-Code (Views, Services, Models)
- `src/main/frontend`: Styles und UI-bezogene Dateien
- `src/main/resources`: Bilder und statische Inhalte

## Technologien

Spring Boot 3.4.3 – Applikations-Framework
Vaadin 24.7.1 – UI-Framework
PostgreSQL – optionale Datenbank
Spring Security – Zugriffsschutz
iText – PDF-Export von Anwesenheitslisten
Maven – Build Management

## Hinweise zur Nutzung

Es gibt keine automatische Benutzererstellung beim ersten Start – Benutzer müssen manuell über die Oberfläche erstellt werden.
Die Anwendung speichert standardmäßig im Arbeitsspeicher (kein persistenter Speicher, außer mit PostgreSQL-Konfiguration).
Der PDF-Export generiert Teilnehmerlisten pro Veranstaltung.

## Autoren

Andreas Wegscheider [Backend Developer & Database | SS 2025]
Ramona Kerschbaumer [Backend Developer | SS 2025]
Richard Kurz [Backend Developer | SS 2025]
Daniel Weilguny [DevOps | SS 2025]
Melanie Scheiber [Frontend Developer | SS 2025]


