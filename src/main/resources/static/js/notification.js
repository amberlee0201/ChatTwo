document.addEventListener("DOMContentLoaded", function () {
  let notificationCount = 0; // ✅ 전역 변수 선언

  // 알림 리스트를 화면에 출력
  fetch("/api/notifications")
  .then(response => response.json())
  .then(data => {
    notificationCount = data.length;
    updateNotificationBadge(notificationCount);

    data.forEach(notification => {
      addNotificationToList(notification.message);
    });
  })
  .catch(error => console.error("❌ 알림 개수 불러오기 실패:", error));

  // WebSocket 연결
  const socket = new SockJS("/notification-connect");
  const stompClient = Stomp.over(socket);

  stompClient.connect({}, function (frame) {
    console.log("✅ STOMP 연결 성공:", frame);

    // 실시간 알림 수신
    stompClient.subscribe("/topic/notification-sub", function (message) {
      const notification = JSON.parse(message.body);
      console.log("📩 받은 알림:", notification.message);

      // 메시지 출력은 notification-list가 있을 때만
      const notificationList = document.getElementById("notification-list");
      if (notificationList) {
        addNotificationToList(notification.message);
      }

      // 실시간 알림 카운트 수신
      stompClient.subscribe("/topic/notification-count", function (message) {
        console.log("✅ [notification-count] 메시지 도착:", message.body);
        const count = parseInt(message.body);
        if (!isNaN(count)) {
          updateNotificationBadge(count);
        } else {
          console.warn("⚠️ 알림 수가 숫자가 아님:", message.body);
        }
      });
    });

    // ✅ 알림 목록에 추가
    function addNotificationToList(message) {
      const notificationList = document.getElementById("notification-list");
      if (!notificationList) {
        console.warn("⚠️ 이 페이지엔 notification-list가 없어요. 리스트 추가 스킵!");
        return;
      }

      const newItem = document.createElement("li");
      newItem.classList.add("notification-item");
      newItem.textContent = message;
      notificationList.prepend(newItem);
    }

    // ✅ 알림 전체 삭제
    window.clearNotifications = function () {
      const notificationList = document.getElementById("notification-list");
      if (notificationList) {
        notificationList.innerHTML = "";
      }
      notificationCount = 0;
      updateNotificationBadge(notificationCount);
    };

    // ✅ 알림 배지 업데이트
    function updateNotificationBadge(count) {
      const badge = document.getElementById("notificationBadge");
      if (badge) {
        if (count > 0) {
          badge.style.display = "inline-block";
          badge.textContent = count;
        } else {
          badge.style.display = "none";
        }
      }
    }

    // (선택) 친구 알림 수동 전송 (테스트용)
    window.sendFriendNotification = function (sender, receiver) {
      fetch("/notifications/friend-added", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: `sender=${encodeURIComponent(
            sender)}&receiver=${encodeURIComponent(receiver)}`
      })
      .then(response => {
        if (response.ok) {
          console.log("✅ 친구 추가 알림 전송 성공");
        } else {
          console.error("🚨 친구 추가 알림 전송 실패");
        }
      })
      .catch(error => console.error("🚨 서버 오류:", error));
    };
  })
});
