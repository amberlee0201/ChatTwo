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

    // ✅ window.userId가 있어야 작동함 (템플릿에서 설정 필요)
    const userId = window.userId;
    if (!userId) {
      console.warn("❗ userId가 정의되지 않았습니다.");
      return;
    }

    stompClient.connect({}, function (frame) {
      console.log("✅ STOMP 연결 완료", frame);
      fetch("/notifications/data")
      .then(res => res.json())
      .then(data => {
        const activeCount = data.length;
        notificationCount = activeCount;
        updateNotificationBadge(activeCount);
      })
      .catch(err => {
        console.error("🚨 알림 초기 로드 실패:", err);
      });

      stompClient.subscribe("/topic/notification-sub/" + userId, function (msg) {
        if (!msg.body) return;
        const notification = JSON.parse(msg.body);
        if (notification.message) {
          addNotificationToList(notification.message);
        }
        if (!isNaN(notification.count)) {
          notificationCount = notification.count;
          updateNotificationBadge(notificationCount);
        }
      });

      stompClient.subscribe("/topic/notification-count/" + userId, function (msg) {
        const count = parseInt(msg.body);
        if (!isNaN(count)) {
          notificationCount = count;
          updateNotificationBadge(count);
        }
      });

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

  document.addEventListener("DOMContentLoaded", function () {
    const deleteAllBtn = document.getElementById("deleteAllBtn");
    if (deleteAllBtn) {
      deleteAllBtn.addEventListener("click", function () {
        fetch("/notifications", { method: "DELETE" })
        .then((res) => {
          if (res.ok) {
            window.clearNotifications();
            alert("모든 알림을 숨김 처리했습니다.");
          } else {
            alert("알림 숨김 처리 실패");
          }
        })
        .catch((err) => {
          console.error("🚨 삭제 요청 실패:", err);
          alert("에러 발생");
        });
      });
    }
  });

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initNotifications);
  } else {
    initNotifications();
  }
}
