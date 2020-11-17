const roomsDB = [
  {
    roomName: "오픈형",
    roomInfo: {
      maximum: 11,
      board: true,
      beamProjector: true,
      Adaptor: 4,
    },
  },
  {
    roomName: "1회의실",
    roomInfo: {
      maximum: 6,
      board: true,
      beamProjector: true,
      Adaptor: 3,
    },
  },
  {
    roomName: "2회의실",
    roomInfo: {
      maximum: 6,
      board: true,
      beamProjector: false,
      Adaptor: 4,
    },
  },
  {
    roomName: "대회의실",
    roomInfo: {
      maximum: 23,
      board: true,
      beamProjector: true,
      Adaptor: 8,
    }
  }
];

export default roomsDB;
