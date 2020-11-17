const roomsDB = [
  {
    roomName: "A",
    roomInfo: {
      maximum: 5,
      board: true,
      beamProjector: true,
    },
  },
  {
    roomName: "B",
    roomInfo: {
      maximum: 8,
      board: true,
      beamProjector: true,
    },
  },
  {
    roomName: "C",
    roomInfo: {
      maximum: 8,
      board: true,
      beamProjector: false,
    },
  },
  {
    roomName: "D",
    roomInfo: {
      maximum: 10,
      board: false,
      beamProjector: true,
    },
  },
  {
    roomName: "E",
    roomInfo: {
      maximum: 20,
      board: false,
      beamProjector: false,
    },
  },
  {
    roomName: "F",
    roomInfo: {
      maximum: 20,
      board: false,
      beamProjector: false,
    },
  },
];

export default roomsDB;
