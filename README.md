# 🗂️ 회의실 예약 API

본 프로젝트는 사내 회의실 예약을 위한 RESTful API 서버를 구현한 과제용 백엔드 애플리케이션입니다.  
예약 중복 방지, 유효성 검증, 동시성 테스트 및 클라우드 아키텍처 설계를 포함합니다.

---

## 🚀 기술 스택 및 버전

| 항목            | 내용                          |
|---------------|-----------------------------|
| Language      | Java 17                     |
| Framework     | Spring Boot 3.4.5           |
| Build Tool    | Gradle - Groovy             |
| Database      | PostgreSQL 17 (Docker 기반)   |
| ORM           | Spring Data JPA             |
| Validation    | Jakarta Bean Validation     |
| Documentation | Swagger (springdoc-openapi) |
| Test          | JUnit 5                     |
| Container     | Docker, Docker Compose      |

---

## 🐳 실행 방법 (Docker 기반)

```bash
# 프로젝트 루트에서 실행
docker-compose up --build
```

- `8080` 포트: Spring Boot 애플리케이션
- `5432` 포트: PostgreSQL
- DB 초기 데이터는 테스트 코드 또는 `data.sql` 기반 설정 가능

---

## 📘 Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## ✅ 테스트 실행 방법

```bash
./gradlew test
```

**테스트 포함 내용:**

- ✅ 예약 등록 성공 / 중복 실패 검증
- ✅ 시작 < 종료 시간 검증
- ✅ 00/30분 단위 시간 검증
- ✅ 동시성 테스트 (멀티스레드 예약 충돌 시 하나만 성공 확인)

---

## ☁️ 클라우드 아키텍처 설계

**작성 중...**

---

## ⚠️ 교착상태 고려 사항

### 📌 발생 가능 시나리오

- 2명 이상의 사용자가 동시에 같은 회의실에, 겹치는 시간대에 예약을 시도할 경우
- 여러 회의실을 동시에 예약할 때 락 순서가 꼬이면 교착 상태 가능

### 🛠 대응 방안

- 회의실 단위로 `PESSIMISTIC_WRITE` 비관적 락을 사용하여 중복 방지
- 트랜잭션 안에서 락을 획득하고 예약을 등록
- 락 획득 순서를 일관되게 유지
- 필요 시 `LockTimeout`, 재시도 로직 등 적용 고려
- 실제 동시성 테스트를 통해 다중 접근 상황에서 하나만 성공함을 검증

---

## 🧩 설계 및 구현 시 가정

- 사용자(User), 회의실(MeetingRoom)은 API가 아닌 사전 등록된 데이터
- 예약 시간은 반드시 **30분 단위**이며, 시작 < 종료 시간 조건 필수
- 동일 회의실 내 **시간이 겹치는 예약은 불가능**
- 예약 등록 시 **비관적 락**을 사용하여 동시성 문제 방지
- Swagger를 통한 API 명세 및 테스트 제공

---

## 🙋‍♂️ 개발자

| 이름     | 연락처                                                  |
|--------|------------------------------------------------------|
| 양현준    | windofwish@gmail.com                                 |
| GitHub | [github.com/yhjun0523](https://github.com/yhjun0523) |
