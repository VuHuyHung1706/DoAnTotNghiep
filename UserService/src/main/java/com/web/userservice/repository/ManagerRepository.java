package com.web.userservice.repository;

import com.web.userservice.entity.Manager;
import com.web.userservice.enums.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {
    Optional<Manager> findByAccountUsername(String username);

    @Query("SELECT m FROM Manager m WHERE m.position IN :positions")
    Page<Manager> findAllByPositionIn(@Param("positions") java.util.List<Position> positions, Pageable pageable);

    @Query("SELECT m FROM Manager m WHERE m.position IN :positions AND " +
            "(LOWER(m.account.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Manager> searchStaffByPositions(@Param("keyword") String keyword,
                                         @Param("positions") java.util.List<Position> positions,
                                         Pageable pageable);

    boolean existsByEmail(String email);
}
