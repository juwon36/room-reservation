# 회의실 예약 프로그램

사내 회의실 (오픈회의실, 1회의실, 2회의실, 대회의실) 예약 프로그램

<br>

## 개발 환경
- Spring Boot
- React
- postgreSQL

<br>
   
## API 목록
| 기능          | 메소드 | url                      | param                                                                                                 |
| ------------- | ------ | ------------------------ | ----------------------------------------------------------------------------------------------------- |
| 예약조회      | GET    | /api/reserve             | roomId, yearMonth, reserveDate                                                                        |
| 예약하기      | POST   | /api/reserve             | roomId, userName, userEmail, userPassword, userNum, title, reserveDate, startTime, endTime            |
| 예약수정      | PUT    | /api/reserve/{ReserveId} | reserveID, roomId, userName, userEmail, userPassword, userNum, title, reserveDate, startTime, endTime |
| 예약취소      | DELETE | /api/reserve/{ReserveId} | reserveId, userPassword                                                                               |
| 비밀번호 확인 | POST   | /api/reserve/checkpw     | reserveId, userPassword                                                                               |
| 회의실 조회   | GET    | /api/room                |

<br>






   

