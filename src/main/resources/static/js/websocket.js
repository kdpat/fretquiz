import {Client} from "@stomp/stompjs";

const brokerURL = "ws://localhost:8080/ws";

function onConnect(frame) {
  console.log("ws connected:", frame);
}

function onDisconnect(frame) {
  console.log("ws disconnected:", frame);
}

function onStompError(frame) {
  console.log("stomp error:", frame);
}

export function makeStompClient() {
  return new Client({
    brokerURL,
    onConnect,
    onDisconnect,
    onStompError,
  });
}
