package club.memoni.study.webflux.codestylecomparison

class CoroutineStyleOrderService(
    private val userRepository: CoroutineStyleUserRepository,
    private val orderRepository: CoroutineStyleOrderRepository
) {
    suspend fun getTotalOrderAmount(userId: String): Int {
        val user = userRepository.findById(userId)
        val orders = orderRepository.findByUserId(user.id)
        return orders.sumOf { it.price }
    }
}


class CoroutineStyleUserRepository {
    // suspend keyword가 redundant한 이유는 실제 DB호출이 아니기 때문
    suspend fun findById(userId: String): User {
        return User(userId, "John Doe")
    }
}

class CoroutineStyleOrderRepository {
    // suspend keyword가 redundant한 이유는 실제 DB호출이 아니기 때문
    suspend fun findByUserId(userId: String): List<Order> {
        return listOf(
            Order(100),
            Order(200),
            Order(300)
        )
    }
}
