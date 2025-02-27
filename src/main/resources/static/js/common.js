function formatChatTimestamp(isoString) {
    const date = new Date(isoString); // ISO 문자열을 Date 객체로 변환
    const now = new Date();

    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 월(0~11) → 1~12 변환
    const day = date.getDate();
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    // 1️⃣ 오늘 날짜인지 확인
    if (date.toDateString() === now.toDateString()) {
        return `${hours}:${minutes}`;
    }

    // 2️⃣ 어제인지 확인
    const yesterday = new Date();
    yesterday.setDate(now.getDate() - 1);
    if (date.toDateString() === yesterday.toDateString()) {
        return `어제`;
    }

    // 3️⃣ 올해인지 확인
    if (year === now.getFullYear()) {
        return `${month}월 ${day}일`;
    }

    // 4️⃣ 전년도 이상
    return `${year}년 ${month}월 ${day}일`;
}

// ✅ 사용 예시
// console.log(formatChatTimestamp("2025-02-25T15:30:00Z")); // 오늘이라면 → "15:30"
// console.log(formatChatTimestamp("2024-02-24T10:20:00Z")); // 전날이라면 → "어제"
// console.log(formatChatTimestamp("2024-02-23T08:15:00Z")); // 올해라면 → "2월 23일"
// console.log(formatChatTimestamp("2023-12-31T23:59:59Z")); // 작년이라면 → "2023년 12월 31일"
