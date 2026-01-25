function escapeHtml(str) {
    if (str == null) return '—';
    const div = document.createElement('div');
    div.textContent = String(str);
    return div.innerHTML;
}

function formatNumber(obj, camelKey, snakeKey) {
    if (obj == null) return '—';
    const value = obj[camelKey] ?? obj[snakeKey];
    if (value == null) return '—';
    const num = Number(value);
    return Number.isNaN(num) ? '—' : String(num);
}

function parseTeamsCsv(text) {
    return text
        .split(/\r?\n/)
        .map(line => line.trim())
        .filter(line => line.length > 0);
}
