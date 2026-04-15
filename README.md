# 👕 WeatherFit

## 📒 팀 협업 문서 링크
- 팀 문서 링크 추가

## 🤝 팀원 구성
- 황준영
- 김지예
- 박도겸
- 안대식
- 최태훈

---

## 📌 프로젝트 소개
> **WeatherFit**은 사용자의 위치, 체감 온도, 보유 의상 정보를 바탕으로  
> 날씨에 맞는 옷차림을 추천하고, 피드·팔로우·DM·알림 기능까지 제공하는  
> **패션 추천 소셜 플랫폼**입니다.

단순히 현재 날씨를 보여주는 서비스가 아니라,  
사용자의 프로필과 옷장 데이터를 함께 반영하여 더 개인화된 의상 추천을 제공하는 것을 목표로 했습니다.  
또한 OOTD 피드, 팔로우, 실시간 알림, DM 기능을 결합해 사용자 간 소셜 경험도 함께 제공하도록 설계했습니다.

### ⏰ 프로젝트 기간
- 2026.03.11 ~ 2026.04.17

---

## 🛠️ 기술 스택

### Backend
**Core**
- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Actuator
- Spring Batch
- Lombok

**Libs**
- QueryDSL
- JWT
- OAuth2 Client
- Swagger / Springdoc
- WebSocket / STOMP
- SSE

***

### Database
- PostgreSQL
- Redis
- H2 (Test / Local 확인용)
- Elasticsearch

***

### Infrastructure
**Infra**
- Docker
- Docker Compose

**CI/CD**
- GitHub Actions

***

### AWS
**Backend Infrastructure**
- AWS ECS
- AWS ECR
- AWS RDS
- AWS ElastiCache
- AWS S3
- Application Load Balancer

**Monitoring**
- AWS CloudWatch
- AWS Managed Prometheus

---

## ✨ 주요 기능

### 🙍 사용자 / 프로필
- 회원가입 / 로그인 / 로그아웃
- JWT 기반 인증 / 인가
- CSRF 토큰 기반 보안 처리
- 사용자 프로필 조회 / 수정
- 위치 정보 및 체감 온도 민감도 설정
- 관리자 계정 초기화
- 사용자 권한 변경 / 계정 잠금
- 권한 변경 및 잠금 시 강제 로그아웃 처리

### 🌐 소셜 로그인
- Google OAuth2 로그인
- Kakao OAuth2 로그인
- 소셜 계정 기반 사용자 자동 생성 및 연동
- 분산 환경(ECS 다중 태스크)에서도 동작할 수 있도록 인증 요청 저장 구조 개선

### 🌤️ 날씨 / 추천
- 사용자 위치 기반 날씨 조회
- 날씨 데이터 캐싱
- 날씨와 프로필 정보를 반영한 의상 추천
- 체감 온도 및 사용자 성향 기반 추천 흐름 제공

### 👕 옷장 관리
- 의상 등록 / 수정 / 삭제
- 의상 속성 관리
- 사용자 보유 의상을 기반으로 추천 결과 반영

### 📸 피드
- OOTD 피드 등록 / 조회 / 수정 / 삭제
- 피드 좋아요 / 상호작용
- 피드 검색 및 목록 조회

### 🤝 팔로우 / DM
- 사용자 팔로우 / 언팔로우
- 실시간 DM 송수신
- WebSocket 기반 메시지 처리

### 🛎️ 알림
- SSE 기반 실시간 알림
- 개인 알림 / 시스템 알림 처리
- Redis Pub/Sub 기반 알림 확장 구조
- 읽은 알림 관리 및 정리

### ⚙️ 배치 / 운영
- 날씨 데이터 주기적 수집 및 갱신
- 불필요 데이터 정리 작업
- 운영 환경 기준 로그 및 모니터링 구성
- 분산 환경을 고려한 인증/토큰 관리 구조 적용

---

## 🏗️ 시스템 특징

### 1. 개인화 추천
- 사용자의 위치, 날씨, 체감 온도 민감도, 보유 의상 정보를 함께 반영
- 단순 날씨 조회가 아니라 실제 옷차림 추천까지 연결

### 2. 소셜 기능 결합
- 추천 서비스에 피드, 팔로우, DM, 알림 기능을 결합
- 사용자 간 스타일 공유와 상호작용 지원

