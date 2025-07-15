package de.paulmannit.kalorix.repository;

import de.paulmannit.kalorix.model.UserKalTrackingEty;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class UserKalTrackingRepository implements PanacheRepository<UserKalTrackingEty> {

    public List<UserKalTrackingEty> findByUserName(String userName) {
        return find("user.username", userName).list();
    }

    public List<UserKalTrackingEty> findByUserNameAndToday(String username) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return find("user.username = ?1 and zeitstempel >= ?2 and zeitstempel < ?3", username, startOfDay, endOfDay).list();
    }
}
