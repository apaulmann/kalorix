package de.paulmannit.kalorix;

import de.paulmannit.kalorix.model.UserEty;
import de.paulmannit.kalorix.service.UserService;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger LOGGER = Logger.getLogger(AppLifecycleBean.class);

    @Inject
    UserService userService;

    @ConfigProperty(name = "app.admin.username")
    String adminUsername;

    @ConfigProperty(name = "app.admin.password")
    String adminPassword;

    @ConfigProperty(name = "app.admin.email")
    String adminEmail;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Die Anwendung startet...");
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        try {
            UserEty userEty = new UserEty();
            userEty.password = BcryptUtil.bcryptHash(adminPassword);;
            userEty.username = adminUsername;
            userEty.role = "admin";
            userEty.email = adminEmail;
            userService.createInitialAdminIfNeeded(userEty);
            LOGGER.info("Admin-Benutzer wurde überprüft oder erstellt");
        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des Admin-Benutzers", e);
        }
    }
}