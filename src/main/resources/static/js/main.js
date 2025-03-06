/*
 * main.js (Refactored)
 * 이 파일은 웹소켓 연결, 채팅방 구독, 채팅방 목록 렌더링, 친구 선택 및 초대 등의 기능을 처리합니다.
 * 기능은 그대로 유지하면서 코드 중복을 줄이고 가독성을 높였습니다.
 */

const websocketUrl = "http://localhost:8080/connect";
const userId = document.body.getAttribute('data-user-id');

// 웹소켓 구독 객체 (key: roomId, value: subscription 객체)
const chatRoomSubscriptions = {};

// 채팅방 목록 배열
let chatRooms = [];

// 전체 친구 목록 (명확하게 allFriends로 명명)
let allFriends = [];

const chatList = document.getElementById("chat-list");
const createRoomBtn = document.getElementById("create-new-room");
const confirmBtn = document.getElementById("confirm-create-room");
const friendList = document.getElementById("friend-list");
const friendModalEl = document.getElementById('friendModal');

// 친구 선택 상태 저장 Set
const selectedFriends = new Set();

/** 유틸리티 함수: 채팅방 항목 DOM 생성 */
function createChatRoomItem(room) {
  return `
    <li class="list-group-item d-flex justify-content-between align-items-center" id="room-${room.roomId}">
      <div class="d-flex align-items-center">
        <img src="favicon.ico" alt="Profile" class="rounded-circle me-2" style="width:40px; height:40px; background-color:#DDD;">
        <div>
          <div class="room-title fw-bold text-primary">
            ${room.roomName}
            <i class="bi bi-pencil-square" onclick="openEditRoomModal('${room.roomId}','${room.roomName}')"></i>
          </div>
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
    </li>
  `;
}

/** 채팅방 목록 UI 재렌더링 */
function renderChatList() {
  chatList.innerHTML = "";
  if (chatRooms.length === 0) {
    chatList.innerHTML += `<div class="room-title fw-bold text-primary">
                             채팅방이 없어요! <i class="bi bi-chat-dots-fill"></i>
                           </div>`;
  } else {
    chatRooms.forEach(room => {
      chatList.insertAdjacentHTML('beforeend', createChatRoomItem(room));
    });
  }
}

/**
 * 채팅방 정보를 추가하거나 업데이트한 후, 배열 및 UI를 갱신합니다.
 * @param {Object} room - 채팅방 객체 (roomId, roomName, latestMessage, latestTimestamp 등)
 */
function addOrUpdateChatRoom(room) {
  const existingIndex = chatRooms.findIndex(r => r.roomId === room.roomId);
  if (existingIndex !== -1) {
    chatRooms[existingIndex] = room;
  } else {
    chatRooms.push(room);
  }
  // 최신 메시지 순으로 정렬 후 UI 업데이트
  chatRooms.sort((a, b) => new Date(b.latestTimestamp) - new Date(a.latestTimestamp));
  renderChatList();
}

/** 유틸리티 함수: 친구 목록 항목 DOM 생성 */
function createFriendListItem(friend) {
  const li = document.createElement("li");
  li.className = "list-group-item d-flex align-items-center";
  li.dataset.id = friend.id;
  li.innerHTML = `
    <input type="hidden" value="${friend.id}">
    <img src="${friend.image}" alt="${friend.name}" class="rounded-circle me-2" width="40">
    <span>${friend.name}</span>
  `;
  li.addEventListener("click", () => toggleFriendSelection(li, friend.id));
  return li;
}

/**
 * 공통 함수: 주어진 친구 목록 데이터를 모달에 렌더링
 * @param {Array} list - 친구 객체 배열
 */
function renderFriendListFromData(list) {
  friendList.innerHTML = "";
  if (!list || list.length === 0) {
    friendList.innerHTML = `<p class="text-center text-muted">초대할 수 있는 친구가 없습니다.</p>`;
    return;
  }
  list.forEach(friend => friendList.appendChild(createFriendListItem(friend)));
}

/**
 * 친구 항목 선택 상태 토글
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

/**
 * 서버에서 전체 친구 목록을 가져옵니다.
 */
async function fetchAllFriends() {
  try {
    const response = await fetch(`/api/friends/${userId}`);
    if (!response.ok) {
      const errorData = await response.json();
      if (errorData.errorCode === "NO_FRIENDS_FOUND") {
        return [];
      }
      throw new Error(errorData.message || "친구 목록을 가져오는데 실패했습니다.");
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching friends:', error);
    return [];
  }
}

/**
 * 서버에서 채팅방에 없는 친구 목록을 가져옵니다.
 * @param {string} roomId - 채팅방 ID
 * @returns {Array} 초대 가능한 친구 목록
 */
async function fetchFriendsNotInRoom(roomId) {
  try {
    const response = await fetch(`/api/rooms/${roomId}/friends`);
    if (!response.ok) {
      const errorData = await response.json();
      if (errorData.errorCode === "NO_FRIENDS_FOUND") {
        return [];
      }
      throw new Error('친구 목록을 가져오는데 실패했습니다.');
    }
    return await response.json();
  } catch (error) {
    console.error("Error fetching friends:", error);
    return [];
  }
}

/**
 * 신규 채팅방 생성 API 호출
 * @param {Array} invited - 초대할 친구 ID 배열
 */
async function createChatRoom(invited) {
  try {
    const response = await fetch('/api/rooms', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({invitedIds: invited})
    });
    if (response.ok) {
      renderChatList();
    } else {
      alert("채팅방 생성에 실패했습니다.");
    }
  } catch (error) {
    console.error("채팅방 생성 오류:", error);
    alert("채팅방 생성 중 오류가 발생했습니다.");
  }
}

