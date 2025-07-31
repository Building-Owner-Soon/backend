package com.bos.backend.infrastructure.persistence

import com.bos.backend.domain.user.entity.User
import com.bos.backend.domain.user.repository.UserRepository
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

interface UserCoroutineRepository : CoroutineCrudRepository<User, Long> {
    @Modifying
    @Query("UPDATE users SET deleted_at = :deletedAt, updated_at = :updatedAt WHERE id = :userId")
    suspend fun deleteUser(
        @Param("userId") userId: Long,
        @Param("deletedAt") deletedAt: Instant,
        @Param("updatedAt") updatedAt: Instant,
    ): Int
}

@Repository
class R2dbcUserRepositoryImpl(
    private val coroutineRepository: UserCoroutineRepository,
) : UserRepository {
    override suspend fun save(user: User): User = coroutineRepository.save(user)

    override suspend fun findById(id: Long): User? = coroutineRepository.findById(id)

    override suspend fun deleteById(id: Long) {
        val user = findById(id) ?: return
        val deletedUser = user.delete()
        coroutineRepository.deleteUser(id, deletedUser.deletedAt!!, deletedUser.updatedAt)
    }
}
