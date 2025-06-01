package club.memoni.study.webflux.codestylecomparison

import io.kotest.core.spec.style.StringSpec

data class User(val id: String, val name: String)
data class Order(val price: Int)

class ReactiveCodeStyleComparison : StringSpec({
    "General Reactive Code Style" {
        val userRepository = GeneralStyleUserRepository()
        val orderRepository = GeneralStyleOrderRepository()
        val service = GeneralStyleOrderService(userRepository, orderRepository)

        service.getTotalOrderPriceAmount("user1")
            // 명시적 subscribe 호출을 통한 lazy evaluation
            .subscribe { total ->
                println("Total Order Price: $total") // Expected output: Total Order Price: 600
            }
    }

    "Kotlin coroutine Reactive Code Style" {
        val userRepository = CoroutineStyleUserRepository()
        val orderRepository = CoroutineStyleOrderRepository()
        val service = CoroutineStyleOrderService(userRepository, orderRepository)

        // 코루틴을 사용하여 suspend 함수 호출
        kotlinx.coroutines.runBlocking {
            val total = service.getTotalOrderAmount("user1")
            println("Total Order Amount: $total")
        }
    }
})
