# Booking Platform

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen)
![MongoDB](https://img.shields.io/badge/MongoDB-latest-green)
![H2](https://img.shields.io/badge/H2-In--Memory-blue)
![Redisson](https://img.shields.io/badge/Redisson-3.35.0-red)

숙박 시설 예약 플랫폼 - Spring Boot 기반 REST API

## 목차

- [기술 스택](#기술-스택)
- [주요 기능](#주요-기능)
- [아키텍처 특징](#아키텍처-특징)
    - [시스템 아키텍처](#시스템-아키텍처)
    - [하이브리드 데이터베이스](#1-하이브리드-데이터베이스)
    - [AOP 기반 횡단 관심사](#2-aop-기반-횡단-관심사)
    - [헤더 기반 인증](#3-헤더-기반-인증)
    - [패키지 구조](#4-패키지-구조)
- [실행 방법](#실행-방법)
- [주요 비즈니스 로직](#주요-비즈니스-로직)
- [참고](#참고)

## 기술 스택

- **Java 17** / **Spring Boot 3.5.7**
- **MongoDB** - 숙박 시설 및 객실 정보 (NoSQL)
- **H2 Database** - 예약 정보 (RDB, 인메모리)
- **Redisson** - 분산 락
- **Spring AOP** - 횡단 관심사 처리

## 주요 기능

### API 엔드포인트

**숙박 시설 (Accommodation)**

- `POST /api/accommodations` - 등록
- `GET /api/accommodations` - 목록 조회 (커서 페이지네이션)
- `GET /api/accommodations/{id}` - 상세 조회
- `PUT /api/accommodations/{id}` - 수정 (소유자/관리자)
- `DELETE /api/accommodations/{id}` - 삭제 (소유자/관리자)

**객실 (Room)**

- `POST /api/accommodations/{id}/rooms` - 등록 (소유자/관리자)
- `GET /api/accommodations/{id}/rooms` - 목록 조회
- `GET /api/accommodations/{id}/rooms/available` - 예약 가능 객실 조회
- `PUT /api/rooms/{id}` - 수정 (소유자/관리자)
- `DELETE /api/rooms/{id}` - 삭제 (소유자/관리자)

**예약 (Reservation)**

- `POST /api/reservations` - 예약 생성 (분산 락)
- `GET /api/reservations/me` - 내 예약 목록
- `GET /api/reservations/{id}` - 상세 조회 (소유자/관리자)
- `DELETE /api/reservations/{id}` - 취소 (소유자/관리자)

## 아키텍처 특징

### 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                       Client                            │
└───────────────────┬─────────────────────────────────────┘
                    │ HTTP (X-User-Id, X-User-Role)
┌───────────────────▼─────────────────────────────────────┐
│                   Controller Layer                      │
│  AccommodationController | RoomController | ...         │
└───────────────────┬─────────────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────────────┐
│                 AOP Interceptors                        │
│  @DistributedLock (Redisson) | @RequireOwner            │
└───────────────────┬─────────────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────────────┐
│                   Service Layer                         │
│  AccommodationService | ReservationService | ...        │
└────────────┬──────────────────────┬─────────────────────┘
             │                      │
┌────────────▼─────────┐   ┌───────▼──────────────────────┐
│   MongoDB (NoSQL)    │   │     H2 (RDB)                 │
├──────────────────────┤   ├──────────────────────────────┤
│  - Accommodation     │   │  - Reservation               │
│  - Room              │   │  (트랜잭션 보장)                │
│  (빠른 조회)           │   │                              │
└──────────────────────┘   └──────────────────────────────┘
```

### 1. 하이브리드 데이터베이스

- **MongoDB**: 숙박 시설/객실 - 빠른 조회 성능
- **H2**: 예약 정보 - 트랜잭션 보장

### 2. AOP 기반 횡단 관심사

**분산 락**

```java

@DistributedLock(key = "#request.roomId()")
public ReservationResponse createReservation(UserContext user, ReservationCreateRequest request) {
    // Redisson을 이용한 동시 예약 방지
}
```

**소유권 검증**

```java

@RequireOwner(resourceType = ResourceType.ACCOMMODATION)
public void updateAccommodation(Long id, AccommodationUpdateRequest request) {
    // ADMIN 또는 소유자만 접근 가능
}
```

### 3. 헤더 기반 인증

```java
public List<ReservationDetailResponse> getMyReservations(@CurrentUser UserContext user) {
    // X-User-Id, X-User-Role 헤더로 인증
}
```

### 4. 패키지 구조

```
io.yugurt.booking_platform
├── aop                    # AOP (분산 락, 소유권 검증)
├── config                 # 설정
├── controller             # REST API
├── domain                 # 도메인 모델 (nosql, rdb, enums)
├── dto                    # 요청/응답 DTO
├── exception              # 예외 처리
├── repository             # 저장소 (nosql, rdb)
├── security               # 인증/인가
├── service                # 비즈니스 로직
└── util                   # 유틸리티
```

## 실행 방법

### 사전 요구사항

- Java 17
- MongoDB (`localhost:27017`)

### 실행

```bash
./gradlew bootRun
```

## 주요 비즈니스 로직

### 예약 생성 프로세스

1. 날짜 유효성 검증 (과거 날짜, 체크아웃 > 체크인)
2. 분산 락 획득 (roomId 기준)
3. 예약 중복 검증
4. 트랜잭션 커밋 후 락 해제

### 예약 취소 규칙

- 체크인 전날까지 취소 가능
- 체크인 당일 이후 취소 불가

### 커서 기반 페이지네이션

- `_id` 기준 정렬
- `cursor`와 `size`로 다음 페이지 조회
- `hasNext` 플래그 제공

## 참고

- [Kurly - Redisson 분산 락](https://helloworld.kurly.com/blog/distributed-redisson-lock/)
