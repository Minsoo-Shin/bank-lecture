package com.example.domains.transaction.service

import com.example.common.cache.RedisClient
import com.example.common.cache.RedisKeyProvider
import com.example.common.exception.CustomException
import com.example.common.exception.ErrorCode
import com.example.common.logging.Logging
import com.example.common.transaction.Transactional
import com.example.domains.transaction.model.DepositRequest
import com.example.domains.transaction.model.DepositResponse
import com.example.domains.transaction.model.TransferRequest
import com.example.domains.transaction.model.TransferResponse
import com.example.domains.transaction.repository.TransactionAccount
import com.example.domains.transaction.repository.TransactionUser
import com.example.types.dto.Response
import com.example.types.dto.ResponseProvider
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TransactionService(
    private val transactionUser: TransactionUser,
    private val transactionAccount: TransactionAccount,
    private val transactional: Transactional,
    private val logger: Logger = Logging.getLogger(TransactionService::class.java),
    private val redisClient: RedisClient,
) {
    fun deposit(request: DepositRequest): Response<DepositResponse> = Logging.logFor(logger) { log ->
        log["userUlid"] = request.userUlid
        log["accountUlid"] = request.accountUlid
        log["value"] = request.value

        val redisKey = RedisKeyProvider.bankMutexKey(accountUlid = request.accountUlid)
        return@logFor redisClient.invokeWithMutex(redisKey) {
            return@invokeWithMutex transactional.run {
                // 계좌를 조회하고, 유저 일치하는지 확인
                val account = (transactionAccount.findByUlidAndIsDeletedFalse(request.accountUlid)
                    ?: throw CustomException(ErrorCode.FAILED_TO_FIND_DATA, "accountUlid:${request.accountUlid}"))

                if (account.user.ulid != request.userUlid) throw CustomException(
                    ErrorCode.MISMATCH_ACCOUNT_ULID_AND_USER_ULID,
                    "accountUlid:${request.accountUlid}, userUlid:${request.userUlid}"
                )
                // 계좌에 돈을 넣는다.
                account.apply {
                    balance = balance.add(request.value)
                    updatedAt = LocalDateTime.now()
                }

                transactionAccount.save(account)

                return@run ResponseProvider.success(DepositResponse(afterBalance = account.balance))
            }
        }
    }

    fun transfer(request: TransferRequest): Response<TransferResponse> = Logging.logFor(logger) {
        // 트랜잭션
        // 출금 계좌 유저 맞는지 확인
        // 입금 계좌 있는지 조회
        // 계좌 잔고 유효성 검증
        // fromAccount 출금
        // toAccount 입금
        val redisKey = RedisKeyProvider.bankMutexKey(accountUlid = request.fromAccountUlid)
        return@logFor redisClient.invokeWithMutex(redisKey) {
            return@invokeWithMutex transactional.run {
                ResponseProvider.success(
                    TransferResponse(
                        afterFromBalance = 0.toBigDecimal()
                    )
                )
            }
        }

    }
}