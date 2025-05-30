## 💩 주의사항

- 메인 브랜치에 직접 푸시 금지. PR로 코드 리뷰 받고 머지
- Vertex AI는 쿼리당 비용 발생! 테스트할 때 신중하게 요청

## 🚨 개발 환경 설정

### 1. google-services.json (Firebase 기본 설정)

- **용도**: Firebase Auth, Firestore 등 기본 Firebase 서비스용
- **위치**: `app/google-services.json`
- **다운로드
  **: https://console.firebase.google.com/project/slow-clock-scheduler/settings/general/android:com.example.slowclock

### 2. service_account.json (AI 기능 전용)

- **용도**: Vertex AI API 호출용
- **위치**: `app/src/main/res/raw/service_account.json`
- **다운로드**:
    1. https://console.firebase.google.com/project/slow-clock-scheduler/settings/serviceaccounts/adminsdk
       접속
    2. "새 비공개 키 생성" 클릭
    3. Node.js 선택 → 키 생성
    4. 다운로드된 파일을 `service_account.json`로 이름 변경

### 3. 디버그 SHA-1 키 등록

- **용도**: Google 로그인 기능을 위한 앱 인증
- https://console.firebase.google.com/project/slow-clock-scheduler/settings/general/android:com.example.slowclock
  에서 본인의 디버그용 SHA-1 키 추가

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