package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {

    List<BusinessHours> findByVendorProfileId(Long vendorProfileId);

    Optional<BusinessHours> findByVendorProfileIdAndDayOfWeek(Long vendorProfileId, BusinessHours.DayOfWeek dayOfWeek);

    List<BusinessHours> findByVendorProfileIdAndIsOpenTrue(Long vendorProfileId);

    @Query("SELECT bh FROM BusinessHours bh WHERE bh.vendorProfile.id = :vendorProfileId AND bh.isOpen = true AND bh.openTime <= :currentTime AND bh.closeTime >= :currentTime")
    List<BusinessHours> findCurrentlyOpenHours(@Param("vendorProfileId") Long vendorProfileId,
            @Param("currentTime") LocalTime currentTime);

    @Query("SELECT bh FROM BusinessHours bh WHERE bh.vendorProfile.id = :vendorProfileId AND bh.dayOfWeek = :dayOfWeek AND bh.isOpen = true")
    Optional<BusinessHours> findOpenHoursForDay(@Param("vendorProfileId") Long vendorProfileId,
            @Param("dayOfWeek") BusinessHours.DayOfWeek dayOfWeek);

    @Query("SELECT bh FROM BusinessHours bh WHERE bh.vendorProfile.id = :vendorProfileId AND bh.isOpen = true ORDER BY bh.dayOfWeek")
    List<BusinessHours> findOpenBusinessHours(@Param("vendorProfileId") Long vendorProfileId);

    @Query("SELECT bh FROM BusinessHours bh WHERE bh.vendorProfile.id = :vendorProfileId AND bh.openTime <= :time AND bh.closeTime >= :time AND bh.isOpen = true")
    List<BusinessHours> findVendorsOpenAtTime(@Param("vendorProfileId") Long vendorProfileId,
            @Param("time") LocalTime time);

    boolean existsByVendorProfileIdAndDayOfWeek(Long vendorProfileId, BusinessHours.DayOfWeek dayOfWeek);

    @Query("SELECT COUNT(bh) FROM BusinessHours bh WHERE bh.vendorProfile.id = :vendorProfileId AND bh.isOpen = true")
    long countOpenDaysForVendor(@Param("vendorProfileId") Long vendorProfileId);
}