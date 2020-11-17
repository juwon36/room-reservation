import React from "react";
import { BrowserRouter, Route } from "react-router-dom";
import Daily from "./Routes/Daily";
import Monthly from "./Routes/Monthly";
import TitleBox from "./Components/Title/TitleBox";
import "./App.scss";

function App() {
  return (
    <BrowserRouter>
      <div className="App">
        <TitleBox />
        <Route path="/" exact={true} component={Daily} />
        <Route path="/month" component={Monthly} />
        {/* <Route path="/reserve" component={} />
      <Route path="/update" component={} /> */}
      </div>
    </BrowserRouter>
  );
}

export default App;
