package com.budgetplanner.BudgetPlanner.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 예시)
    // RECRUITMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "R001", "해당하는 채용공고가 없습니다."),

    //User
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "U001", "이미 계정이 존재합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "유저를 찾을 수 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 일치하지 않습니다."),

    //auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "잘못된 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),

    //budget
    CATEGORY_MISSING(HttpStatus.BAD_REQUEST, "B001", "카테고리에 대한 예산을 모두 입력해주세요."),
    BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND, "B002", "설정된 예산이 없습니다."),

    //expense
    EXPENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "해당 지출을 찾을 수 없습니다."),
    EXPENSE_USER_MISMATCH(HttpStatus.BAD_REQUEST, "E002", "기록한 유저와 찾을 유저가 일치하지 않습니다."),

    //statistics
    DATA_MIS_MATCH(HttpStatus.BAD_REQUEST, "S001", "해당 데이터는 없는 케이스입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

}