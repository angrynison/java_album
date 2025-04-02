# 앨범 프로젝트
사용자의 로컬 이미지 파일을 저장하고 관리하는 JAVA와 MySQL기반 프로젝트입니다.

## 기능 
이미지 추가
이미지 조회
이미지 검색
이미지 정렬
이미지 관

## 필수 구성 요소
- Java 8 이상 설치
- MySQL 서버 설치 및 실행

## 설치 방법 & 실행 

```
git clone https://github.com/angrynison/food_recommendation.js.git](https://github.com/angrynison/java_album.git
```

의존성 설치

```
mvn install
```

데이터베이스 연결 설정 수정:
Database.java 파일에서 다음 정보 수정

```
String url = "jdbc:mysql://localhost:3306/testdb?serverTimezone=UTC";
connection = DriverManager.getConnection(url, "root", "1234"); // 비밀번호 변경
```
