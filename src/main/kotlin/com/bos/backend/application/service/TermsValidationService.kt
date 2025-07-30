package com.bos.backend.application.service

import com.bos.backend.domain.term.repository.TermRepository
import com.bos.backend.presentation.auth.dto.TermAgreementItemDTO
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class TermsValidationService(
    private val termRepository: TermRepository,
) {
    suspend fun validateTermsAgreements(termsAgreements: List<TermAgreementItemDTO>) {
        require(termsAgreements.isNotEmpty()) { "Terms agreements cannot be empty" }

        val termIds = termsAgreements.map { it.termId }
        val existingTerms = termRepository.findAllByIds(termIds).toList()

        // Check if all provided term IDs exist
        val existingTermIds = existingTerms.map { it.id!! }.toSet()
        val providedTermIds = termIds.toSet()
        val missingTermIds = providedTermIds - existingTermIds
        require(missingTermIds.isEmpty()) { "Terms not found: $missingTermIds" }

        // Check if all required terms are agreed to
        val requiredTerms = existingTerms.filter { it.isRequired }
        val agreedTermIds = termsAgreements.filter { it.isAgree }.map { it.termId }.toSet()

        val unagreedRequiredTerms = requiredTerms.filter { it.id!! !in agreedTermIds }
        if (unagreedRequiredTerms.isNotEmpty()) {
            val unagreedTermTitles = unagreedRequiredTerms.map { it.title }
            error("Required terms must be agreed to: $unagreedTermTitles")
        }
    }
}
