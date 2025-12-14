package com.example.domains.transaction.service

import com.example.domains.transaction.model.DepositRequest
import com.example.domains.transaction.model.DepositResponse
import com.example.domains.transaction.model.TransferRequest
import com.example.domains.transaction.model.TransferResponse
import com.example.types.dto.Response
import com.example.types.dto.ResponseProvider
import org.springframework.stereotype.Service

@Service
class TransactionService {
    fun deposit(request: DepositRequest): Response<DepositResponse> {
        // 트랜잭션
        // 계좌를 조회하고, 유저 일치하는지 확인
        // 계좌에 돈을 넣는다.
        return ResponseProvider.success(
            DepositResponse(
                afterBalance = 0.toBigDecimal()
            )
        )
    }

    fun transfer(request: TransferRequest): Response<TransferResponse> {
        // 트랜잭션
        // 출금 계좌 유저 맞는지 확인
        // 입금 계좌 있는지 조회
        // 계좌 잔고 유효성 검증
        // fromAccount 출금
        // toAccount 입금
        return ResponseProvider.success(
            TransferResponse(
                afterFromBalance = 0.toBigDecimal()
            )
        )
    }
}