### 3. 분산 환경 대응
- ECS 다중 태스크 환경에서 인증이 유지되도록 토큰 저장 구조 개선
- Redis 기반 공유 저장소를 활용해 서버 인스턴스가 달라도 동일한 로그인 상태 유지 가능

### 4. 실시간 처리
- SSE를 통한 실시간 알림 제공
- WebSocket 기반 DM 기능 구현
- Redis Pub/Sub 구조를 활용한 확장성 확보

---

## 📂 프로젝트 구조

```text
.
├── build.gradle
├── settings.gradle
├── Dockerfile
├── Dockerfile.elasticsearch
├── docker-compose.yml
├── docker-compose.multi.yml
├── schema.sql
├── init-db.sh
├── .github
│   └── workflows
│       ├── Deploy.yml
│       └── Integration.yml
└── src
    └── main
        ├── java
        │   └── com.codeit.weatherfit
        │       ├── WeatherFitApplication.java
        │       ├── domain
        │       │   ├── auth
        │       │   │   ├── controller
        │       │   │   │   └── AuthController.java
        │       │   │   ├── docs
        │       │   │   │   └── AuthControllerDocs.java
        │       │   │   ├── dto
        │       │   │   │   ├── request
        │       │   │   │   │   ├── ResetPasswordRequest.java
        │       │   │   │   │   └── SignInRequest.java
        │       │   │   │   └── response
        │       │   │   │       └── JwtDto.java
        │       │   │   ├── entity
        │       │   │   │   ├── SocialAccount.java
        │       │   │   │   ├── SocialProvider.java
        │       │   │   │   └── TemporaryPassword.java
        │       │   │   ├── repository
        │       │   │   │   ├── SocialAccountRepository.java
        │       │   │   │   └── TemporaryPasswordRepository.java
        │       │   │   ├── security
        │       │   │   │   ├── HttpCookieOAuth2AuthorizationRequestRepository.java
        │       │   │   │   ├── InMemoryAuthTokenStore.java
        │       │   │   │   ├── JwtAuthenticationFilter.java
        │       │   │   │   ├── JwtTokenProvider.java
        │       │   │   │   ├── OAuth2AuthenticationFailureHandler.java
        │       │   │   │   ├── OAuth2AuthenticationSuccessHandler.java
        │       │   │   │   ├── TemporaryPasswordGenerator.java
        │       │   │   │   ├── WeatherFitUserDetails.java
        │       │   │   │   └── WeatherFitUserDetailsService.java
        │       │   │   └── service
        │       │   │       ├── AuthService.java
        │       │   │       ├── AuthServiceImpl.java
        │       │   │       ├── AuthTokenResult.java
        │       │   │       ├── OAuth2SocialLoginService.java
        │       │   │       ├── OAuth2SocialLoginServiceImpl.java
        │       │   │       ├── PasswordResetMailSender.java
        │       │   │       ├── ConsolePasswordResetMailSender.java
        │       │   │       └── SmtpPasswordResetMailSender.java
        │       │   ├── base
        │       │   │   └── BaseEntity.java
        │       │   ├── clothes
        │       │   │   ├── controller
        │       │   │   │   ├── ClothesController.java
        │       │   │   │   └── ClothesAttributeDefController.java
        │       │   │   ├── docs
        │       │   │   │   ├── ClothesControllerDocs.java
        │       │   │   │   └── ClothesAttributeDefControllerDocs.java
        │       │   │   ├── dto
        │       │   │   │   ├── request
        │       │   │   │   │   ├── ClothesCreateRequest.java
        │       │   │   │   │   ├── ClothesUpdateRequest.java
        │       │   │   │   │   ├── ClothesAttributeDefCreateRequest.java
        │       │   │   │   │   ├── ClothesAttributeDefUpdateRequest.java
        │       │   │   │   │   └── ClothesAttributeDefGetRequest.java
        │       │   │   │   └── response
        │       │   │   │       ├── ClothesDto.java
        │       │   │   │       ├── ClothesDtoCursorResponse.java
        │       │   │   │       ├── ClothesAttributeDto.java
        │       │   │   │       ├── ClothesAttributeDefDto.java
        │       │   │   │       ├── ClothesAttributeWithDefDto.java
        │       │   │   │       ├── SortBy.java
        │       │   │   │       └── SortDirection.java
        │       │   │   ├── entity
        │       │   │   │   ├── Clothes.java
        │       │   │   │   ├── ClothesAttribute.java
        │       │   │   │   ├── ClothesAttributeType.java
        │       │   │   │   ├── ClothesType.java
        │       │   │   │   └── SelectableValue.java
        │       │   │   ├── exception
        │       │   │   │   ├── ClothesException.java
        │       │   │   │   ├── ClothesNotFoundException.java
        │       │   │   │   ├── ClothesExtractionException.java
        │       │   │   │   ├── ClothesAttributeTypeNotFoundException.java
        │       │   │   │   ├── ClothesAttributeValueMissingException.java
        │       │   │   │   └── InvalidClothesAttributeOptionException.java
        │       │   │   ├── repository
        │       │   │   │   ├── ClothesRepository.java
        │       │   │   │   ├── ClothesRepositoryCustom.java
        │       │   │   │   ├── ClothesRepositoryImpl.java
        │       │   │   │   ├── ClothesAttributeRepository.java
        │       │   │   │   ├── ClothesAttributeTypeRepository.java
        │       │   │   │   ├── ClothesAttributeTypeRepositoryCustom.java
        │       │   │   │   ├── ClothesAttributeTypeRepositoryImpl.java
        │       │   │   │   ├── SelectableValueRepository.java
        │       │   │   │   ├── SelectableValueRepositoryCustom.java
        │       │   │   │   └── SelectableValueRepositoryImpl.java
        │       │   │   └── service
        │       │   │       ├── ClothesService.java
        │       │   │       ├── ClothesServiceImpl.java
        │       │   │       ├── AttributeDefService.java
        │       │   │       ├── AttributeDefServiceImpl.java
        │       │   │       └── SortByConverter.java
        │       │   ├── feed
        │       │   │   ├── controller
        │       │   │   │   └── FeedController.java
        │       │   │   ├── docs
        │       │   │   │   └── FeedControllerDocs.java
        │       │   │   ├── dto
        │       │   │   │   ├── FeedDto.java
        │       │   │   │   ├── CommentDto.java
        │       │   │   │   ├── Ootd.java
        │       │   │   │   ├── request
        │       │   │   │   │   ├── FeedCreateRequest.java
        │       │   │   │   │   ├── FeedUpdateRequest.java
        │       │   │   │   │   ├── FeedGetRequest.java
        │       │   │   │   │   ├── CommentCreateRequest.java
        │       │   │   │   │   ├── CommentGetRequest.java
        │       │   │   │   │   ├── SortBy.java
        │       │   │   │   │   └── SortDirection.java
        │       │   │   │   └── response
        │       │   │   │       ├── FeedGetResponse.java
        │       │   │   │       └── CommentGetResponse.java
        │       │   │   ├── entity
        │       │   │   │   ├── Feed.java
        │       │   │   │   ├── Comment.java
        │       │   │   │   ├── FeedLike.java
        │       │   │   │   ├── FeedClothes.java
        │       │   │   │   ├── ClothesSnapshot.java
        │       │   │   │   ├── WeatherSnapshot.java
        │       │   │   │   └── search
        │       │   │   │       └── FeedDocument.java
        │       │   │   ├── event
        │       │   │   │   ├── FeedCreatedEvent.java
        │       │   │   │   ├── FeedDeletedEvent.java
        │       │   │   │   └── FeedUpdatedEvent.java
        │       │   │   ├── eventListener
        │       │   │   │   └── EsEventListener.java
        │       │   │   ├── exception
        │       │   │   │   ├── FeedException.java
        │       │   │   │   ├── FeedBadRequestException.java
        │       │   │   │   ├── FeedForbiddenException.java
        │       │   │   │   ├── FeedNotExistException.java
        │       │   │   │   ├── FeedLikeAlreadyExistException.java
        │       │   │   │   ├── FeedLikeNotExistException.java
        │       │   │   │   ├── FeedDocumentNotFoundException.java
        │       │   │   │   └── CommentNotFoundException.java
        │       │   │   ├── repository
        │       │   │   │   ├── FeedRepository.java
        │       │   │   │   ├── FeedRepositoryCustom.java
        │       │   │   │   ├── FeedRepositoryImpl.java
        │       │   │   │   ├── FeedLikeRepository.java
        │       │   │   │   ├── FeedClothesRepository.java
        │       │   │   │   ├── CommentRepository.java
        │       │   │   │   ├── CommentRepositoryCustom.java
        │       │   │   │   ├── CommentRepositoryCustomImpl.java
        │       │   │   │   └── search
        │       │   │   │       └── FeedSearchRepository.java
        │       │   │   └── service
        │       │   │       ├── FeedService.java
        │       │   │       ├── FeedServiceImpl.java
        │       │   │       └── search
        │       │   │           ├── FeedIndexInitializer.java
        │       │   │           ├── FeedSearchService.java
        │       │   │           └── FeedSearchServiceImpl.java
        │       │   ├── follow
        │       │   │   ├── controller
        │       │   │   │   └── FollowController.java
        │       │   │   ├── docs
        │       │   │   │   └── FollowControllerDocs.java
        │       │   │   ├── dto
        │       │   │   │   ├── request
        │       │   │   │   │   ├── FollowCreateRequest.java
        │       │   │   │   │   ├── FolloweeSearchCondition.java
        │       │   │   │   │   └── FollowerSearchCondition.java
        │       │   │   │   └── response
        │       │   │   │       ├── FollowDto.java
        │       │   │   │       ├── FollowListResponse.java
        │       │   │   │       ├── FollowSummaryDto.java
        │       │   │   │       ├── FollowUser.java
        │       │   │   │       ├── SortBy.java
        │       │   │   │       └── SortDirection.java
        │       │   │   ├── entity
        │       │   │   │   ├── Follow.java
        │       │   │   │   └── FollowCreateParam.java
        │       │   │   ├── exception
        │       │   │   │   ├── FollowException.java
        │       │   │   │   ├── AlreadyFollowException.java
        │       │   │   │   ├── NotExistFollowException.java
        │       │   │   │   ├── SelfFollowNotAllowedException.java
        │       │   │   │   ├── FollowUserNotExistException.java
        │       │   │   │   ├── FollowProfileNotExistException.java
        │       │   │   │   └── InvalidFollowArgumentException.java
        │       │   │   ├── repository
        │       │   │   │   ├── FollowRepository.java
        │       │   │   │   ├── FollowCustomRepository.java
        │       │   │   │   └── FollowCustomRepositoryImpl.java
        │       │   │   └── service
        │       │   │       ├── FollowService.java
        │       │   │       └── FollowServiceImpl.java
        │       │   ├── message
        │       │   │   ├── controller
        │       │   │   │   ├── MessageController.java
        │       │   │   │   └── MessageSocketController.java
        │       │   │   ├── docs
        │       │   │   │   └── MessageControllerDocs.java
        │       │   │   ├── dto
        │       │   │   │   ├── DmDto.java
        │       │   │   │   ├── request
        │       │   │   │   │   ├── MessageCreateRequest.java
        │       │   │   │   │   └── MessageGetRequest.java
        │       │   │   │   └── response
        │       │   │   │       ├── MessageDto.java
        │       │   │   │       ├── MessageCursorResponse.java
        │       │   │   │       ├── MessageUser.java
        │       │   │   │       ├── SortBy.java
        │       │   │   │       └── SortDirection.java
        │       │   │   ├── entity
        │       │   │   │   └── Message.java
        │       │   │   ├── exception
        │       │   │   │   ├── MessageException.java
        │       │   │   │   ├── InvalidMessageArgumentException.java
        │       │   │   │   ├── MessageContentNullException.java
        │       │   │   │   └── NotSendSelfMessageException.java
        │       │   │   ├── repository
        │       │   │   │   ├── MessageRepository.java
        │       │   │   │   ├── MessageCustomRepository.java
        │       │   │   │   └── MessageCustomRepositoryImpl.java
        │       │   │   └── service
        │       │   │       ├── MessageService.java
        │       │   │       ├── MessageServiceImpl.java
        │       │   │       ├── event
        │       │   │       │   ├── MessageCreatedEvent.java
        │       │   │       │   └── MessageEventListener.java
        │       │   │       └── kafka
        │       │   │           └── MessageConsumer.java
        │       │   ├── notification
        │       │   │   ├── controller
        │       │   │   │   ├── NotificationController.java
        │       │   │   │   └── SseController.java
        │       │   │   ├── docs
        │       │   │   │   ├── NotificationControllerDocs.java
        │       │   │   │   └── SseControllerDocs.java
        │       │   │   ├── dto
        │       │   │   │   ├── SortBy.java
        │       │   │   │   ├── SortDirection.java
        │       │   │   │   ├── request
        │       │   │   │   │   └── NotificationSearchCondition.java
        │       │   │   │   └── response
        │       │   │   │       ├── NotificationDto.java
        │       │   │   │       └── NotificationCursorResponse.java
        │       │   │   ├── entity
        │       │   │   │   ├── Notification.java
        │       │   │   │   └── NotificationLevel.java
        │       │   │   ├── event
        │       │   │   │   ├── PersonalNotificationEvent.java
        │       │   │   │   ├── SystemNotificationEvent.java
        │       │   │   │   ├── clothes
        │       │   │   │   ├── feed
        │       │   │   │   ├── follow
        │       │   │   │   ├── message
        │       │   │   │   └── user
        │       │   │   ├── repository
        │       │   │   │   ├── NotificationRepository.java
        │       │   │   │   ├── NotificationCustomRepository.java
        │       │   │   │   ├── NotificationCustomRepositoryImpl.java
        │       │   │   │   └── SseEmitterRepository.java
        │       │   │   └── service
        │       │   │       ├── NotificationService.java
        │       │   │       ├── NotificationServiceImpl.java
        │       │   │       ├── SseService.java
        │       │   │       ├── event
        │       │   │       │   └── NotificationEventListener.java
        │       │   │       └── redis
        │       │   │           ├── BroadcastEvent.java
        │       │   │           ├── PersonalEvent.java
        │       │   │           ├── RedisBroadcastSubscriber.java
        │       │   │           └── RedisPersonalSubscriber.java
        │       │   ├── profile
        │       │   │   ├── controller
        │       │   │   │   └── ProfileController.java
        │       │   │   ├── docs
        │       │   │   │   └── ProfileControllerDocs.java
        │       │   │   ├── dto
        │       │   │   │   ├── request
        │       │   │   │   │   └── ProfileUpdateRequest.java
        │       │   │   │   └── response
        │       │   │   │       ├── ProfileDto.java
        │       │   │   │       └── ProfileLocationDto.java
        │       │   │   ├── entity
        │       │   │   │   ├── Profile.java
        │       │   │   │   ├── Gender.java
        │       │   │   │   └── Location.java
        │       │   │   ├── location
        │       │   │   │   ├── KakaoRegionClient.java
        │       │   │   │   ├── KakaoCoordToRegionResponse.java
        │       │   │   │   ├── ProfileLocationResolver.java
        │       │   │   │   ├── WeatherGridCoordinate.java
        │       │   │   │   └── WeatherGridCoordinateCalculator.java
        │       │   │   ├── repository
        │       │   │   │   └── ProfileRepository.java
        │       │   │   └── service
        │       │   │       ├── ProfileService.java
        │       │   │       └── ProfileServiceImpl.java
        │       │   ├── recommendation
        │       │   │   ├── AiConfig.java
        │       │   │   ├── ai
        │       │   │   │   ├── AiClothesRecommender.java
        │       │   │   │   ├── ClothesRecommendationPrompt.java
        │       │   │   │   ├── ClothesSetResponse.java
        │       │   │   │   ├── PromptClothesInfo.java
        │       │   │   │   ├── UserInfo.java
        │       │   │   │   └── WeatherInfo.java
        │       │   │   ├── controller
        │       │   │   │   └── RecommendationController.java
        │       │   │   ├── docs
        │       │   │   │   └── RecommendationControllerDocs.java
        │       │   │   ├── dto
        │       │   │   │   ├── AttributesDto.java
        │       │   │   │   ├── RecommendationDto.java
        │       │   │   │   └── RecommendedClothes.java
        │       │   │   └── service
        │       │   │       ├── ClothesRecommender.java
        │       │   │       ├── BasicClothesRecommender.java
        │       │   │       ├── RecommendationService.java
        │       │   │       └── RecommendationServiceImpl.java
        │       │   ├── user
        │       │   │   ├── controller
        │       │   │   │   └── UserController.java
        │       │   │   ├── docs
        │       │   │   │   └── UserControllerDocs.java
        │       │   │   ├── dto
        │       │   │   │   ├── request
        │       │   │   │   │   ├── ChangePasswordRequest.java
        │       │   │   │   │   ├── UserCreateRequest.java
        │       │   │   │   │   ├── UserLockUpdateRequest.java
        │       │   │   │   │   └── UserRoleUpdateRequest.java
        │       │   │   │   └── response
        │       │   │   │       ├── UserDto.java
        │       │   │   │       ├── UserDtoCursorResponse.java
        │       │   │   │       ├── UserFeedDto.java
        │       │   │   │       └── UserSummary.java
        │       │   │   ├── entity
        │       │   │   │   ├── User.java
        │       │   │   │   └── UserRole.java
        │       │   │   ├── event
        │       │   │   │   └── UserRoleChangedEvent.java
        │       │   │   ├── repository
        │       │   │   │   ├── UserRepository.java
        │       │   │   │   ├── UserRepositoryCustom.java
        │       │   │   │   ├── UserRepositoryImpl.java
        │       │   │   │   └── UserSearchCondition.java
        │       │   │   └── service
        │       │   │       ├── UserService.java
        │       │   │       └── UserServiceImpl.java
        │       │   └── weather
        │       │       ├── batch
        │       │       │   ├── JobStatus.java
        │       │       │   ├── job
        │       │       │   │   ├── LogUploadConfig.java
        │       │       │   │   ├── WeatherDeleteConfig.java
        │       │       │   │   └── WeatherUpdateConfig.java
        │       │       │   ├── scheduler
        │       │       │   │   ├── BatchScheduler.java
        │       │       │   │   └── SystemScheduler.java
        │       │       │   └── tasklet
        │       │       │       ├── LogUploadTasklet.java
        │       │       │       ├── WeatherDeleteTasklet.java
        │       │       │       └── WeatherUpdateTasklet.java
        │       │       ├── controller
        │       │       │   └── WeatherController.java
        │       │       ├── docs
        │       │       │   └── WeatherControllerDocs.java
        │       │       ├── dto
        │       │       │   ├── request
        │       │       │   │   ├── WeatherApiTestRequest.java
        │       │       │   │   └── WeatherRequest.java
        │       │       │   └── response
        │       │       │       ├── WeatherResponse.java
        │       │       │       ├── SimpleWeatherResponse.java
        │       │       │       ├── TemperatureResponse.java
        │       │       │       ├── HumidityResponse.java
        │       │       │       ├── PrecipitaionResponse.java
        │       │       │       ├── WindSpeedResponse.java
        │       │       │       ├── LocationResponse.java
        │       │       │       ├── KakaoLocationResponse.java
        │       │       │       └── weatherAdministrationApi
        │       │       ├── entity
        │       │       │   ├── Weather.java
        │       │       │   ├── Temperature.java
        │       │       │   ├── Humidity.java
        │       │       │   ├── Precipitation.java
        │       │       │   ├── WindSpeed.java
        │       │       │   ├── AsWord.java
        │       │       │   ├── SkyStatus.java
        │       │       │   └── PrecipitationType.java
        │       │       ├── exception
        │       │       │   ├── WeatherException.java
        │       │       │   ├── WeatherNotFoundException.java
        │       │       │   └── WeatherCategoryNotFoundException.java
        │       │       ├── repository
        │       │       │   ├── WeatherRepository.java
        │       │       │   ├── WeatherRepositoryCustom.java
        │       │       │   └── WeatherRepositoryImpl.java
        │       │       └── service
        │       │           ├── WeatherService.java
        │       │           ├── WeatherServiceImpl.java
        │       │           ├── WeatherScheduler.java
        │       │           ├── WeatherApiCallService.java
        │       │           ├── WeatherApiCallServiceImpl.java
        │       │           ├── LocationApiCallService.java
        │       │           └── LocationApiCallServiceImpl.java
        │       └── global
        │           ├── config
        │           │   ├── AsyncConfig.java
        │           │   ├── BatchConfig.java
        │           │   ├── CacheConfig.java
        │           │   ├── ElasticsearchConfig.java
        │           │   ├── JpaAuditingConfig.java
        │           │   ├── KafkaConfig.java
        │           │   ├── MailConfig.java
        │           │   ├── PasswordConfig.java
        │           │   ├── PropertiesScanConfig.java
        │           │   ├── QueryDslConfig.java
        │           │   ├── RecommendationRedisConfig.java
        │           │   ├── RedisNotificationConfig.java
        │           │   ├── RepositoryConfig.java
        │           │   ├── RestClientConfig.java
        │           │   ├── RetryConfig.java
        │           │   ├── S3Config.java
        │           │   ├── SchedulerConfig.java
        │           │   ├── SecurityConfig.java
        │           │   ├── SwaggerConfig.java
        │           │   ├── WebClientConfig.java
        │           │   ├── WebMvcConfig.java
        │           │   └── WebSocketConfig.java
        │           ├── exception
        │           │   ├── ErrorCode.java
        │           │   ├── ErrorResponse.java
        │           │   ├── GlobalExceptionHandler.java
        │           │   └── WeatherFitException.java
        │           ├── interceptor
        │           │   └── MDCLogInterceptor.java
        │           ├── s3
        │           │   ├── S3Controller.java
        │           │   ├── S3Service.java
        │           │   ├── S3ServiceImpl.java
        │           │   ├── LogS3Service.java
        │           │   ├── S3Scheduler.java
        │           │   ├── docs
        │           │   │   └── S3ControllerDocs.java
        │           │   ├── event
        │           │   ├── eventListener
        │           │   │   └── S3EventListener.java
        │           │   ├── exception
        │           │   ├── properties
        │           │   │   └── S3Properties.java
        │           │   └── util
        │           │       └── S3KeyGenerator.java
        │           ├── security
        │           │   ├── CustomAccessDeniedHandler.java
        │           │   └── CustomAuthenticationEntryPoint.java
        │           └── util
        │               └── ContextCopyingTaskDecorator.java
        └── resources
            ├── application.yml
            ├── application-dev.yml
            ├── application-docker.yml
            └── logback-spring.xml
```
## 🔐 인증 / 인가 구조
- JWT 기반 **Access Token / Refresh Token** 사용
- **Refresh Token은 쿠키 기반**으로 관리
- CSRF 토큰은 쿠키(`XSRF-TOKEN`)와 헤더(`X-XSRF-TOKEN`)를 함께 사용
- 관리자 권한 변경 / 계정 잠금 시 기존 토큰 무효화
- Google / Kakao **OAuth2 로그인** 지원
- 분산 환경에서도 로그인 상태가 유지되도록 인증 저장 구조 개선

