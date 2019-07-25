package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Hello;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Hello entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HelloRepository extends JpaRepository<Hello, Long> {

}
