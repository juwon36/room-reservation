function CreateTimeTd({ id, hour }) {
  return (
    <td id={`ctTd${id}`} className="CreateTimeTd">
      {hour}
    </td>
  );
}

export default CreateTimeTd;
