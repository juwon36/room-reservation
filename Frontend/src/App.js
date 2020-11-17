import "./App.scss";
import TitleBox from "./Components/Title/TitleBox";
import DateBox from "./Components/Date/DateBox";
import ConferenceRoomsBox from "./Components/ConferenceRoom/ConferenceRoomsBox";

function App() {
  return (
    <div className="App bd">
      <TitleBox />
      <DateBox />
      <ConferenceRoomsBox />
    </div>
  );
}

export default App;
