package com.gar.system.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gar.system.model.GarUser;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface GarUserRepository extends JpaRepository<GarUser, Long> {

    Optional<GarUser> findOneByActivationKey(String activationKey);

    List<GarUser> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);

    Optional<GarUser> findOneByResetKey(String resetKey);

    Optional<GarUser> findOneByEmail(String email);

    Optional<GarUser> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    GarUser findOneWithAuthoritiesById(Long id);

    @EntityGraph(attributePaths = "authorities")
    Optional<GarUser> findOneWithAuthoritiesByLogin(String login);

    Page<GarUser> findAllByLoginNot(Pageable pageable, String login);
}
