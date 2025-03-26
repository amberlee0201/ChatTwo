# 🗨️ ChatTwo - 실시간 채팅 웹 서비스

## 📌 프로젝트 개요

**ChatTwo**는 카카오톡을 벤치마킹한 실시간 채팅 서비스입니다.  
WebSocket + Redis Pub/Sub 구조를 통해 실시간 통신을 구현하고,  
Google OAuth 로그인, 친구 관리, 채팅방 참여 및 초대, 파일 전송 기능 등을 제공합니다.  
전체 인프라는 AWS 상에 구축되었으며, Terraform과 ArgoCD(GitOps)를 통해 선언적으로 관리되고,  
EKS(Kubernetes) 환경에서 실행됩니다.

---

## 🔑 주요 기능

- Google OAuth 로그인 / 회원가입
- 사용자 이름 및 프로필 사진 설정
- 친구 추가, 삭제, 검색
- 채팅방 생성, 조회, 입장, 나가기, 친구 초대
- 실시간 채팅 (WebSocket + STOMP + Redis Pub/Sub)
- 파일 전송, 무한 스크롤 페이징
- 알림 기능 (친구 추가, 새 메시지 등)

---

## 🔥 팀원 역할

| 이름  | 담당 업무                                                          |
|-----|----------------------------------------------------------------|
| 강수재 | 채팅방 관련 기능 / DynamoDB 기초 설계 /  Terraform AWS 인프라 / Jira 프로젝트 관리 |
| 강대현 | 실시간 친구 추가(구독)기능, WebSocket + STOMP기반 설계 및 구현                   |
| 김동현 | Back-end / OAuth / CI-CD / WebSocket / 채팅 기능 / GitHub 관리       |
| 허선호 | 회원 관리 기능, 인프라 구축 보조                                            |

---

## 🧱 기술 스택

### Back-End
- Java 17, Spring Boot 3.4
- Spring Security + OAuth2 (Google)
- WebSocket + STOMP + SockJS
- Redis (Session + Pub/Sub)
- MySQL 8.0 (회원, 친구 관리)
- DynamoDB (채팅방 정보 및 메시지 저장)
- S3 (프로필 사진, 채팅 파일 저장)

### Front-End
- Thymeleaf
- Bootstrap 5, Vanilla JS

### DevOps / Infra
- AWS EKS + Karpenter
- RDS, S3, DynamoDB, Route53, CloudFront
- Docker, ECR
- Terraform (IaC)
- ArgoCD (GitOps 배포)
- Prometheus + Grafana (모니터링)
- GitHub Actions (CI)

---

## 📄 ERD 설계

### MySQL
<img src="https://imgur.com/CMClrLq.png" alt="mysql" width="500"/>

### DynamoDB
<img src="https://imgur.com/qOSKP77.png" alt="dynamo"/>   

---

## 📌 API 명세서

