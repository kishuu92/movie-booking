package com.xyz.movie_booking.strategy.discount;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class DiscountConfig {

    @Bean
    @Primary
    public DiscountHandler discountChain(BulkDiscountHandler bulk,
                                         NoDiscountHandler none) {
        bulk.setNext(none);
        return bulk;
    }
}