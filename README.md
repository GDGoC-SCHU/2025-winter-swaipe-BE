# 🚀 SWAIPE API 서버

## 📑 목차
- [개발 환경 설정](#-개발-환경-설정)
- [개발 가이드](#-개발-가이드)

## ⚙️ 개발 환경 설정

### 💻 기술 스택
- ☕️ Java 17
- 🌱 Spring Boot(3.x.x)
- 🐬 MySQL 8.0.37
- 🔄 Redis 6.2
- 🔍 QueryDSL
- 🐳 Docker

### 🛠️ 환경 설정
1. `.env` 파일 생성:
```properties
SERVER_PORT=8080
DB_URL=localhost:3306
DB_NAME=swaibe
DB_USER=root
DB_PASSWORD=your_password
REDIS_HOST=redis
REDIS_PORT=6379
JWT_KEY=your_jwt_key
MYSQL_PORT=3307
```

### 🚀 실행방법
```bash
# 도커 컨테이너 실행
docker-compose up -d
```

## 🌱 개발 가이드

### 🔀 브랜치 전략 (GitHub Flow)
* `main`: 배포 가능한 안정적인 코드
* `dev`: 개발자들이 기능을 병합하는 통합 브랜치
* `feature/*`: 새로운 기능 개발 브랜치
* `bugfix/*`: 버그 수정 브랜치

### 📝 커밋 컨벤션
| 타입 | 설명 |
|------|------|
| **feat** | 새로운 기능 추가 |
| **fix** | 버그 수정 |
| **docs** | 문서 수정 |
| **style** | 코드 스타일 수정 |
| **refactor** | 코드 리팩토링 |
| **test** | 테스트 코드 추가 |
| **chore** | 빌드 업무 수정 |

### 📥 PR 템플릿
```markdown
## #️⃣ 연관된 이슈
> ex) #이슈번호

## 📝 작업 내용
> 작업 내용 설명

## 💬 리뷰 요구사항(선택)
> 리뷰어 집중 확인 필요 사항
```

### 📂 프로젝트 구조
```
src/main/java/com/sparta/donut/
├── 🔧 config/
├── 📦 domain/
│   └── product/
│       ├── 🎮 controller/
│       ├── ⚙️ service/
│       ├── 💾 repository/
│       └── 📑 entity/
└── 🌍 global/
    ├── ❌ error/
    └── 🛠️ utils/
```

### 🔍 코드 리뷰 가이드
- 코드 스타일과 일관성 체크
- 불필요한 코드/주석 제거
- 예외 처리 적절성 확인
- 성능 및 보안 이슈 검토

⚠️ **주의:** `.env` 파일은 반드시 `.gitignore`에 포함시켜 주세요!
