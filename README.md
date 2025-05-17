## 💩 주의사항

- 메인 브랜치에 직접 푸시 금지. PR로 코드 리뷰 받고 머지
- Vertex AI는 쿼리당 비용 발생! 테스트할 때 신중하게 요청

## 🚨 개발 환경 설정

- google-services.json
  파일은 https://console.firebase.google.com/project/slow-clock-scheduler/settings/general/android:com.example.slowclock
  에서 받아 `app`에 넣기
- service_account.json 파일은 `app/src/main/res/raw/`에 넣기
- 본인 디버그용 SHA-1
  키를 https://console.firebase.google.com/project/slow-clock-scheduler/settings/general/android:com.example.slowclock
  에 등록

```
  ./gradlew signingReport  # 맥/리눅스
  gradlew signingReport    # 윈도우
```

## 📋 개발 규칙

- 각자 개발할 기능은 feature branch로 분리해서 작업 (feature/기능명)
- 커밋 메시지 규칙: "[기능] 내용" 형식으로 작성
- API/외부 서비스 관련 로그 태그는 `{기능영역}_SLOWCLOCK` 형식으로 작성
    - 예: `Auth_SLOWCLOCK`, `Calender_SLOWCLOCK`, `FCM_SLOWCLOCK`, `DB_SLOWCLOCK`

## 📦 패키지 구조

* `com.example.slowclock`
    * `data`: 데이터 관련 클래스
        * `model`: 앱에서 사용하는 데이터 객체들 (Schedule, User 등)
        * `repository`: Firestore DB 접근 및 데이터 CRUD 처리 클래스
        * `api`: VertexAI API 연동 인터페이스 및 요청/응답 모델
    * `domain`: 비즈니스 로직
        * `usecase`: 앱의 주요 기능 구현 UseCase 클래스
    * `ui`: UI 관련 클래스
        * `theme`: 앱 테마, 색상, 타이포그래피 정의
    * `util`: 유틸리티 클래스
        * 구글 로그인, 캘린더 API 연동
        * FCM 토큰 관리
        * 테스트용 유틸리티
    * `service`: 백그라운드 서비스
        * FCM 알림 처리
    * `config`: 앱 설정 상수 (AI 관련 등)

com.example.slowclock

data: 데이터 관련 클래스

model: 앱에서 사용하는 데이터 객체들 (Schedule, User 등)
repository: Firestore DB 접근 및 데이터 CRUD 처리 클래스
api: 외부 API 연동 인터페이스 (VertexAI API 등)

domain: 비즈니스 로직

usecase: 앱의 주요 기능 구현 클래스

ui: UI 관련 클래스

theme: 앱 테마, 색상, 타이포그래피 정의

util: 유틸리티 클래스

구글 로그인, 캘린더 API 연동
FCM 토큰 관리
테스트용 유틸리티

service: 백그라운드 서비스

FCM 알림 처리

config: 앱 설정 상수 (AI 관련 등)