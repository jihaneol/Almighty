package com.example.A201.history.repository;

import com.example.A201.history.domain.StatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory,Long> {
    @Query("select s from StatusHistory s join fetch s.battery b where s.battery.id=:batteryId order by s.date desc")
    List<StatusHistory> findByBatteryId(Long batteryId);

    @Query(value = "select s from StatusHistory s join fetch s.battery b join b.member m where m.memberId=:memberId order by s.id desc"
            , countQuery = "select count(s) from StatusHistory s join s.battery b join b.member m where m.memberId=:memberId")
    Page<StatusHistory> findAllByMember(@Param("memberId") Long memberId, Pageable pageable);

    @Query(value =  "select s from StatusHistory s join fetch s.battery b order by s.date desc"
            , countQuery = "select count(s) from StatusHistory s")
    Page<StatusHistory> findAll(Pageable pageable);

    StatusHistory save(StatusHistory statusHistory);
}
