/*
 * main.js
 * 이 파일은 웹소켓 연결, 채팅방 구독, 채팅방 목록 렌더링, 친구 선택 및 초대 등의 기능을 처리합니다.
 */

const websocketUrl = "http://localhost:8080/connect";

const userId = document.body.getAttribute('data-user-id');

/**
 * 웹소켓 구독 객체를 저장하는 변수 (key: roomId, value: subscription 객체)
 */
const chatRoomSubscriptions = {};

/**
 * 채팅방 목록 정보를 저장하는 배열
 */
let chatRooms = [];

/** DOMContentLoaded 이벤트: 웹소켓 연결 및 친구 목록 가져오기 초기화 */
document.addEventListener("DOMContentLoaded", function () {

    // SockJS와 STOMP를 이용하여 웹소켓 연결 초기화
    const socket = new SockJS(websocketUrl);
    const stompClient = Stomp.over(socket);

    // STOMP 디버그 메시지 출력
    stompClient.debug = function (str) {
        console.log("STOMP DEBUG:", str);
    };

    // 웹소켓 연결 및 초기 구독 설정
    stompClient.connect({}, function () {
        console.log("WebSocket 연결 성공");

        // 초기 채팅방 목록 요청
        stompClient.send('/room-pub/rooms/init', {}, {});

        // 사용자 개인별 채팅방 미리보기 구독
        stompClient.subscribe(`/room-sub/user/${userId}`, function (messageOutput) {
            const response = JSON.parse(messageOutput.body);
            const chatRoomIds = response.rooms;

            // 각 채팅방에 대해 구독이 없으면 새로 구독
            chatRoomIds.forEach(roomId => {
                if (!chatRoomSubscriptions[roomId]) {
                    chatRoomSubscriptions[roomId] = stompClient.subscribe(`/room-sub/room/${roomId}`, function (messageOutput) {
                        const roomInfo = JSON.parse(messageOutput.body);

                        // 채팅방 UI 업데이트
                        addOrUpdateChatRoom(roomInfo);
                        console.log("채팅방 업데이트:", roomInfo);

                        // 신규 채팅방인 경우 배열에 추가 후 목록 렌더링
                        if (!chatRooms.some(room => room.roomId === roomInfo.roomId)) {
                            chatRooms.push(roomInfo);
                            renderChatList();
                        }
                    });
                    console.log(`Subscribed to chat room: ${roomId}`);
                }
            });
        });
        renderChatList();
    });

    // 친구 목록 가져오기 (채팅방 생성 및 초대에 사용)
    fetchAllFriends();
});

/** 채팅방 목록을 표시할 DOM 요소 참조 */
const chatList = document.getElementById("chat-list");

/**
 * 채팅방 정보를 추가하거나 업데이트하고 UI에 반영합니다.
 * @param {Object} room - 채팅방 객체 (roomId, roomName, latestMessage, latestTimestamp 등 포함)
 */
function addOrUpdateChatRoom(room) {
    let roomElement = document.getElementById(`room-${room.roomId}`);

    if (roomElement) {
        // 기존 채팅방의 정보 업데이트
        roomElement.querySelector(".room-title").textContent = room.roomName;
        roomElement.querySelector(".last-message").textContent = room.latestMessage;
    } else {
        // 새로운 채팅방 항목 생성 (Bootstrap List Group Item 활용)
        const li = document.createElement("li");
        li.className = "list-group-item d-flex justify-content-between align-items-center";
        li.id = `room-${room.roomId}`;
        li.innerHTML = `
      <div class="d-flex align-items-center">
        <img src="favicon.ico" alt="Profile" class="rounded-circle me-2" style="width:40px; height:40px; background-color:#DDD;">
        <div>
          <div class="room-title fw-bold text-primary">${room.roomName}</div>
          <div class="last-message">${room.latestMessage}</div>
        </div>
      </div>
      <div class="d-flex flex-column text-end">
        <div class="timestamp">${room.latestTimestamp}</div>
        <div class="mt-2">
          <button class="btn btn-warning btn-sm me-1" onclick="addFriends('${room.roomId}')">친구 초대</button>
          <button class="btn btn-danger btn-sm" onclick="exitChatRoom('${room.roomId}')">나가기</button>
        </div>
      </div>
    `;
        chatList.appendChild(li);
    }

    // 배열 업데이트: 기존 채팅방이면 수정, 아니면 추가
    const existingIndex = chatRooms.findIndex(r => r.roomId === room.roomId);
    if (existingIndex !== -1) {
        chatRooms[existingIndex] = room;
    } else {
        chatRooms.push(room);
    }

    // 최신 메시지 순으로 정렬 후 UI 렌더링
    chatRooms.sort((a, b) => new Date(b.latestTimestamp) - new Date(a.latestTimestamp));
    renderChatList();
}

