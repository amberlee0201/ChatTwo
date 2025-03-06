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
    return `${year}. ${month}. ${day}.`;
}