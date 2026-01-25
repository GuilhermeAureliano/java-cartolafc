const API_BASE = '';

async function fetchWithTimeout(url, timeoutMs = 10000) {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), timeoutMs);

    try {
        const response = await fetch(url, { signal: controller.signal });
        clearTimeout(timeoutId);
        return response;
    } catch {
        clearTimeout(timeoutId);
        return null;
    }
}

async function fetchJson(url, timeoutMs = 10000) {
    try {
        const response = await fetchWithTimeout(url, timeoutMs);
        if (!response || !response.ok) return null;
        return await response.json();
    } catch {
        return null;
    }
}

async function fetchText(url, timeoutMs = 10000) {
    try {
        const response = await fetchWithTimeout(url, timeoutMs);
        if (!response || !response.ok) return null;
        return await response.text();
    } catch {
        return null;
    }
}

function fetchTeamById(id) {
    if (!id) return Promise.resolve(null);
    return fetchJson(`${API_BASE}/time/id/${encodeURIComponent(id)}`);
}

function fetchTeamByName(name) {
    return fetchJson(`${API_BASE}/times?q=${encodeURIComponent(name)}`);
}
