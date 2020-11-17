import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser } from "@fortawesome/free-solid-svg-icons";
import { faPen } from "@fortawesome/free-solid-svg-icons";
import { faVideo } from "@fortawesome/free-solid-svg-icons";

function CreateRoomInfoBox({ roomInfo }) {
  const MaximumBox = () => (
    <div>
      <FontAwesomeIcon icon={faUser} />
      <span>최대 {roomInfo.maximum}명</span>
    </div>
  );

  const BoardBox = () => (
    <div>
      <FontAwesomeIcon icon={faPen} />
      <span>보드</span>
    </div>
  );

  const BeamProjectorBox = () => (
    <div>
      <FontAwesomeIcon icon={faVideo} />
      <span>빔프로젝터</span>
    </div>
  );

  return (
    <div className="bd CreateRoomInfoBox">
      <div className="bd RoomInfoAlignBox">
        <MaximumBox />
        {roomInfo.board && <BoardBox />}
        {roomInfo.beamProjector && <BeamProjectorBox />}
      </div>
    </div>
  );
}

export default CreateRoomInfoBox;
