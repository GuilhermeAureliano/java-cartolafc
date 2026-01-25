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
    return Number.isNaN(num) ? '—' : num.toLocaleString('pt-BR');
}

function extractNumber(obj, camelKey, snakeKey) {
    if (obj == null) return 0;
    const value = obj[camelKey] ?? obj[snakeKey];
    if (value == null) return 0;
    const num = Number(value);
    return Number.isNaN(num) ? 0 : num;
}

function formatCompactNumber(num) {
    if (num == null || Number.isNaN(num)) return '—';
    
    const formatter = new Intl.NumberFormat('pt-BR', {
        notation: 'compact',
        compactDisplay: 'short',
        maximumFractionDigits: 1
    });
    
    return formatter.format(num);
}

function parseTeamsCsv(text) {
    return text
        .split(/\r?\n/)
        .map(line => line.trim())
        .filter(line => line.length > 0);
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function normalizeText(text) {
    if (!text) return '';
    return text
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '');
}
