package com.shms.repository;
import com.shms.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BedRepository extends JpaRepository<Bed, Long> { List<Bed> findByWardId(Long wardId); }
