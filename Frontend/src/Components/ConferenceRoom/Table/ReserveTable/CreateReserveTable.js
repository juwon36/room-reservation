import CreateReserveTd from "./CreateReserveTd";

function CreateReserveTable() {
  return (
    <table className="CreateReserveTable">
      <tbody>
        <tr>
          {[...Array(26).keys()].map((i) => (
            <CreateReserveTd id={i} key={`crTd${i}`} />
          ))}
        </tr>
      </tbody>
    </table>
  );
}

export default CreateReserveTable;
