package net.mci.seii.group3;

import com.vaadin.flow.component.page.AppShellConfigurator;
import net.mci.seii.group3.service.PersistenzService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        // 🔽 Automatisches Laden VOR dem Start
        PersistenzService.laden();

        SpringApplication.run(Application.class, args);
    }
}
