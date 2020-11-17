import CreateRoomInfoBox from "./Info/CreateRoomInfoBox";
import CreateTableBox from "./Table/CreateTableBox";

function CreateRoomBox({ id, roomDB }) {
  return (
    <div id={id} key={id} className="bd CreateRoomBox">
      <div className="RoomNameBox">
        <h3 className="bd RoomName">{roomDB.roomName} 회의실</h3>
      </div>
      <CreateRoomInfoBox roomInfo={roomDB.roomInfo} />
      <CreateTableBox />
    </div>
  );
}

export default CreateRoomBox;
