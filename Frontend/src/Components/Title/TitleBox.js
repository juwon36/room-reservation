import React, { useState } from "react";
import moment from "moment";
import "moment/locale/ko";
import useInterval from "./useInterval";

function TitleBox() {
  const nowTime = moment().format("HH:mm:ss");
  const [seconds, setSeconds] = useState(nowTime);

  // useInterval
  useInterval(() => {
    setSeconds(moment().format("HH:mm:ss"));
  }, 1000);

  return (
    <div className="bd TitleBox">
      <h1>RadiusLab 회의실 예약</h1>
      <div className="NowTime">{seconds}</div>
    </div>
  );
}

export default TitleBox;
