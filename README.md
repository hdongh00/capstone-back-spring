# 🧠 Refill (심리 케어 서비스)

> **"당신의 마음을 기록하고, AI와 함께 성장하세요."** > 사용자의 대화 패턴과 감정을 분석하여 개인화된 심리 리포트를 제공하고,  
> 커뮤니티를 통해 서로를 위로하는 멘탈 케어 플랫폼입니다.

---

## 📖 프로젝트 개요 (Overview)
현대인의 정신 건강 관리를 돕기 위해 개발된 본 서비스는 **LLM 기반의 AI 챗봇**과 **사용자 커뮤니티**를 결합했습니다.
단순한 대화를 넘어, 사용자의 언어적 표현을 분석하여 **3가지 핵심 성장 지표(긍정성, 안정도, 자신감)**를 시각화하여 제공함으로써 사용자가 스스로의 마음 상태를 객관적으로 파악하고 관리할 수 있도록 돕습니다.

---

## 🛠 기술 스택 (Tech Stack)

### Backend
| Category | Technology |
| --- | --- |
| **Framework** | Spring Boot 3.2.4 |
| **Language** | Java 17 |
| **Security** | Spring Security, JWT, OAuth2 (Kakao) |
| **Database** | MariaDB (User/Community), MongoDB (Chat Logs) |
| **AI Framework** | LangChain (Prompt & Context Management) |
| **AI Model** | OpenAI GPT API |
| **Communication** | WebSocket (STOMP), REST API |

### Frontend
| Category | Technology |
| --- | --- |
| **Framework** | React |
| **Styling** | Styled-components (or CSS Framework) |
| **Communication** | Axios, SockJS |

### Infrastructure
| Category | Technology |
| --- | --- |
| **Server** | AWS EC2 (Ubuntu) / Raspberry Pi |
| **Build Tool** | Gradle |

---

## ✨ 주요 기능 (Key Features)

### 1. 💬 AI 심리 상담 챗봇
* **실시간 대화**: WebSocket(STOMP)을 이용한 지연 없는 실시간 채팅 환경 구현
* **대화 로그 저장**: MongoDB를 활용하여 방대한 양의 대화 내역을 유연하게 저장하고 관리

### 2. 📊 개인화된 성장 지표 (Growth Metrics)
단순 평균이 아닌, 사용자의 **최근 상태와 변화 추세**를 반영하는 독자적인 알고리즘을 적용했습니다.

* **긍정성 (Positivity)**: 지수 가중 이동 평균(EMA)를 적용하여, 과거보다 최근의 감정 상태가 점수에 더 크게 반영

* **안정도 (Stability)**: 감정의 표준편차를 기반으로 계산, 감정 기복이 적고 꾸준할수록 높은 점수 부여
   
* **자신감 (Confidence)**: 긍정성과 안정도가 모두 높거나, 최근 감정 상태가 '상승세'일 경우 추가 보너스 점수 부여

### 3. 🏘️ 치유 커뮤니티 (Forum)
* **감정 일기 공유**: 오늘 하루의 감정을 게시글로 작성하고 공유 가능
* **소통 기능**: 댓글과 좋아요 기능을 통해 사용자 간의 공감과 위로를 나눌 수 있음
* **필터링**: 최신순, 공감순, 댓글순으로 정렬을 지원

### 4. 🔐 사용자 편의성
* **Kakao 소셜 로그인**: OAuth 2.0 기반의 간편 로그인 기능 지원
* **보안**: JWT (Access/Refresh Token) 기반의 안전한 보안 인증/인가 구현
 
---

## 📡 API 명세 (Key API Endpoints)

### 👤 User & Auth

| Method | Endpoint | Description |
|------|---------|------------|
| GET | `/api/user/oauth2/kakao` | 카카오 소셜 로그인 및 JWT 발급 |
| GET | `/auth/user` | 사용자 프로필 정보 조회 |
| POST | `/auth/user` | 사용자 프로필 수정 |

---

### 💬 Chat & Analysis

| Method | Endpoint | Description |
|------|---------|------------|
| POST | `/auth/conversations` | 새로운 AI 대화방 생성 |
| GET | `/auth/messages` | 채팅방 대화 내역 조회 (Paging) |
| GET | `/auth/growth` | 사용자 심리 성장 지표 조회 (긍정, 안정, 자신감) |
| GET | `/auth/calendar` | 월별 감정 캘린더 데이터 조회 |

---

### 📝 Community (Board)

| Method | Endpoint | Description |
|------|---------|------------|
| POST | `/api/board` | 게시글 작성 |
| GET | `/api/board` | 게시글 목록 조회 (정렬 지원) |
| POST | `/api/board/{forumId}/comments` | 댓글 작성 |
| POST | `/api/board/{forumId}/like` | 게시글 좋아요 토글 |

---


## 📂 프로젝트 구조 (Package Structure)

```bash
com.website
├── board           # 커뮤니티(게시글, 댓글) 도메인
├── chat            # 채팅 서비스 및 감정 분석 로직 (MongoDB 연동)
├── config          # WebSocket, Security, Web 설정
├── entity          # JPA(MySQL) 및 MongoDB Document 엔티티
├── repository      # Data Access Layer (JPA & MongoRepository)
├── security        # JWT 필터 및 OAuth2 핸들러
├── user            # 회원 관리 및 카카오 로그인
└── websocket       # 소켓 메시지 브로커 설정
```

---

## 🏗 시스템 아키텍처 (System Architecture)

본 프로젝트는 데이터의 특성에 따라 RDBMS와 NoSQL을 혼용하는 **Hybrid Database 전략**을 채택했습니다.

```mermaid
graph TD
    subgraph Client
        FE[("🖥️ React Frontend")]
    end

    subgraph Server ["Backend Server"]
        SB[("🍃 Spring Boot Application")]
        Logic["Core Logic<br/>(Auth, Chat, Board)"]
        Lang[("🦜🔗 LangChain<br/>(LLM Orchestration)")]
        
        SB --> Logic
        Logic --> Lang
    end

    subgraph Database ["Hybrid Data Storage"]
        Maria[("🐬 MariaDB (RDBMS)<br/>User / Board / Analysis")]
        Mongo[("🍃 MongoDB (NoSQL)<br/>Chat Logs / History")]
    end

    subgraph External ["External Services"]
        Kakao[("💬 Kakao Login")]
        GPT[("🤖 OpenAI GPT API")]
    end

    FE -- "REST API / WebSocket" --> SB
    
    Logic -- "JPA" --> Maria
    Logic -- "MongoRepository" --> Mongo
    Logic -- "OAuth2" --> Kakao
    
    Lang -- "API Request" --> GPT



