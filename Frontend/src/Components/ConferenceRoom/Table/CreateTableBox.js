import CreateReserveTable from "./ReserveTable/CreateReserveTable";
import CreateTimeTable from "./TimeTable/CreateTimeTable";

function CreateTableBox({ roomName }) {
  return (
    <div className="bd CreateTableBox">
      <CreateReserveTable />
      <CreateTimeTable />
    </div>
  );
}

export default CreateTableBox;
