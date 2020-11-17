import React from "react";
import DateBox from "../Components/DateNav/DateBox";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";

function Monthly() {
  return (
    <>
      <DateBox />
      <FullCalendar plugins={[dayGridPlugin]} initialView="dayGridMonth" />
    </>
  );
}

export default Monthly;
