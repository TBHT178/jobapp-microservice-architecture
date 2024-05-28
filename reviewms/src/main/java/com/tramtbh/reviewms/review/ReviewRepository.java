/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tramtbh.reviewms.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author DELL
 */
public interface ReviewRepository extends JpaRepository<Review, Long>{

    public List<Review> findByCompanyId(Long companyId);
    
}
