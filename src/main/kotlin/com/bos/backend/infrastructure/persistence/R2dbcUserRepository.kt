package com.bos.backend.infrastructure.persistence

import com.bos.backend.domain.user.entity.User
import com.bos.backend.domain.user.repository.UserRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

interface UserCoroutineRepository : CoroutineCrudRepository<User, Long>

@Repository
class R2dbcUserRepositoryImpl(
    private val coroutineRepository: UserCoroutineRepository,
) : UserRepository {
    override suspend fun save(user: User): User = coroutineRepository.save(user)

    override suspend fun findById(id: Long): User? = coroutineRepository.findById(id)

    override suspend fun deleteById(id: Long) {
        val user = findById(id) ?: return
        val deletedUser = user.delete()
        coroutineRepository.save(deletedUser)
    }
}
