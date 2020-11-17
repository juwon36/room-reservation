import CreateRoomBox from "./CreateRoomBox";
import roomsDB from "../../DB/roomsDB";

function ConferenceRoomsBox() {
  return (
    <div className="bd ConferenceRoomsBox">
      {roomsDB.map((db, i) => (
        <CreateRoomBox id={i} key={i} roomDB={db} />
      ))}
    </div>
  );
}

export default ConferenceRoomsBox;
