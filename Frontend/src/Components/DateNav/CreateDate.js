import { IoIosArrowForward } from "react-icons/io";
import { IoIosArrowBack, IoIosArrowDropleftCircle, IoIosArrowDroprightCircle } from "react-icons/io";

function CreateDate({ data, state, onClick }) {
  let stateSpan = {
    pre:
      <span className="CreateDate-pre">
        {/* <IoIosArrowBack onClick={onClick} /> */}
        <IoIosArrowDropleftCircle onClick={onClick} />
        <span>{data}</span>
      </span>,
    today:
      <span className="CreateDate-today">
        <span className="WebTodayDate">{data[0]}</span>
        <strong> {data[2]} </strong >
        <span className="MobileTodayDate">{data[1]}</span>
      </span>
    ,
    post:
      <span className="CreateDate-post">
        <span>{data}</span>
        <IoIosArrowDroprightCircle onClick={onClick} />
      </span>
  }


  return (
    <div className="CreateDate">
      {
        stateSpan[state]
      }
    </div>
  );
}

export default CreateDate;