package com.bos.backend.application.transaction

import com.bos.backend.application.CommonErrorCode
import com.bos.backend.application.CustomException
import com.bos.backend.domain.transaction.entity.RepaymentSchedule
import com.bos.backend.domain.transaction.enum.RepaymentStatus
import com.bos.backend.domain.transaction.repository.RepaymentScheduleRepository
import com.bos.backend.domain.transaction.repository.TransactionRepository
import com.bos.backend.presentation.transaction.dto.CreateRepaymentRequestDTO
import com.bos.backend.presentation.transaction.dto.RepaymentManagementResponseDTO
import com.bos.backend.presentation.transaction.dto.RepaymentScheduleItemDTO
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RepaymentService(
    private val repaymentScheduleRepository: RepaymentScheduleRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend fun getRepaymentManagement(
        userId: Long,
        transactionId: Long,
    ): RepaymentManagementResponseDTO {
        val transaction =
            transactionRepository.findById(transactionId)
                ?: throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)

        if (transaction.userId != userId) {
            throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)
        }

        val repaymentSchedules = repaymentScheduleRepository.findByTransactionId(transactionId)
        val today = LocalDate.now()
        val repaymentItems = generateRepaymentItems(repaymentSchedules, today)

        val overdueRepayments =
            repaymentItems
                .filter { it.status == RepaymentStatus.OVERDUE }
                .sortedByDescending { it.displayDate }

        val regularRepayments =
            repaymentItems
                .filter { it.status != RepaymentStatus.OVERDUE }
                .sortedByDescending { it.displayDate }

        return RepaymentManagementResponseDTO(
            overdueRepayments = overdueRepayments,
            regularRepayments = regularRepayments,
        )
    }

    private fun generateRepaymentItems(
        repaymentSchedules: List<RepaymentSchedule>,
        today: LocalDate,
    ): List<RepaymentScheduleItemDTO> {
        // RepaymentSchedule 기반으로 직접 아이템 생성
        return repaymentSchedules.map { schedule ->
            val status =
                when (schedule.status) {
                    RepaymentStatus.COMPLETED -> RepaymentStatus.COMPLETED
                    else -> getScheduledStatus(schedule.scheduledDate, today)
                }

            RepaymentScheduleItemDTO(
                status = status,
                displayDate = schedule.actualDate ?: schedule.scheduledDate,
                displayAmount = schedule.actualAmount ?: schedule.scheduledAmount,
            )
        }.sortedBy { it.displayDate }
    }

    private fun getScheduledStatus(
        scheduledDate: LocalDate,
        today: LocalDate,
    ): RepaymentStatus =
        when {
            scheduledDate.isBefore(today) -> RepaymentStatus.OVERDUE
            scheduledDate >= today.minusDays(2) && scheduledDate <= today ->
                RepaymentStatus.IN_PROGRESS
            else -> RepaymentStatus.SCHEDULED
        }

    suspend fun addRepayment(
        userId: Long,
        scheduleId: Long,
        createRepaymentRequestDTO: CreateRepaymentRequestDTO,
    ): RepaymentScheduleItemDTO {
        val schedule =
            repaymentScheduleRepository.findById(scheduleId)
                ?: throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)

        val transaction =
            transactionRepository.findById(schedule.transactionId)
                ?: throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)

        validateUserAccess(transaction.userId, userId)

        val updatedSchedule =
            schedule.copy(
                status = RepaymentStatus.COMPLETED,
                actualDate = createRepaymentRequestDTO.repaymentDate,
                actualAmount = createRepaymentRequestDTO.repaymentAmount,
                updatedAt = java.time.Instant.now(),
            )

        val savedSchedule = repaymentScheduleRepository.save(updatedSchedule)

        return RepaymentScheduleItemDTO(
            status = RepaymentStatus.COMPLETED,
            displayDate = savedSchedule.actualDate!!,
            displayAmount = savedSchedule.actualAmount!!,
        )
    }

    private fun validateUserAccess(
        transactionUserId: Long,
        requestUserId: Long,
    ) {
        if (transactionUserId != requestUserId) {
            throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)
        }
    }
}
