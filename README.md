# UPDA
## 🌱 Up-Cycling Funding Platform

친환경 제품 제작을 위한 재료 기부 및 펀딩 플랫폼

## 📝 프로젝트 소개

UPDA는 환경 보호에 관심이 있는 창작자와 후원자를 연결하는 플랫폼입니다. 
재활용 가능한 재료를 기부하고, 이를 활용한 제품 제작을 후원하며, 완성된 제품을 리워드로 받을 수 있습니다.

### 주요 기능
- 🔄 재활용 재료 기부 시스템
- 💰 제품 제작 펀딩
- 🎁 리워드 배송
- 💬 실시간 채팅 문의(예정)
- 🏆 환경 보호 활동 업적 시스템

## 🛠 기술 스택

### Backend
- Java 17
- Spring Boot 3.4.1
- Spring Security
- Spring Data JPA + Hibernate
- MySQL 8.0

### Frontend
- Thymeleaf
- Tailwind CSS

### DevOps
- Docker
- GitHub Actions

## 📋 ERD
![ERD 다이어그램](https://cdn.discordapp.com/attachments/1329368511062999135/1336554790896799804/erd111.PNG?ex=67a98126&is=67a82fa6&hm=6761733a736eeda1eb9edd3a4fba6a24547073fdb636abe9af7f64ac32997760&)

## API
> OAuth2
- KAKAO
https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
- NAVER
https://developers.naver.com/docs/login/api/api.md
- GOOGLE
https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko#httprest_2
> Delivery
- Trancking
https://tracker.delivery/
> 공공 데이터
- 사업자 등록 확인
https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15081808
- 통신판매업 등록 확인
https://www.data.go.kr/data/15126311/openapi.do
