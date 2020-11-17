import React, { useState } from "react";
import dayjs from 'dayjs';
import 'dayjs/locale/ko'
import useInterval from "./useInterval";

function TitleBox() {
  const nowTime = dayjs().format("HH:mm:ss");
  const [seconds, setSeconds] = useState(nowTime);

  // useInterval
  useInterval(() => {
    setSeconds(dayjs().format("HH:mm:ss"));
  }, 1000);

  return (
    <div className="bd TitleBox">
      <h1>RadiusLab 회의실 예약</h1>
      <div className="NowTime">{seconds}</div>
    </div>
  );
}

export default TitleBox;
