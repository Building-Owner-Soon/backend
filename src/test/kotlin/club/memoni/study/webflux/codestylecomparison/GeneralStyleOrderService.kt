package club.memoni.study.webflux.codestylecomparison

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class GeneralStyleOrderService(
    private val userRepository: GeneralStyleUserRepository,
    private val orderRepository: GeneralStyleOrderRepository,
) {
    fun getTotalOrderPriceAmount(userId: String): Mono<Int> {
        /**
         * flatMap, map, reduce 등의 체이닝
         *
         * 결과는 최종적으로 Mono<Int>로 감싸짐
         *
         * 흐름이 명시적이고 reactive stream을 직접적으로 처리
         * */
        return userRepository.findById(userId) // Mono<User>
            .flatMap { user -> // 구독하고 있는 Mono객체에서 User가 emit되었을때 해당 람다 실행
                orderRepository.findByUserId(user.id) // Flux<Order>
                    .map { it.price } // Flux<Int>
                    .reduce(0) { acc, price -> acc + price } // Mono<Int>
            }
    }
}

class GeneralStyleUserRepository {
    fun findById(userId: String): Mono<User> {
        return Mono.just(User(userId, "John Doe"))
    }
}

class GeneralStyleOrderRepository {
    @Suppress("UnusedParameter")
    fun findByUserId(userId: String): Flux<Order> {
        // 실제 구현에서는 userId를 사용하여 DB 조회
        return Flux.just(
            Order(100),
            Order(200),
            Order(300),
        )
    }
}
