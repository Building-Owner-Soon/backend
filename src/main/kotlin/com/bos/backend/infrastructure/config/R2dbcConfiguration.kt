package com.bos.backend.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.bos.backend.infrastructure.persistence"])
class R2dbcConfiguration
