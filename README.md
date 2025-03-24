# 🗨️ ChatTwo - 실시간 채팅 웹 서비스

## 📌 프로젝트 개요

**ChatTwo**는 카카오톡을 벤치마킹한 실시간 채팅 서비스입니다.  
WebSocket + Redis Pub/Sub 구조를 통해 실시간 통신을 구현하고,  
Google OAuth 로그인, 친구 관리, 채팅방 참여 및 초대, 이미지 파일 전송, 메시지 읽음 처리 기능 등을 제공합니다.  
전체 인프라는 AWS 상에 구축되었으며, Terraform과 ArgoCD(GitOps)를 통해 선언적으로 관리되고, EKS(Kubernetes) 환경에서 실행됩니다.
---

## 🔑 주요 기능

- Google OAuth 로그인 / 회원가입
- 사용자 이름 및 프로필 사진 설정
- 친구 추가, 삭제, 검색
- 채팅방 생성, 조회, 입장, 나가기, 친구 초대
- 실시간 채팅 (WebSocket + STOMP + Redis Pub/Sub)
- 이미지 파일 전송, 메시지 읽음 처리, 무한 스크롤 페이징
- 알림 기능 (친구 추가, 새 메시지 등)
---

## 🔥 팀원 역할

| 이름  | 담당 업무                                                   |
|-----|---------------------------------------------------------|
| 강수재 |           |
| 강대현 |  |
| 김동현 |  |
| 허선호 | 회원 관리 기능, 인프라 구축 보조 |

---

## 🧱 기술 스택

### Back-End
- Java 17, Spring Boot 3.4
- Spring Security + OAuth2 (Google)
- WebSocket + STOMP + SockJS
- Redis (Session + Pub/Sub)
- MySQL 8.0 (회원, 친구 관리)
- DynamoDB (채팅방 정보 및 메시지 저장)
- S3 (프로필 사진, 채팅 이미지 저장)

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

<img src="https://i.imgur.com/CMClrLq.png" alt="mysql" width="500"/>   
<img src="https://i.imgur.com/qOSKP77.png" alt="dynamo"/>   

---

## 📌 API 명세서

![사진 넣을 예정]()

---

## 🏗️ 시스템 아키텍처

![Architecture](https://i.imgur.com/ZNg9Kgn.png)

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
│   ├── 📄 chat2-app-manifest.yaml
│   │   ├── service
│   │   ├── ingress
│   │   ├── deployment
│   │   ├── hpa
│   ├── 📄 redis.yaml
│   │   ├── service
│   │   ├── deployment
└── └── └── config

```

### 인프라 (Terraform)

```
📂 terraform/
├── 📂 envs/dev              # 개발 환경
└── 📂 modules/
│   ├── 📂 backend
│   ├── 📂 dynamodb
│   ├── 📂 ecr
│   ├── 📂 eks-karpenter
│   ├── 📂 rds
│   ├── 📂 route53-acm
│   ├── 📂 s3-cloudfront
└── └── 📂 vpc
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

### 1️⃣ ALB Controller 네트워크 인터페이스 인식 오류
**원인:** Terraform EKS 모듈 전체에 `karpenter.sh/discovery` 태그 지정으로, 불필요한 보안그룹에 해당 태그가 전파되어 karpenter 인스턴스에 적용됨   
**해결:** EKS 모듈 전체가 아닌 managed node group에만 해당 태그 적용

### 2️⃣ 문제
**원인:** 원인  
**해결:** 해결

---

## 📝 향후 계획

- 미흡했던 사항, 시간내에 구현하지 못했던 기능, 하고 싶었는데 못했던 것들?
- `Helm Chart`와 `Kustomize` 등을 활용한 EKS 초기 설정 자동화
- Application Load Balancer에 대한 IPv6/IPv4 듀얼 스택 주소 적용

---