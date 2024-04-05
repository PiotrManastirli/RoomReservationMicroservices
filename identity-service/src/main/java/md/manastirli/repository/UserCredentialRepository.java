package md.manastirli.repository;

import md.manastirli.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredentials, Integer> {

    Optional<UserCredentials> findByName(String username);
}