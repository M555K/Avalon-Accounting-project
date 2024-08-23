package com.company.repository;

import com.company.entity.Company;
import com.company.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p where p.year = :year and p.company.id = :companyId and p.isDeleted = false  order by p.month")
    List<Payment> findAllByYearAndCompanyId(@Param("year") Integer year, @Param("companyId") Long companyId);
}