![API](https://imgur.com/LtvZfW5.png)

---

## 🏗️ 시스템 아키텍처

![Architecture](https://imgur.com/ZNg9Kgn.png)

---

## 🗂️ 프로젝트 디렉터리 구조

### 백엔드 서비스 (Spring Boot)

```
📂 src/main/java/com/ce/chat2
├── 📂 chat              # 실시간 채팅 처리
├── 📂 room              # 채팅방 관련 로직
├── 📂 participation     # 채팅방 참여자 관리
├── 📂 user              # 사용자 및 OAuth 로그인
├── 📂 follow            # 친구 추가 / 삭제 / 검색
├── 📂 notification      # 알림 이벤트 처리
└── 📂 common            # 공통 설정 (S3, OAuth, 예외 등)
```

### 배포 리소스 (Kubernetes Manifest)

```
📂 manifest/
├───📄 chat2-app-manifest.yaml
│   ├── service
│   ├── ingress
│   ├── deployment
│   ├── hpa
└── 📄 redis.yaml
    ├── service
    ├── deployment
    └── config

```

### 인프라 (Terraform)

```
📂 terraform/
├── 📂 envs/dev              # 개발 환경
└── 📂 modules/
    ├── 📂 backend
    ├── 📂 dynamodb
    ├── 📂 ecr
    ├── 📂 eks-karpenter
    ├── 📂 rds
    ├── 📂 route53-acm
    ├── 📂 s3-cloudfront
    └── 📂 vpc
```

---

## 🔍 기술적 구현 포인트

### 🧵 실시간 채팅 구조
- WebSocket + STOMP → 빠른 양방향 통신
- Redis Pub/Sub → 다중 서버 환경 대응
- SockJS → WebSocket 미지원 환경 지원

### 🔐 OAuth 인증
- Spring Security + OAuth2로 Google 로그인 연동

### 💾 Redis Session
- 무중단 배포 및 세션 공유를 위한 중앙 세션 관리

### ⚡ NoSQL (DynamoDB)
- 채팅 메시지를 빠르게 쓰고 읽기 위한 NoSQL 설계

### 🛠 Terraform
- AWS 리소스를 코드로 관리하여 자동화 및 버전 관리

### 🧪 부하 테스트
- k6로 부하 한계, 병목 구간 사전 점검

### ☁️ Karpenter + Prefix Delegation
- 비용 절감 및 Pod 밀도 향상
- 수요 기반 자동 노드 생성/제거

---

## 🚀 실행 방법

> AWS에서 실행:

1. terraform 초기화 & 실행으로 인프라 구축
2. bastion host 접속 후 EKS 초기 설정 진행
3. 백엔드 서비스에 `application.properties` 설정
4. 원격 저장소에 push -> GitHub Actions → ArgoCD + EKS 연동으로 자동화

---
## 🌟 트러블슈팅

### 1️⃣ Karpenter 노드 생성 오류
**원인:** Karpenter 구버전의 버그로 unknown capacity type capacity-block 오류 발생   
**해결:** Karpenter를 최신 버전으로 업그레이드. 단, 새 버전에서 변경된 api에 따라 NodePool, EC2NodeClass 등을 설정(기존 API: Provisioner, AWSNodeTemplate).

### 2️⃣ ALB Controller 네트워크 인터페이스 인식 오류
**원인:** Terraform EKS 모듈 전체에 `karpenter.sh/discovery` 태그 지정으로, 불필요한 보안그룹에 해당 태그가 전파되어 karpenter 인스턴스에 적용됨   
**해결:** EKS 모듈 전체가 아닌 managed node group에만 해당 태그 적용

### 3️⃣ K6 STOMP 부하테스트 - STOMP 미지원 이슈
**원인:** K6가 WebSocket은 지원하지만, STOMP 프로토콜은 기본적으로 지원하지 않음.  
**해결:** STOMP 프로토콜을 직접 WebSocket 내부에서 구현하여 테스트를 수행.  
또한 STOMP 연결과 메시지 송수신이 가능한 K6 데모 프로젝트를 작성하여 테스트 자동화 기반 마련.

### 4️⃣ 알림 삭제 요청 후에도 클라이언트에서 알림이 계속 표시되는 오류
**원인:** DB에서 알림이 실제 삭제되지 않았거나(Soft Delete 미적용), 프론트엔드에서 필터링되지 않아 발생.  
**해결:** 알림 테이블에 isDeleted 필드를 추가하여 Soft Delete 적용.  
JavaScript 로직을 수정하여 삭제된 알림을 필터링하고, 삭제 후 실시간으로 UI가 갱신되도록 구현

### 5️⃣ 새 메시지 도착 시 채팅방 목록이 갱신되지 않음
**원인:** 채팅방 정보 업데이트 누락 또는 프론트엔드의 최신 정렬 로직 미작동  
**해결:** 메시지 저장 시 해당 채팅방 정보를 함께 업데이트, Redis Pub/Sub를 통해 채팅방 목록을 구독 중인 사용자들에게 최신 메시지 정보를 포함해 전송  
프론트엔드에서 목록을 실시간 갱신 및 최신순 정렬

---

## 📝 향후 계획

- `Helm Chart`와 `Kustomize` 등을 활용한 EKS 초기 설정 자동화
- Application Load Balancer에 대한 IPv6/IPv4 듀얼 스택 주소 적용
- 더 신속한 Pod auto scheduling 방안 고려
---