---

## 🚀 배포 환경
- **Backend**: AWS ECS
- **Database**: AWS RDS PostgreSQL
- **Cache**: AWS ElastiCache Redis
- **Storage**: AWS S3
- **Load Balancer**: ALB
- **Container Registry**: AWS ECR
- **CI/CD**: GitHub Actions
- **Monitoring**: CloudWatch, Prometheus

---

## 📊 다이어그램

### ERD
<img width="6724" height="2256" alt="ERD다이어그램" src="https://github.com/user-attachments/assets/21a928a6-7464-460e-a12e-41b8861df4ae" />

### 클래스 다이어그램
<img width="4482" height="1598" alt="클래스 다이어그램" src="https://github.com/user-attachments/assets/9eaa7e23-8753-4607-9849-31cbcbeb79be" />

### 인프라 / 배포 다이어그램
<img width="2192" height="1295" alt="배포 다이어그램" src="https://github.com/user-attachments/assets/f1e3e422-63bd-4bea-aaa7-77a304ce3e0d" />

---

## 🌍 서비스 주소
- [WeatherFit](https://www.weatherfit.cloud/)

---

## 📷 시연 자료

### 팀 발표 영상
- 발표 영상 링크 추가

### 팀 발표 자료
- 발표 자료 링크 추가

---

## 📌 프로젝트 회고
- 프로젝트 회고 링크 추가