/**
 * 채팅방 목록을 UI에 다시 렌더링합니다.
 */
function renderChatList() {
    chatList.innerHTML = "";
    chatRooms.forEach(room => {
        const li = document.createElement("li");
        li.className = "list-group-item d-flex justify-content-between align-items-center";
        li.id = `room-${room.roomId}`;
        li.innerHTML = `
      <div class="d-flex align-items-center">
        <img src="favicon.ico" alt="Profile" class="rounded-circle me-2" style="width:40px; height:40px; background-color:#DDD;">
        <div>
          <div class="room-title fw-bold text-primary">${room.roomName}</div>
          <div class="last-message">${room.latestMessage}</div>
        </div>
      </div>
      <div class="d-flex flex-column text-end">
        <div class="timestamp">${formatChatTimestamp(room.latestTimestamp)}</div>
        <div class="mt-2">
          <button class="btn btn-warning btn-sm me-1" onclick="addFriends('${room.roomId}')">친구 초대</button>
          <button class="btn btn-danger btn-sm" onclick="exitChatRoom('${room.roomId}')">나가기</button>
        </div>
      </div>
    `;
        chatList.appendChild(li);
    });
}

/**
 * 채팅방의 타임스탬프를 포맷팅합니다.
 * @param {string} timestamp - 채팅방 데이터의 타임스탬프 문자열
 * @returns {string} 포맷팅된 타임스탬프
 */
function formatChatTimestamp(timestamp) {
    // 필요에 따라 타임스탬프 포맷을 변경할 수 있음.
    return timestamp;
}

/**
 * 서버에서 전체 친구 목록을 가져옵니다.
 */
async function fetchAllFriends() {
    try {
        const response = await fetch(`/api/friends/${userId}`);
        if (!response.ok) throw new Error('Failed to fetch friends');
        friends = await response.json();
        console.log(friends);
    } catch (error) {
        console.error('Error fetching friends:', error);
    }
}

/** "새로 만들기" 버튼 DOM 요소 참조 */
const createRoomBtn = document.getElementById("create-new-room");

/** 모달 내 확인 버튼 DOM 요소 참조 */
const confirmBtn = document.getElementById("confirm-create-room");

/** 모달 내 친구 목록 DOM 요소 참조 */
const friendList = document.getElementById("friend-list");

/**
 * 친구 선택 시 선택된 친구의 ID를 저장하는 Set 객체
 */
const selectedFriends = new Set();

/**
 * 서버에서 받아온 친구 객체들을 저장하는 배열
 */
let friends = [];

/**
 * 모달에 친구 목록을 렌더링합니다.
 */
function renderFriendList() {
    friendList.innerHTML = "";
    friends.forEach(friend => {
        const li = document.createElement("li");
        li.className = "list-group-item d-flex align-items-center";
        li.dataset.id = friend.id;
        li.innerHTML = `
      <input type="hidden" value="${friend.id}">
      <img src="${friend.image}" alt="${friend.name}" class="rounded-circle me-2" width="40">
      <span>${friend.name}</span>
    `;
        li.addEventListener("click", () => toggleFriendSelection(li, friend.id));
        friendList.appendChild(li);
    });
}

/**
 * 친구 항목의 선택/해제 상태를 토글합니다.
 * @param {HTMLElement} element - 친구 항목 DOM 요소
 * @param {string} friendId - 친구의 ID
 */
function toggleFriendSelection(element, friendId) {
    if (selectedFriends.has(friendId)) {
        selectedFriends.delete(friendId);
        element.classList.remove("active");
    } else {
        selectedFriends.add(friendId);
        element.classList.add("active");
    }
}
const friendModalEl = document.getElementById('friendModal');

/**
 * "새로 만들기" 버튼 클릭 시, 친구 선택 모달을 엽니다.
 */
createRoomBtn.addEventListener("click", function () {
    confirmBtn.dataset.action = "create"; // 신규 채팅방 생성 액션 설정
    delete confirmBtn.dataset.roomId; // 초대용 roomId 제거
    renderFriendList();
    const friendModal = new bootstrap.Modal(friendModalEl);
    friendModal.show();
});

/**
 * 모달 취소 또는 닫기 이벤트:
 * 선택된 친구 배열 초기화
 */
friendModalEl.addEventListener('hidden.bs.modal', function (event) {
    selectedFriends.clear();
});

/**
 * 모달 확인 버튼 클릭 이벤트 처리:
 * 신규 채팅방 생성 또는 기존 채팅방에 친구 초대 기능 수행
 */
confirmBtn.addEventListener("click", function () {
    const action = confirmBtn.dataset.action;
    const roomId = confirmBtn.dataset.roomId;
    const selectedIds = Array.from(selectedFriends);
    if (selectedIds.length === 0) {
        alert("최소 한 명 이상 선택하세요.");
        return;
    }
    if (action === "create") {
        createChatRoom(selectedIds);
    } else if (action === "invite" && roomId) {
        inviteFriendsToRoom(roomId, selectedIds);
    }
    // 모달 닫기
    document.getElementById("friendModal").querySelector(".btn-close").click();
});

