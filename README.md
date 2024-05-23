# ✈️ Travel Mate 
사용자의 현재 위치나 원하는 위치를 기반으로 누구나 쉽게 여행 정보를 얻을 수 있도록 도와주고, <br>
일정표를 작성하는 기능을 제공하는 서비스입니다.

## ✈️ 프로젝트 기간 : 2024.05.15 ~ 2024.06.17
<a href="https://docs.google.com/spreadsheets/d/1PXe3l0eFhTbXUCP5dBkVUeoWb218ewFu2p-qeqo4YDE/edit?usp=sharing" target="_blank">일정표 링크</a>

## ✈️ 프로젝트 기능 및 설계
### 1. 회원 관리
- 회원 가입 기능
: 이메일, 비밀번호, 이름, 생년월일 정보 입력, 이메일 인증 (JavaMailSender)
- 소셜 로그인 연동 (Naver 또는 Kakao)
- 회원 로그인/로그아웃: JWT를 이용한 권한 관리 (Spring Security, OAuth 2.0, JWT)
- 비밀번호 찾기: 이름, 생년월일 입력 후 임시 비밀번호 발급 -> 바로 비밀번호를 변경할 수 있는 링크 같이 보내주기
- 회원 정보 수정: 비밀번호 변경, 회원 탈퇴 기능(모든 정보 삭제)

### 2. 여행 정보 조회 (OpenAPI 활용)
- 공통 기능: 원하는 여행지 정보를 장바구니처럼 담아두는 기능 -> favorite table에 저장 <br>
- 위치 정보 기준 조회: 현재 위치 또는 입력받은 위치 근처의 여행 정보 제공 (Geolocation API, Naver Maps Geocoding API)
- 키워드로 여행 정보 조회: Elastic Search를 활용한 검색 기능 구현
- 자주 조회된 여행 정보 캐시에 저장 (Redis)
<br>

<OpenAPI에서 저장할 정보> 
- 지역 코드를 먼저 Area_code 테이블에 저장 <br>
- TourInfo : 저장된 지역코드 전체의 여행 정보를 저장한 테이블 <br>

⬇️ TourInfo 테이블에 저장되는 컬럼 정보
1. 관광지 ID (tour_id, 기본 키)
2. 지역 코드 (area_code, 외래 키로 AreaCode 테이블 참조)
3. 시군구 코드 (sigungu_code)
4. 제목 (title)
5. 주소 1 (addr1)
6. 주소 2 (addr2)
7. 콘텐츠 유형 ID (content_type_id)
8. 콘텐츠 ID (content_id)
9. 지도 X 좌표 (mapx)
10. 지도 Y 좌표 (mapy)
11. 이미지 URL 1 (image_url1)
12. 이미지 URL 2 (image_url2)
13. 생성 시간 (created_time) -> OpenAPI에서 저장된 정보 생성 날짜
14. 수정 시간 (modified_time) -> OpenAPI에서 저장된 정보 수정 날짜
15. 생성일 (created_at) -> DB에 저장된 데이터 생성 날짜
16. 수정일 (updated_at) -> DB에 저장된 데이터 수정 날짜

### 3. 일정표
- 일정 생성: 여행 일자, 시작/종료 시간, 컨텐츠(Type), 메모 입력하여 일정 추가
- 일정 조회: 선택 일자의 일정을 시간 순으로 나열 -> 시간별로 나열해서 보여주기 위해 PlanDetail 테이블 생성
- 일정 수정/삭제: 저장된 일정 수정 및 삭제 기능

위의 기능들을 Ver.1로 구현할 예정입니다.

## ✈️ ERD
<img width="100%" src="https://github.com/ESJung95/TravelMate/assets/155522048/13010969-6702-4114-8c7b-e7bd1506271c"/>

## ✈️ Tech Stack
<div align=center> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">
  <img src="https://img.shields.io/badge/OAuth_2.0-3C3C3D?style=for-the-badge&logo=oauth&logoColor=white">
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jwt&logoColor=white">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"> 
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=Elasticsearch&logoColor=white">
  <img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">
</div>


## ✈️ 개발 환경
- IDE : IntelliJ Ultimate
- Framework : Spring Boot 3.2.5
- Build Tool : Gradle
- Language : Java 17
- DataBase : Redis, MySQL (JPA), Elasticsearch
- 라이브러리 : Lombok, JJWT, SpringDoc OpenAPI(Swagger), MySQL Connector/J, JUnit5, Mockito

## ✈️ Flow
### 1. 회원 관리 플로우
<img width="80%" src="https://github.com/ESJung95/TravelMate/assets/155522048/dd82ccce-7173-436b-b726-39e3a950daeb"/>

### 2. 여행 정보 조회 플로우
<img width="60%" src="https://github.com/ESJung95/TravelMate/assets/155522048/bb2515c8-a1b1-4b26-a40d-59480804438f"/>

### 3. 일정표 플로우
<img width="80%" src="https://github.com/ESJung95/TravelMate/assets/155522048/cee23900-bdda-45af-a483-8a76f2a0d3a7"/>

## ✈️ 소프트웨어 아키텍처 다이어그램 (추가 예정)

## ✈️ 개선 사항 (프로젝트 마무리 후 작성 예정)
## ✈️ 프로젝트 후 느낀점 (프로젝트 마무리 후 작성 예정)
