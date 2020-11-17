import { useEffect, useState } from "react";
import CreateDate from "./CreateDate";
import dayjs from "dayjs";
import "dayjs/locale/ko";
import { IoIosCalendar } from "react-icons/io";
// I18n
dayjs.locale("ko");

function goCalendar(e) {
  alert("Clicked calendar!");
}

function DateBox() {
  // 오늘 날짜를 state값으로 설정
  const [todayDate, setTodayDate] = useState(dayjs());
  console.log(todayDate.format("YYYY년 MM월 DD일"));

  useEffect(() => {
    console.log("useEffect 사용하기");
  }, [todayDate]);

  const calcDay = (m) => {
    return todayDate.add(m, "days").format("MM/DD ddd");
  };

  const moveDay = (m) => {
    //let date = todayDate.clone();
    setTodayDate(todayDate.add(m, "days"));
    console.log(todayDate.format("YYYY년 MM월 DD일"));
  };

  return (
    <div className="bd DateBox">
      <CreateDate data={calcDay(-1)} state="pre" onClick={() => moveDay(-1)} />
      <div></div>
      <CreateDate
        data={[
          todayDate.format("YYYY년 MM월 DD일"),
          todayDate.format("MM / DD"),
          todayDate.format("ddd"),
        ]}
        state="today"
      />
      <div className="moveMonthButtonBox">
        <div className="moveMonthButton">
          <IoIosCalendar />
          <span onClick={goCalendar}>월별 보기</span>
        </div>
      </div>
      <CreateDate data={calcDay(1)} state="post" onClick={() => moveDay(1)} />
    </div>
  );
}

export default DateBox;
