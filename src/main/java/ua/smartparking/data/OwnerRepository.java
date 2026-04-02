package ua.smartparking.data;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.smartparking.domain.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}