package com.gar.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gar.system.model.GarAuthority;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface GarAuthorityRepository extends JpaRepository<GarAuthority, String> {
}
