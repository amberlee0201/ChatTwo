// 📁 /js/notification.js

if (!window.notificationInitialized) {
  window.notificationInitialized = true;

  let notificationCount = 0;

  function updateNotificationBadge(count) {
    const badge = document.getElementById("notificationBadge");
    if (!badge) return;

    if (count > 0) {
      badge.style.display = "inline-block";
      badge.textContent = count;
      localStorage.setItem("notificationCount", count);
    } else {
      badge.style.display = "none";
      localStorage.setItem("notificationCount", 0);
    }
  }

  function addNotificationToList(message) {
    const list = document.getElementById("notification-list");
    if (!list) return;

    const empty = list.querySelector(".text-muted");
    if (empty) empty.remove();

    const item = document.createElement("li");
    item.className = "notification-item";
    item.textContent = message;
    list.prepend(item);
  }

  function initNotifications() {
    const saved = parseInt(localStorage.getItem("notificationCount")) || 0;
    notificationCount = saved;
    updateNotificationBadge(notificationCount);

    const socket = new SockJS("/notification-connect");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
      console.log("✅ STOMP 연결 완료", frame);

      stompClient.subscribe("/topic/notification-sub", function (msg) {
        if (!msg.body) return;
        const notification = JSON.parse(msg.body);
        addNotificationToList(notification.message);
        notificationCount++;
        updateNotificationBadge(notificationCount);
      });

      stompClient.subscribe("/topic/notification-count", function (msg) {
        const count = parseInt(msg.body);
        if (!isNaN(count)) {
          notificationCount = count;
          updateNotificationBadge(count);
        }
      });

      // ✅ 알림 삭제 함수 전역 등록
      window.clearNotifications = function () {
        const list = document.getElementById("notification-list");
        if (list) {
          list.innerHTML = '<li class="notification-item text-muted">알림이 없습니다.</li>';
        }
        notificationCount = 0;
        updateNotificationBadge(0);
      };

      window.notificationCount = notificationCount;
      window.updateNotificationBadge = updateNotificationBadge;
    });
  }

  // ✅ 알림 전체 삭제 버튼 클릭 처리 추가
  document.addEventListener("DOMContentLoaded", function () {
    const deleteAllBtn = document.getElementById("deleteAllBtn");
    if (deleteAllBtn) {
      deleteAllBtn.addEventListener("click", function () {
        fetch("/api/notifications/all", {
          method: "DELETE",
        })
        .then((res) => {
          if (res.ok) {
            window.clearNotifications(); // ✅ 프론트에서도 제거
            alert("모든 알림을 숨김 처리했습니다."); // ✅ 변경됨
          } else {
            alert("알림 숨김 처리 실패"); // ✅ 변경됨
          }
        })
        .catch((err) => {
          console.error("🚨 삭제 요청 실패:", err); // ✅ 변경됨
          alert("에러 발생"); // ✅ 변경됨
        });
      });
    }
  }); // ✅ 변경됨

  // DOMContentLoaded 대응
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initNotifications);
  } else {
    initNotifications();
  }
}
