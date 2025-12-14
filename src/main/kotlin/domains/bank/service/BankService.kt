package com.example.domains.bank.service

import com.example.common.exception.CustomException
import com.example.common.exception.ErrorCode
import com.example.common.logging.Logging
import com.example.common.transaction.Transactional
import com.example.domains.bank.repository.BankAccountRepository
import com.example.domains.bank.repository.BankUserRepository
import com.example.types.dto.Response
import com.example.types.dto.ResponseProvider
import com.example.types.entity.Account
import com.github.f4b6a3.ulid.UlidCreator
import org.slf4j.Logger
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

@Service
class BankService(
    private val bankUserRepository: BankUserRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val transaction: Transactional,
    private val logger: Logger = Logging.getLogger(BankService::class.java)
) {

    fun createAccount(userUlid: String): Response<String> = Logging.logFor(logger) { log ->
        log["userUlid"] = userUlid

        transaction.run {
            val user = bankUserRepository.findByUlid(userUlid) ?: throw CustomException(
                ErrorCode.FAILED_TO_FIND_DATA,
                "userUlid:$userUlid"
            )

            val ulid = UlidCreator.getUlid().toString()
            val accountNumber = generateRandomAccountNumber()

            val account = Account(
                ulid = ulid,
                user = user,
                accountNumber = accountNumber,
            )

            try {
                bankAccountRepository.save(account)
            } catch (e: DataAccessException) {
                logger.error("Failed to save account for userUlid: $userUlid", e)
                throw CustomException(ErrorCode.FAILED_TO_SAVE_DATA)
            }
        }

        return@logFor ResponseProvider.success("success")
    }

    fun balance(userUlid: String, accountUlid: String): Response<BigDecimal> = Logging.logFor(logger) { log ->
        log["userUlid"] = userUlid
        log["accountUlid"] = accountUlid

        return@logFor transaction.run {
            val account = bankAccountRepository.findByUlid(accountUlid) ?: throw CustomException(
                ErrorCode.FAILED_TO_FIND_DATA,
                "accountUlid:$accountUlid"
            )
            if (account.user.ulid != userUlid) throw CustomException(ErrorCode.MISMATCH_ACCOUNT_ULID_AND_USER_ULID)
            ResponseProvider.success(account.balance)
        }

    }

    fun removeAccount(userUlid: String, accountUlid: String): Response<String> = Logging.logFor(logger) { log ->
        log["userUlid"] = userUlid
        log["accountUlid"] = accountUlid

        transaction.run {
            val account = bankAccountRepository.findByUlid(accountUlid) ?: throw CustomException(
                ErrorCode.FAILED_TO_FIND_DATA,
                accountUlid
            )

            if (account.user.ulid != userUlid) throw CustomException(ErrorCode.MISMATCH_ACCOUNT_ULID_AND_USER_ULID)
            if (account.balance.compareTo(BigDecimal.ZERO) != 0) throw CustomException(ErrorCode.ACCOUNT_BALANCE_IS_NOT_ZERO)

            val updatedAccount = account.copy(
                isDeleted = true,
                deletedAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            bankAccountRepository.save(updatedAccount)
        }

        return@logFor ResponseProvider.success("success")
    }

    private fun generateRandomAccountNumber(): String {
        val bankCode = "003"
        val section = "12"

        val number = ThreadLocalRandom.current().nextInt(100_000, 1_000_000).toString()
        return "$bankCode-$section-$number"
    }
}