/**
 * 기존 채팅방에 친구 초대 API 호출
 * @param {string} roomId - 채팅방 ID
 * @param {Array} selectedIds - 초대할 친구 ID 배열
 */
async function inviteFriendsToRoom(roomId, selectedIds) {
  try {
    const response = await fetch(`/api/rooms/${roomId}`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({invitedIds: selectedIds})
    });
    if (response.ok) {
      bootstrap.Modal.getInstance(document.getElementById("friendModal")).hide();
    } else {
      alert("친구 초대에 실패했습니다.");
    }
  } catch (error) {
    console.error("친구 초대 오류:", error);
    alert("친구 초대 중 오류가 발생했습니다.");
  }
}

/**
 * 채팅방 나가기 기능
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

/**
 * 방 이름 수정 모달 열기 및 기존 이름 설정
 * @param {string} roomId - 수정할 방의 ID
 * @param {string} currentName - 현재 방 이름
 */
function openEditRoomModal(roomId, currentName) {
  document.getElementById('edit-room-name').value = currentName;
  document.getElementById('save-room-name-btn').setAttribute('data-room-id', roomId);
  new bootstrap.Modal(document.getElementById('editRoomModal')).show();
}

document.getElementById('save-room-name-btn').addEventListener('click', async function () {
  const roomId = this.getAttribute('data-room-id');
  const newName = document.getElementById('edit-room-name').value.trim();

  if (!newName) {
    alert('방 이름을 입력하세요.');
    return;
  }

  try {
    const response = await fetch(`/api/rooms/${roomId}`, {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({roomName: newName})
    });

    if (!response.ok) {
      throw new Error('방 이름 수정 실패');
    }
    bootstrap.Modal.getInstance(document.getElementById('editRoomModal')).hide();
    alert('채팅방 이름이 수정되었습니다.');
  } catch (error) {
    console.error(error);
    alert('채팅방 이름 수정 중 오류가 발생했습니다.');
  }
});

/** DOMContentLoaded: 초기 설정 및 웹소켓 연결 */
document.addEventListener("DOMContentLoaded", async function () {
  // SockJS와 STOMP를 이용한 웹소켓 연결 초기화
  const socket = new SockJS(websocketUrl);
  const stompClient = Stomp.over(socket);
  // stompClient.debug = str => console.log("STOMP DEBUG:", str);

  stompClient.connect({}, function () {
    stompClient.send('/room-pub/rooms/init', {}, {});

    // 사용자 채팅방 구독
    stompClient.subscribe(`/room-sub/user/${userId}`, function (messageOutput) {
      const {rooms: chatRoomIds} = JSON.parse(messageOutput.body);
      chatRoomIds.forEach(roomId => {
        if (!chatRoomSubscriptions[roomId]) {
          chatRoomSubscriptions[roomId] = stompClient.subscribe(`/room-sub/room/${roomId}`, function (msg) {
            const roomInfo = JSON.parse(msg.body);
            addOrUpdateChatRoom(roomInfo);
          });
        }
      });
    });
    renderChatList();
  });

  // 전체 친구 목록 초기 로딩
  allFriends = await fetchAllFriends();
});

/** "새로 만들기" 버튼 클릭: 신규 채팅방 생성 모달 열기 */
createRoomBtn.addEventListener("click", function () {
  confirmBtn.dataset.action = "create";
  delete confirmBtn.dataset.roomId;
  if (allFriends.length === 0) {
    friendList.innerHTML = `<p class="text-center text-muted">초대할 수 있는 친구가 없습니다.</p>`;
    confirmBtn.disabled = true;
  } else {
    renderFriendListFromData(allFriends);
    confirmBtn.disabled = false;
  }
  new bootstrap.Modal(friendModalEl).show();
});

/** 모달 닫힘 시 선택된 친구 초기화 */
friendModalEl.addEventListener('hidden.bs.modal', function () {
  selectedFriends.clear();
});

/** 모달 확인 버튼 클릭: 신규 채팅방 생성 또는 친구 초대 */
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
  friendModalEl.querySelector(".btn-close").click();
});

/**
 * 친구 초대: 지정 채팅방에 없는 친구 목록을 모달에 렌더링하고 열기
 * @param {string} roomId - 채팅방 ID
 */
async function addFriends(roomId) {
  confirmBtn.dataset.action = "invite";
  confirmBtn.dataset.roomId = roomId;
  const friendsNotInRoom = await fetchFriendsNotInRoom(roomId);
  selectedFriends.clear();

  if (friendsNotInRoom.length === 0) {
    friendList.innerHTML = `<p class="text-center text-muted">초대할 수 있는 친구가 없습니다.</p>`;
    confirmBtn.disabled = true;
  } else {
    renderFriendListFromData(friendsNotInRoom);
    confirmBtn.disabled = false;
  }
  new bootstrap.Modal(friendModalEl).show();
}
