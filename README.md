# 🏅 VisiLog

> 방문자의 행동을 추적하고,  
> 여러분의 비즈니스를 더 바쁘게(Busy) 만들어주는 Behavior Trigger 서비스 (MVP)

---

## 🧠 프로젝트 개요

**VisiLog**는 웹사이트 방문자의 행동(페이지뷰, 클릭, 체류 시간 등)을 수집하여,  
설정된 조건에 따라 자동으로 이메일을 발송하는 **SDK 기반 마케팅 자동화 도구**입니다.

---

## 📌 핵심 기능

| 기능 | 설명 | 상태 |
|------|------|------|
| 방문자 행동 수집 | SDK로 방문자의 행동(page_view 등) 수집 | ✅ 완료 |
| 조건 설정 | 특정 이벤트 조건 등록 (ex. 특정 URL 3회 방문) | ✅ 완료 |
| 세그먼트 구성 | 조건에 해당하는 방문자 그룹 생성 | ✅ 완료 |
| 자동 이메일 발송 | 조건 만족 세그먼트에 이메일 발송 | ✅ 완료 |
| 관리자 API 대시보드 | 프로젝트, 조건, 로그 등 관리 API 제공 | ✅ 완료 |
| SDK 배포 | SDK 스크립트 vercel로 정적 배포 | ✅ 완료 |

---

## 🚀 기술 스택

- **백엔드**: Java 17, Spring Boot 3.4.4, JPA 3.4.4, PostgreSQL 16
- **배포**: Render (서버), Vercel (SDK)
- **문서화**: Swagger (springdoc-openapi)
- **기타**: GitHub Actions (CI), Docker (Render용)

---

## 🌐 배포 및 접근

- **SDK 배포 URL (Vercel)**:  
  👉 [SDK 배포 링크](https://behavior-tracking-sdk.vercel.app/sdk.js)
  👉 [SDK GitHub] (https://github.com/SungHuii/behavior-tracking-sdk)


- **Swagger API 문서**:  
  👉 [Swagger 문서](https://sdk-behavior-trigger-mvp.onrender.com/swagger-ui/index.html)

---

## 🗂️ 프로젝트 구조

```
src
├── main
│ ├── java/com.behavior.sdk.trigger
│ │ ├── common # 공통 기능 (Interceptor, Security)
│ │ │ ├── interceptor # 요청 인터셉터
│ │ │ └── security # JWT 인증/인가 처리
│ │ ├── config # Web/Security 설정 클래스
│ │ ├── condition # 조건 등록/조회/삭제 (Condition 도메인)
│ │ ├── email # 이메일 발송 API
│ │ ├── email_log # 이메일 발송 로그 처리
│ │ ├── email_template # 이메일 템플릿 관리
│ │ ├── log_event # 방문자 행동 로그 기록
│ │ ├── project # 프로젝트 생성/수정/삭제
│ │ ├── segment # 조건 기반 방문자 그룹 관리
│ │ ├── user # 회원가입, 로그인 등 인증 관련
│ │ ├── visitor # 방문자 생성 및 정보 저장
│ │ ├── web # WebMvc 관련 설정 등
│ │ └── TriggerApplication # 메인 실행 클래스
│ └── resources
│ ├── static
│ ├── application.yml
│ ├── application-dev.yml
│ └── application-local.yml
├── test # 통합 테스트
```

---

## 📑 API 명세 요약

- `POST /api/visitors` : 방문자 생성
- `POST /api/conditions` : 조건 생성
- `POST /api/segments` : 세그먼트 생성
- `POST /api/segments/{id}/send-email` : 이메일 자동 발송
- 전체 명세: **[Swagger 문서](https://sdk-behavior-trigger-mvp.onrender.com/swagger-ui/index.html)**

---

## 🧩 ERD (Database Schema)

> 최신 ERD 기반 설계, SaaS 구조 확장 고려됨

[✔ ERD 보기](https://dbdiagram.io/d/sdk-behavior-trigger-mvp-67fb965b4f7afba184664689)

- 총 12개 테이블(users, projects, visitors, logs, conditions, segments, email_template, email_batch, email_log 등)
- trigger 기반 설계 구조

---

## 🔄 개발 프로세스

**VisiLog 프로젝트는 유저 스토리 기반의 기능 단위 개발 방식**으로 진행되었습니다.  
기획 → 설계 → 구현 → 테스트까지의 전체 흐름을 단계적으로 관리했습니다.

### 📌 개발 단계

1. **에픽 정의**
    - 주요 기능 영역을 에픽 단위로 나눔 (예: 방문자 추적, 조건 설정, 이메일 발송 등)

2. **유저 스토리 도출**
    - 각 에픽을 사용자의 입장에서 유저 스토리로 세분화
    - 예: "방문자로서 내가 사이트를 방문하면 행동 로그가 수집된다"

3. **유저 시나리오 작성**
    - 시나리오 기반 흐름 설계 (조건 만족 → 세그먼트 생성 → 이메일 발송 등)

4. **ERD 설계**
    - 유저 시나리오에 필요한 데이터 구조 설계
    - dbdiagram.io를 활용한 테이블 정의 및 관계 매핑

5. **시퀀스 다이어그램 설계**
    - 유저 액션에 따른 백엔드 흐름을 시각화
    - mermaid.js 기반으로 API 흐름 설계

6. **API 명세 작성**
    - Swagger 기반 REST API 문서화
    - 요청/응답 스펙, 상태 코드, 예외 케이스 정의

7. **기능 구현**
   - 실제 구현은 다음과 같은 순서로 진행:
    1) **Entity**: 먼저 도메인 모델 정의 및 관계 설정
    2) **Repository**: 엔티티 기반 CRUD 인터페이스 구성
    3) **DTO**: 요청/응답 데이터를 분리하여 정의
    4) **Service**: 핵심 비즈니스 로직 구현
    5) **Controller**: REST API 엔드포인트 구성 및 매핑
    - 계층적 구조를 기반으로 각 단계를 테스트하며 개발 진행

8. **통합 테스트 및 CI 구성**
    - Spring MockMvc를 활용한 통합 테스트
    - GitHub Actions로 자동 테스트 및 빌드 파이프라인 구성

---

### 🔀 Git 전략

- **기능별 브랜치**: `feat/*`
- **통합 브랜치**: `dev`
- **배포용 메인 브랜치**: `main`
- PR 머지 전 테스트 → `dev` 통합 → `main` 릴리즈 방식 유지



## 🧪 테스트 전략

- SecurityContext 설정 오류 해결 경험 보유
- MockMvc를 활용한 통합 테스트

---

## 🙌 향후 계획

- Admin 대시보드 UI (React 또는 Next.js)
- 방문자 통계 시각화 (차트 라이브러리 연동)
- Redis 기반 조건 캐싱 최적화
- MQ 기반 이메일 발송 비동화 (RabbitMQ or BullMQ)
- OAuth2 인증 연동 (Google, Kakao, GitHub)


---

## ✨ 만든 사람

- 👨‍💻 개발자: [홍성휘](https://github.com/SungHuii)
- 📧 이메일: gkemg2017@gmail.com