/**
 * 선택된 친구들을 초대하여 신규 채팅방을 생성합니다.
 * @param {Array} invited - 초대할 친구 ID 배열
 */
async function createChatRoom(invited) {
    const response = await fetch('/api/rooms', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({invitedIds: invited})
    });
    if (response.ok) {
        const newRoom = await response.json();
        const newRoomId = newRoom.roomId;
        console.log("채팅방 생성 완료:", newRoomId);
        renderChatList();
    } else {
        alert("채팅방 생성에 실패했습니다.");
    }
}

/**
 * 지정된 채팅방에 포함되지 않은 친구 목록을 서버에서 가져옵니다.
 * @param {string} roomId - 채팅방 ID
 * @returns {Array} 초대 가능한 친구 목록
 */
async function fetchFriendsNotInRoom(roomId) {
    try {
        const response = await fetch(`/api/rooms/${roomId}/friends`);
        if (!response.ok) throw new Error('친구 목록을 가져오는데 실패했습니다.');
        const friendsNotInRoom = await response.json();
        return friendsNotInRoom;
    } catch (error) {
        console.error("Error fetching friends:", error);
        return [];
    }
}

/**
 * 지정된 채팅방에 친구를 초대하기 위해, 친구 선택 모달을 엽니다.
 * @param {string} roomId - 초대할 채팅방 ID
 */
async function addFriends(roomId) {
    confirmBtn.dataset.action = "invite";
    confirmBtn.dataset.roomId = roomId;
    const friendsNotInRoom = await fetchFriendsNotInRoom(roomId);
    console.log(friendsNotInRoom);
    selectedFriends.clear();
    renderFriendListNotInRoom(friendsNotInRoom);
    const friendModal = new bootstrap.Modal(document.getElementById("friendModal"));
    friendModal.show();
}

/**
 * 채팅방에 없는 친구 목록을 모달에 렌더링합니다.
 * @param {Array} friendsList - 초대 가능한 친구 배열
 */
function renderFriendListNotInRoom(friendsList) {
    friendList.innerHTML = "";
    friendsList.forEach(friend => {
        const li = document.createElement("li");
        li.className = "list-group-item d-flex align-items-center";
        li.dataset.id = friend.id;
        li.innerHTML = `
      <input type="hidden" value="${friend.id}">
      <img src="${friend.image}" alt="${friend.name}" class="rounded-circle me-2" width="40">
      <span>${friend.name}</span>
    `;
        li.addEventListener("click", () => toggleFriendSelection(li, friend.id));
        friendList.appendChild(li);
    });
}

/**
 * 선택된 친구들을 지정된 채팅방에 초대합니다.
 * @param {string} roomId - 채팅방 ID
 * @param {Array} selectedIds - 초대할 친구 ID 배열
 */
async function inviteFriendsToRoom(roomId, selectedIds) {
    try {
        const response = await fetch(`/api/rooms/${roomId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ invitedIds: selectedIds })
        });
        if (response.ok) {
            alert("친구 초대 완료!");
            document.getElementById("friendModal").querySelector(".btn-close").click();
        } else {
            alert("친구 초대에 실패했습니다.");
        }
    } catch (error) {
        console.error("친구 초대 오류:", error);
        alert("친구 초대 중 오류가 발생했습니다.");
    }
}

/**
 * 채팅방을 나가는 기능.
 * 서버에 DELETE 요청 후 UI 갱신 및 웹소켓 구독 해제.
 * @param {string} roomId - 나갈 채팅방 ID
 */
window.exitChatRoom = async function (roomId) {
    const room = chatRooms.find(room => room.roomId === roomId);
    if (!room) return;
    const isConfirmed = window.confirm(`"${room.roomName}" 채팅방에서 나가시겠습니까?`);
    if (!isConfirmed) return;
    try {
        const response = await fetch(`/api/rooms/${roomId}`, {method: 'DELETE'});
        if (response.ok) {
            console.log(`✅ "${room.roomName}" 채팅방에서 퇴장 완료: ${roomId}`);
            chatRooms = chatRooms.filter(r => r.roomId !== roomId);
            renderChatList();
            if (chatRoomSubscriptions[roomId]) {
                chatRoomSubscriptions[roomId].unsubscribe();
                delete chatRoomSubscriptions[roomId];
            }
        } else {
            alert("채팅방에서 나가기 실패했습니다.");
        }
    } catch (error) {
        console.error("채팅방 나가기 오류:", error);
        alert("채팅방에서 나가는 중 오류가 발생했습니다.");
    }
};
