## 🚨 개발 환경 설정

- google-services.json 파일은 리포에 없음. 각자 Firebase 콘솔에서 다운로드

## 📋 개발 규칙

- 각자 개발할 기능은 feature branch로 분리해서 작업 (feature/기능명)
- 커밋 메시지 규칙: "[기능] 내용" 형식으로 작성

## 💩 주의사항

- 메인 브랜치에 직접 푸시 금지. PR로 코드 리뷰 받고 머지

## 📦 패키지 구조

- `com.example.slowclock`
    - `data`: 데이터 관련 클래스
        - `model`: 앱에서 사용하는 데이터 객체들 (Schedule, User 등)
        - `repository`: Firestore DB 접근 및 데이터 CRUD 처리 클래스
    - `ui`: UI 관련 클래스
        - `theme`: 앱 테마, 색상, 타이포그래피 정의
    - `util`: 유틸리티 클래스
        - 구글 로그인, 캘린더 API 연동
        - FCM 토큰 관리
        - 테스트용 유틸리티
        - 기타 앱 전반에 걸쳐 사용되는 헬퍼 클래스
    - `service`: 백그라운드 서비스
        - FCM 알림 처리

### API/외부 서비스 관련 로그 태그 규칙

- `{기능영역}_SLOWCLOCK` 형식으로 작성
    - 예: `Auth_SLOWCLOCK`, `Calender_SLOWCLOCK`, `FCM_SLOWCLOCK`, `DB_SLOWCLOCK`