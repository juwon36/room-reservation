import CreateDate from "./CreateDate";
import moment from "moment";
import { IoIosCalendar } from "react-icons/io";

function DateBox() {
  const today = moment();
  const webToday = today.format("YYYY년 MM월 DD일");
  const mobileToday = today.format("MM / DD");
  const day = today.format("dddd");

  const yesterday = moment().subtract(1, "days").format("MM / DD");
  const tomorrow = moment().add(1, "days").format("MM / DD");

  return (
    <div className="bd DateBox">
      <CreateDate dateData={yesterday} state="pre" />
      <div></div>
      <CreateDate dateData={[webToday, day, mobileToday]} state="today" />
      <div className="moveMonthButtonBox">
        <div className="moveMonthButton">
          <IoIosCalendar />
          <span>월별 보기</span>
        </div>
      </div>
      <CreateDate dateData={tomorrow} state="post" />
    </div>
  );
}

export default DateBox;
