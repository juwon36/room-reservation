import { useRef, useEffect } from "react";

export default function useInterval(callback, delay) {
  const savedCallback = useRef();

  useEffect(() => {
    // useEffect에 매개변수로 받은 콜백을 현재 Ref로 선언해준다.
    savedCallback.current = callback;
  });

  useEffect(() => {
    function tick() {
      savedCallback.current();
    }
    // useEffect에 Ref의 current를 setInterval를 delay 시간동안 해준다.
    let id = setInterval(tick, delay);
    // 언마운트되기전 clearInterval을 해준다.
    return () => clearInterval(id);
  }, [delay]);
}
