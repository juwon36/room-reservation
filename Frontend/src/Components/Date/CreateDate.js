import { IoIosArrowForward } from "react-icons/io";
import { IoIosArrowBack } from "react-icons/io";

function CreateDate({ dateData, state }) {
  let stateSpan = {
    pre: (
      <span className="CreateDate-pre">
        <IoIosArrowBack />
        {/* <FontAwesomeIcon icon={faArrowCircleLeft} /> */}
        <span>{dateData}</span>
      </span>
    ),
    today: (
      <span className="CreateDate-today">
        <span className="WebTodayDate">{dateData[0]}</span>
        <strong> {dateData[1]}</strong>
        <span className="MobileTodayDate">{dateData[2]}</span>
      </span>
    ),
    post: (
      <span className="CreateDate-post">
        <span>{dateData}</span>
        <IoIosArrowForward />
      </span>
    ),
  };

  return <div className="CreateDate">{stateSpan[state]}</div>;
}

export default CreateDate;
