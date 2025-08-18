package com.bos.backend.presentation.profile.controller

import com.bos.backend.application.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/profile/assets")
class ProfileCacheController(
    private val profileService: ProfileService,
) {
    @DeleteMapping("/cache")
    suspend fun evictCache(): ResponseEntity<Map<String, String>> {
        profileService.evictCache()
        return ResponseEntity.ok(mapOf("message" to "Profile assets cache evicted successfully"))
    }

    @PostMapping("/cache/warm-up")
    suspend fun warmUpCache(): ResponseEntity<Map<String, String>> {
        profileService.getAssets()
        return ResponseEntity.ok(mapOf("message" to "Profile assets cache warmed up successfully"))
    }

    @GetMapping("/cache/status")
    suspend fun getCacheStatus(): ResponseEntity<Map<String, Any>> {
        val status = profileService.getCacheStatus()
        return ResponseEntity.ok(status)
    }
}
