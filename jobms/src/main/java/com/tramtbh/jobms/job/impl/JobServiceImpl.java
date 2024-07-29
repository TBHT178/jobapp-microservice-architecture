/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tramtbh.jobms.job.impl;


import com.tramtbh.jobms.job.Job;
import com.tramtbh.jobms.job.JobRepository;
import com.tramtbh.jobms.job.JobService;
import com.tramtbh.jobms.job.dto.JobDTO;
import com.tramtbh.jobms.job.external.Company;
import com.tramtbh.jobms.job.external.Review;
import com.tramtbh.jobms.job.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author DELL
 */
@Service
public class JobServiceImpl implements JobService {
    @Autowired
    RestTemplate restTemplate;

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<JobDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        List<JobDTO> jobDTOS = new ArrayList<>();

        return jobs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private JobDTO convertToDto(Job job) {

//        RestTemplate restTemplate = new RestTemplate();
        Company company = restTemplate.getForObject("http://companyms:8082/companies/" + job.getCompanyId(), Company.class);

        // Exchange method is much more versatile than getForObject, useful when the response type is a generic collection
        ResponseEntity<List<Review>> reviewResponse = restTemplate.exchange("http://reviewms:8083/reviews?companyId=" + job.getCompanyId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Review>>() {
        });

        List<Review> reviews = reviewResponse.getBody();

        JobDTO jobDTO = JobMapper.mapToJobWithCompanyDTO(job, company, reviews);
//        jobDTO.setCompany(company);

        return jobDTO;
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        return convertToDto(job);
    }

    @Override
    public boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateJob(Long id, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if(jobOptional.isPresent()){
            Job job = jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setLocation(updatedJob.getLocation());
            jobRepository.save(job);
            return true;
        }
        return false;
    }

}
