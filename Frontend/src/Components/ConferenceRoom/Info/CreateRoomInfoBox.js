import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser, faPen, faTv, faPlug } from "@fortawesome/free-solid-svg-icons";

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
      <FontAwesomeIcon icon={faTv} />
      <span>TV</span>
    </div>
  );
  const AdaptorBox = () => (
    <div>
      <FontAwesomeIcon icon={faPlug} />
      <span>콘센트 {roomInfo.Adaptor}구</span>
    </div>
  );


  return (
    <div className="bd CreateRoomInfoBox">
      <div className="bd RoomInfoAlignBox">
        <MaximumBox />
        {roomInfo.board && <BoardBox />}
        {roomInfo.beamProjector && <BeamProjectorBox />}
        <AdaptorBox />
      </div>
    </div>
  );
}

export default CreateRoomInfoBox;
