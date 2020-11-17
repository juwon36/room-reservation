import CreateTimeTd from "./CreateTimeTd";
import hours from "../../../../DB/hoursDB";

function CreateTimeTable() {
  return (
    <table className="CreateTimeTable">
      <tbody>
        <tr>
          {hours.map((hour, i) => (
            <CreateTimeTd id={i} key={`ctTd${i}`} hour={hour} />
          ))}
        </tr>
      </tbody>
    </table>
  );
}

export default CreateTimeTable;
