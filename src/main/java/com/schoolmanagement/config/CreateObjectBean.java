package com.schoolmanagement.config;

import com.schoolmanagement.payload.dto.DeanDto;
import com.schoolmanagement.payload.dto.ViceDeanDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration//Bu configuration class i oldugunu belirtiyorum
public class CreateObjectBean {

    @Bean
    public DeanDto deanDTO(){
        return new DeanDto();
    }

    @Bean
    public ViceDeanDto viceDeanDto(){
        return  new ViceDeanDto();
    }


}