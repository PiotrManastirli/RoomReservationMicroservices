package md.manastirli.repository;

import md.manastirli.entity.Role;
import md.manastirli.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    boolean deleteByUsername(String username);

    void delete(User user);

    Optional<User> findUserByRole(Role role);
}
