function escapeHtml(str) {
    if (str == null) return '—';
    const div = document.createElement('div');
    div.textContent = String(str);
    return div.innerHTML;
}

function formatNumber(obj, camelKey, snakeKey, options = {}) {
    if (obj == null) return '—';
    const value = obj[camelKey] ?? obj[snakeKey];
    if (value == null) return '—';
    const num = Number(value);
    if (Number.isNaN(num)) return '—';

    const { minimumFractionDigits, maximumFractionDigits } = options;
    const formatOpts = {};
    if (minimumFractionDigits != null) formatOpts.minimumFractionDigits = minimumFractionDigits;
    if (maximumFractionDigits != null) formatOpts.maximumFractionDigits = maximumFractionDigits;

    return num.toLocaleString('pt-BR', formatOpts);
}

function formatDecimal(num, decimals = 2) {
    if (num == null || Number.isNaN(Number(num))) return '—';
    return Number(num).toLocaleString('pt-BR', {
        minimumFractionDigits: decimals,
        maximumFractionDigits: decimals
    });
}

function extractNumber(obj, camelKey, snakeKey, extraKeys = []) {
    if (obj == null) return 0;
    const keys = Array.isArray(extraKeys) ? extraKeys : [];
    const value = obj[camelKey] ?? obj[snakeKey] ?? keys.map(k => obj[k]).find(v => v != null);
    if (value == null) return 0;
    const num = parseNumeric(value);
    return num;
}

function parseNumeric(value) {
    if (value == null) return 0;
    if (typeof value === 'number') return Number.isNaN(value) ? 0 : value;
    const str = String(value).trim().replace(/\s/g, '');
    if (str === '') return 0;
    const num = Number(value);
    if (!Number.isNaN(num)) return num;
    const normalized = str.replace(/[^\d,.-]/g, '').replace(/\./g, '').replace(',', '.');
    const parsed = Number(normalized);
    return Number.isNaN(parsed) ? 0 : parsed;
}

function compareForSort(valueA, valueB, direction) {
    const isAsc = direction === 'asc';
    const aMissing = valueA == null || (typeof valueA === 'number' && Number.isNaN(valueA));
    const bMissing = valueB == null || (typeof valueB === 'number' && Number.isNaN(valueB));
    if (aMissing && bMissing) return 0;
    if (aMissing) return isAsc ? 1 : -1;
    if (bMissing) return isAsc ? -1 : 1;
    const isString = typeof valueA === 'string' && typeof valueB === 'string';
    let comparison;
    if (isString) {
        comparison = (valueA || '').localeCompare(valueB || '', 'pt-BR');
    } else {
        const numA = typeof valueA === 'number' ? valueA : parseNumeric(valueA);
        const numB = typeof valueB === 'number' ? valueB : parseNumeric(valueB);
        comparison = numA - numB;
    }
    return isAsc ? comparison : -comparison;
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
