# 📘 SWAB 프로젝트 개발 가이드

## 📑 Table of Contents
1. [📂 프로젝트 구조](#-프로젝트-구조)
2. [🔑 주요 컴포넌트 설명](#-주요-컴포넌트-설명)
3. [🚨 에러 처리 전략](#-에러-처리-전략)
4. [📝 로깅 전략](#-로깅-전략)
5. [🛠️ 개발 가이드라인](#️-개발-가이드라인)
6. [🔑 API 상세 설명](#-api-상세-설명)
7. [🛡️ 토큰 관리 시스템](#️-토큰-관리-시스템)
8. [🔒 보안 설정](#-보안-설정)
9. [⚙️ 개발 환경 설정](#️-개발-환경-설정)
10. [🧪 테스트 시나리오](#-테스트-시나리오)
11. [📥 PR 작성 가이드](#-pr-작성-가이드)
12. [🔍 코드 리뷰 가이드](#-코드-리뷰-가이드)
13. [🔐 환경 변수 및 Docker 설정](#-환경-변수-및-docker-설정)

## 🔀 브랜치 전략 (GitHub Flow)

### 🔧 브랜치 구조
* `main`: 배포 가능한 안정적인 코드
* `dev`: 개발자들이 기능을 병합하는 통합 브랜치
* `feature/*`: 새로운 기능 개발을 위한 브랜치
* `bugfix/*`: 버그 수정 브랜치

### 🔄 브랜치 흐름
1. **기능 개발**: `feature/기능명` 브랜치 생성 후 개발
2. **PR 요청**: `dev` 브랜치로 Pull Request
3. **코드 리뷰 및 머지** 후 `main`으로 병합
4. **배포** 시 `main` 기준으로 진행

```bash
# 기능 브랜치 생성 및 이동
git checkout -b feature/기능명

# 작업 후 커밋 및 dev로 PR
git add .
git commit -m "feat: 기능 추가"
git push origin feature/기능명
```

## 📝 커밋 메시지 컨벤션

### 🎯 커밋 타입
| 타입 | 설명 |
|------|------|
| **feat** | 새로운 기능 추가 |
| **fix** | 버그 수정 |
| **docs** | 문서 수정 (README 등) |
| **style** | 코드 스타일 수정 (포맷, 세미콜론 누락 등) |
| **refactor** | 코드 리팩토링 |
| **test** | 테스트 코드 추가 |
| **chore** | 빌드 업무 수정, 패키지 매니저 설정 |

### ✍ 커밋 메시지 예시
```
feat: 사용자 로그인 기능 추가
fix: 회원가입 시 비밀번호 검증 로직 수정
refactor: 게시글 조회 로직 최적화
```

### 🛠️ 커밋 메시지 규칙
1. 커밋 메시지는 **영어**로 작성합니다.
2. 커밋 제목은 **명령문** 형태로 작성합니다.
3. 커밋 단위는 작은 단위로 자주 커밋합니다.

## 📥 PR 작성 가이드

### ✅ PR 규칙
1. **PR 제목**은 명확하게 작성합니다.
2. **PR 설명**은 작업 내용을 자세히 작성합니다.
3. **Assignees**와 **Reviewers**를 지정합니다.

### 📌 PR 템플릿 예시
```markdown
## #️⃣ 연관된 이슈
> ex) #이슈번호, #이슈번호

## 📝 작업 내용
> 이번 PR에서 작업한 내용을 간략히 설명해주세요(이미지 첨부 가능)

### 스크린샷 (선택)

## 💬 리뷰 요구사항(선택)
> 리뷰어가 특별히 봐주었으면 하는 부분이 있다면 작성해주세요
> ex) 메서드 XXX의 이름을 더 잘 짓고 싶은데 혹시 좋은 명칭이 있을까요?
```

## 🔍 PR 코드 확인 및 테스트 가이드

### 🔄 PR 코드베이스 가져오기

1. **원격 브랜치 정보 최신화**
```bash
# 원격 저장소의 최신 정보를 가져옵니다
git fetch origin

# 특정 PR의 브랜치를 확인합니다 (예: PR #123)
git fetch origin pull/123/head:pr-123
```

2. **PR 브랜치로 전환**
```bash
# 새로 생성된 PR 브랜치로 전환
git checkout pr-123

# 또는 PR 브랜치를 새로 만들어 전환
git checkout -b review-123 pull/123/head
```

3. **특정 커밋으로 이동**
```bash
# 특정 커밋으로 이동하여 코드 확인
git checkout <commit-hash>

# 이전 브랜치로 돌아가기
git checkout -
```

### 🧪 PR 코드 테스트

1. **의존성 설치 및 빌드**
```bash
# Maven 프로젝트의 경우
./mvnw clean install

# Gradle 프로젝트의 경우
./gradlew clean build
```

2. **테스트 실행**
```bash
# 전체 테스트 실행
./mvnw test

# 특정 테스트 클래스만 실행
./mvnw test -Dtest=TestClassName

# 특정 테스트 메소드만 실행
./mvnw test -Dtest=TestClassName#methodName
```

3. **로컬 환경 설정**
```bash
# 환경변수 파일 복사
cp .env.example .env

# 환경변수 파일 수정
vi .env

# Docker 환경 실행
docker-compose up -d
```

### 💡 PR 리뷰 시 유용한 Git 명령어
```bash
# 변경된 파일 목록 확인
git diff --name-only main

# 특정 파일의 변경 내용 확인
git diff main -- path/to/file

# 커밋 이력 확인
git log --oneline --graph

# 특정 라인의 작성자 확인
git blame path/to/file
```

### ⚠️ 주의사항

1. **브랜치 관리**
    - PR 확인 후에는 로컬 브랜치 정리
   ```bash
   git branch -D pr-123
   ```
    - 주기적으로 원격 브랜치 정보 정리
   ```bash
   git remote prune origin
   ```

2. **환경 설정**
    - PR의 환경 설정이 기존과 다를 수 있으므로 `.env.example` 파일 확인
    - 새로운 의존성이 추가되었는지 확인

3. **데이터베이스**
    - 스키마 변경사항 확인
    - 마이그레이션 스크립트 실행 필요 여부 확인

## 🔍 코드 리뷰 가이드

### 📝 리뷰 프로세스
1. **PR 생성** → **Reviewers** 지정
2. **리뷰어**는 코드 확인 후 **Approve** 또는 **Request changes** 선택
3. 피드백 반영 후 **Re-request Review**
4. 리뷰어의 승인 후 **Merge**

### 🔎 리뷰 체크리스트
* 코드 스타일과 일관성 유지
* 불필요한 코드 및 주석 제거
* 예외 처리 및 에러 핸들링 적절성
* 성능 및 최적화 고려
* 보안 이슈 및 민감 정보 확인

### 💡 리뷰어 코멘트 예시
```
💡 제안: 메서드 네이밍을 조금 더 명확하게 하면 어떨까요?
❗️ 수정 필요: 입력 값 검증이 부족해 보입니다. 추가해 주세요.
✅ 확인 완료: 전체적으로 잘 구현되어 있습니다!
```

## 🔐 환경 변수 및 Docker 설정

### 📦 Docker 환경변수 설정

```yaml
version: '3.8'
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
      args:
        DEPENDENCY: build/dependency
    ports:
      - "${SERVER_PORT}:${SERVER_PORT}"
    environment:
      SERVER_PORT: ${SERVER_PORT}
      DB_URL: ${DB_URL}
      DB_NAME: ${DB_NAME}
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      REDIS_HOST: ${REDIS_HOST}
      REDIS_PORT: ${REDIS_PORT}
      JWT_KEY: ${JWT_KEY}
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:8.0.37
    restart: always
    environment:
      MYSQL_DATABASE: ${DB_NAME}
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
    ports:
      - "${MYSQL_PORT}:3306"

  redis:
    image: redis:6.2
    restart: always
    ports:
      - "${REDIS_PORT}:${REDIS_PORT}"
    command: redis-server --appendonly yes
```

⚠️ **보안 주의:** 환경변수 파일(.env)은 Git에 커밋하지 말고 `.gitignore`에 추가하세요!

🚀 **팀원 모두가 일관된 개발 환경과 효율적인 협업을 위해 본 가이드를 반드시 준수해 주세요!**