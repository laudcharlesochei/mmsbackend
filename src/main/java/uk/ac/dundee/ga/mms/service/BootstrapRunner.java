package uk.ac.dundee.ga.mms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uk.ac.dundee.ga.mms.config.MmsProperties;
import uk.ac.dundee.ga.mms.domain.AppUser;
import uk.ac.dundee.ga.mms.domain.Role;
import uk.ac.dundee.ga.mms.storage.UserStore;

import java.time.Instant;
import java.util.EnumSet;

/** Creates the first Administrator from ADMIN_EMAIL / ADMIN_PASSWORD when the user table is empty. */
@Slf4j
@Component
@Order(1)
public class BootstrapRunner implements ApplicationRunner {

    private final UserStore users;
    private final PasswordEncoder encoder;
    private final MmsProperties props;

    public BootstrapRunner(UserStore users, PasswordEncoder encoder, MmsProperties props) {
        this.users = users;
        this.encoder = encoder;
        this.props = props;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (props.isMigrateOnly() || users.count() > 0) {
            return;
        }
        String email = props.getBootstrap().getAdminEmail();
        if (email == null || email.isBlank()) {
            log.warn("No users exist and ADMIN_EMAIL is not set - set ADMIN_EMAIL (and ADMIN_PASSWORD for local auth) to create the first Administrator.");
            return;
        }
        AppUser u = new AppUser();
        u.setEmail(email.trim().toLowerCase());
        u.setDisplayName(props.getBootstrap().getAdminName());
        u.setRoles(EnumSet.of(Role.ADMIN));
        u.setCreatedAt(Instant.now());
        String pwd = props.getBootstrap().getAdminPassword();
        if (pwd != null && !pwd.isBlank()) {
            u.setPasswordHash(encoder.encode(pwd));
        }
        users.save(u);
        log.info("Created bootstrap Administrator {}", u.getEmail());
    }
}
