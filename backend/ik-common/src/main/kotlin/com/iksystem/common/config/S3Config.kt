package com.iksystem.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config(@Value("\${aws.s3.region}") private val region: String) {
    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder().region(Region.of(region)).build()
    }
}