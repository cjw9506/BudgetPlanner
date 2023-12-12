# BudgetPlanner - 예산 관리 어플리케이션

<br>

<div align="center">
<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot 3.1.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Data JPA-gray?style=for-the-badge&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Junit-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"/></a>
</div>
<div align="center">
<img src="https://img.shields.io/badge/MySQL 8-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white"/></a>
<img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"/></a>
<img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens"/></a>
<img src="https://img.shields.io/badge/swagger-%ffffff.svg?style=for-the-badge&logo=swagger&logoColor=white"/></a>
</div>

<br>

BudgetPlanner는 사용자의 예산과 지출 관리에 대한 추천 및 관리를 도와주는 어플리케이션입니다. 사용자의 예산 및 지출을 파악하여 오늘 지출에 대한 안내와 추천을 도와주고, 
사용자의 지출을 파악하여 지난 달, 지난 주, 다른 사용자와 비교 등 각종 통계를 보여줍니다. 
<br>
<br>

## 0. 목차
- [1.개발 기간](#1-개발-기간)
- [2.프로젝트 요구사항](#2-프로젝트-요구사항)
- [3.프로젝트 구조](#3-프로젝트-구조)
- [4.ERD](#4-erd)
- [5.동작예시](#5-동작예시)
- [6.API 문서](#6-api-문서)

## 1. 개발 기간

2023.11.09 ~ 2023.11.22 (14 days)

## 2. 프로젝트 요구사항

- 사용자 (유저) 모듈
  - 회원가입 및 로그인 기능 구현.
  - JWT를 이용한 사용자 인증.

- 예산 설정 및 설계 (Budget) 모듈
  - 카테고리 설정 기능 구현.
  - 모든 카테고리 목록 조회 기능 구현.
  - 예산 설정 및 수정 기능 구현.
  - 예산 설계(추천) 기능 구현.

- 지출 기록 (Expense) 모듈
  - 지출 기록 생성, 수정, 삭제 기능 구현.
  - 지출 목록 조회 및 필터 기능 구현.
  - 지출 합계와 카테고리별 지출 합계 반환 기능 구현.
    
- 지출 컨설팅 (Consulting) 모듈
  - 오늘 지출 추천 및 안내 기능 구현.
  - 사용자 상황에 맞는 멘트 제공.
  - 알림 기능 Discord Webhook 구현.

- 지출 통계 (Statistics) 모듈
  - Dummy 데이터 생성 기능 구현.
  - 지출 통계 조회 기능 구현.
    - 지난 달 대비 총액 및 카테고리 별 소비율 계산.
    - 지난 요일 대비 소비율 계산.
    - 다른 유저 대비 소비율 계산.

## 3. 프로젝트 구조

<details>
    <summary>자세히</summary>

```
└── BudgetPlanner
    ├── BudgetPlannerApplication.java
    ├── auth
    │   ├── config
    │   ├── controller
    │   ├── dto
    │   ├── filter
    │   ├── jwt
    │   └── service
    ├── budget
    │   ├── controller
    │   ├── dto
    │   ├── entity
    │   ├── repository
    │   └── service
    ├── common
    │   └── exception
    ├── expense
    │   ├── controller
    │   ├── dto
    │   ├── entity
    │   ├── repository
    │   └── service
    ├── expenseadvisor
    │   ├── controller
    │   ├── dto
    │   └── service
    ├── notification
    │   ├── scheduler
    │   └── service
    ├── statistics
    │   ├── controller
    │   ├── dto
    │   └── service
    └── user
        ├── entity
        └── repository

```

</details>

## 4. ERD

  <img width="662" alt="스크린샷 2023-11-24 오후 5 43 24" src="https://github.com/cjw9506/BudgetPlanner/assets/63503519/9f699418-b02f-4f37-94a1-b1105499229c">

## 5. 동작예시

  <img width="407" alt="스크린샷 2023-11-22 오전 1 30 57" src="https://github.com/cjw9506/BudgetPlanner/assets/63503519/efe0c1b0-f60c-4272-b7f8-2881c0febc13">
  

## 6. API 문서

URL : `http://server:port/swagger-ui/index.html`

<img width="800" alt="스크린샷 2023-11-26 오후 9 33 32" src="https://github.com/cjw9506/BudgetPlanner/assets/63503519/28793e15-ce25-45a6-be06-3b6140f90b25">

