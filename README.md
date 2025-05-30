## 💩 주의사항

- 메인 브랜치에 직접 푸시 금지. PR로 코드 리뷰 받고 머지
- 각자 개발할 기능은 feature branch로 분리해서 작업 (feature/기능명)
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

## 📦 패키지 구조

* `com.example.slowclock`
    * `auth`: Google OAuth 로그인
    * `data`: 데이터 모델 및 Firestore/API 연동
        * `model`: Schedule, User 등 데이터 클래스
        * `remote`: DB 접근 및 API 연동
    * `ui`: Jetpack Compose UI
        * `main`: 메인 화면
        * `addschedule`: 일정 추가/편집
        * `theme`: 접근성 테마
    * `navigation`: 화면 라우팅
    * `notification`: FCM 알림
    * `util`: 공통 유틸리티