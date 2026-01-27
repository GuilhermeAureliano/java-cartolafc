const CSV_URL = 'data/times.csv';

const state = {
    teams: [],           // Dados completos dos times
    filteredTeams: [],   // Times filtrados
    sortColumn: null,    // Coluna de ordenação atual
    sortDirection: 'asc', // Direção da ordenação
    isLoading: false,    // Estado de carregamento
    searchTerm: ''       // Termo de busca atual
};

const elements = {
    loadButton: document.getElementById('btnCarregar'),
    retryButton: document.getElementById('btnRetry'),
    status: document.getElementById('status'),
    loading: document.getElementById('loading'),
    skeletonTable: document.getElementById('skeletonTable'),
    tableWrapper: document.getElementById('tabelaWrapper'),
    tableBody: document.getElementById('corpoTabela'),
    error: document.getElementById('erro'),
    errorMessage: document.getElementById('mensagemErro'),
    emptyState: document.getElementById('emptyState'),
    emptyFilter: document.getElementById('emptyFilter'),
    searchInput: document.getElementById('searchInput'),
    clearSearchBtn: document.getElementById('btnClearSearch'),
    progressBar: document.getElementById('progressBar'),
    // Stats
    statTotal: document.getElementById('statTotal'),
    statLeader: document.getElementById('statLeader'),
    statAvgPoints: document.getElementById('statAvgPoints'),
    statMaxPatrimony: document.getElementById('statMaxPatrimony')
};

const sortableHeaders = document.querySelectorAll('.data-table th.sortable');

function setStatus(text) {
    elements.status.textContent = text;
}

function setProgress(percent) {
    if (elements.progressBar) {
        elements.progressBar.style.width = `${percent}%`;
    }
}

function showLoading(show) {
    if (show) {
        elements.loading.style.display = 'flex';
        elements.loading.removeAttribute('hidden');
        elements.skeletonTable.removeAttribute('hidden');
        elements.emptyState.hidden = true;
        setProgress(0);
    } else {
        elements.loading.style.display = 'none';
        elements.loading.hidden = true;
        elements.skeletonTable.hidden = true;
    }
}

function showTable(show) {
    elements.tableWrapper.hidden = !show;
    if (show) {
        elements.emptyState.hidden = true;
    }
}

function showError(show, msg = '') {
    elements.error.hidden = !show;
    if (msg) elements.errorMessage.textContent = msg;
    if (show) {
        elements.emptyState.hidden = true;
    }
}

function updateStats() {
    const total = state.teams.length;
    const validTeams = state.teams.filter(t => t.isValid && t.details);
    
    let maxPoints = 0;
    let maxPatrimony = 0;
    let totalPoints = 0;
    
    validTeams.forEach(team => {
        const points = extractNumber(team.details, 'pontosCampeonato', 'pontos_campeonato');
        const patrimony = extractNumber(team.details, 'patrimonio', 'patrimonio');
        
        totalPoints += points;
        if (points > maxPoints) maxPoints = points;
        if (patrimony > maxPatrimony) maxPatrimony = patrimony;
    });
    
    const avgPoints = validTeams.length > 0 ? Math.round(totalPoints / validTeams.length) : 0;
    
    animateValue(elements.statTotal, total);
    animateValue(elements.statLeader, maxPoints, true);
    animateValue(elements.statAvgPoints, avgPoints, true);
    animateValue(elements.statMaxPatrimony, maxPatrimony, true);
}

function animateValue(element, endValue, useCompact = false) {
    const duration = 800;
    const startTime = performance.now();
    const startValue = 0;
    
    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        
        const easeOut = 1 - Math.pow(1 - progress, 3);
        const currentValue = Math.floor(startValue + (endValue - startValue) * easeOut);
        
        element.textContent = useCompact 
            ? formatCompactNumber(currentValue)
            : currentValue.toLocaleString('pt-BR');
        
        if (progress < 1) {
            requestAnimationFrame(update);
        }
    }
    
    requestAnimationFrame(update);
}

function createTableRow(team) {
    const tr = document.createElement('tr');
    
    if (!team.isValid) {
        tr.innerHTML = `
            <td class="col-escudo"><div class="escudo-placeholder">—</div></td>
            <td>${escapeHtml(team.originalName)}</td>
            <td colspan="4" class="not-found-cell">Não encontrado na API</td>
        `;
        tr.dataset.valid = 'false';
        return tr;
    }

    const { teamDto, details, monthlyPoints } = team;

    const shieldHtml = teamDto.url_escudo_png
        ? `<img class="escudo" src="${escapeHtml(teamDto.url_escudo_png)}" alt="Escudo de ${escapeHtml(teamDto.nome)}" loading="lazy" onerror="this.outerHTML='<div class=\\'escudo-placeholder\\'>—</div>'">`
        : '<div class="escudo-placeholder">—</div>';

    const points = formatNumber(details, 'pontosCampeonato', 'pontos_campeonato');
    const patrimony = formatNumber(details, 'patrimonio', 'patrimonio');
    const cartoleiro = teamDto.nome_cartola || teamDto.nomeCartola || '—';
    const monthlyPointsValue = monthlyPoints?.pontos_mensais || 0;

    tr.innerHTML = `
        <td class="col-escudo">${shieldHtml}</td>
        <td class="team-name">${escapeHtml(teamDto.nome || team.originalName)}</td>
        <td class="cartoleiro-name">${escapeHtml(cartoleiro)}</td>
        <td class="col-pontos value-cell">${points}</td>
        <td class="col-patrimonio value-cell">${patrimony}</td>
        <td class="col-pontos-mensais value-cell">${monthlyPointsValue}</td>
    `;
    
    tr.dataset.valid = 'true';
    tr.dataset.nome = normalizeText(teamDto.nome || team.originalName);
    tr.dataset.cartoleiro = normalizeText(cartoleiro);
    tr.dataset.pontos = extractNumber(details, 'pontosCampeonato', 'pontos_campeonato');
    tr.dataset.patrimonio = extractNumber(details, 'patrimonio', 'patrimonio');
    tr.dataset.pontosMensais = monthlyPointsValue || 0;

    return tr;
}

function renderTable() {
    elements.tableBody.innerHTML = '';
    
    if (state.filteredTeams.length === 0 && state.searchTerm) {
        elements.emptyFilter.hidden = false;
        return;
    }
    
    elements.emptyFilter.hidden = true;
    
    const fragment = document.createDocumentFragment();
    state.filteredTeams.forEach(team => {
        fragment.appendChild(createTableRow(team));
    });
    elements.tableBody.appendChild(fragment);
}

function filterTeams() {
    const term = normalizeText(state.searchTerm);
    
    if (!term) {
        state.filteredTeams = [...state.teams];
    } else {
        state.filteredTeams = state.teams.filter(team => {
            const nome = normalizeText(team.teamDto?.nome || team.originalName);
            const cartoleiro = normalizeText(team.teamDto?.nome_cartola || team.teamDto?.nomeCartola || '');
            return nome.includes(term) || cartoleiro.includes(term);
        });
    }
    
    if (state.sortColumn) {
        sortTeams(state.sortColumn, false);
    }
    
    renderTable();
    
    if (state.searchTerm) {
        setStatus(`${state.filteredTeams.length} de ${state.teams.length} times`);
    }
}

function sortTeams(column, toggleDirection = true) {
    if (toggleDirection) {
        if (state.sortColumn === column) {
            state.sortDirection = state.sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            state.sortColumn = column;
            state.sortDirection = 'asc';
        }
    }
    
    sortableHeaders.forEach(header => {
        const headerColumn = header.dataset.sort;
        if (headerColumn === column) {
            header.setAttribute('aria-sort', state.sortDirection === 'asc' ? 'ascending' : 'descending');
        } else {
            header.setAttribute('aria-sort', 'none');
        }
    });
    
    state.filteredTeams.sort((a, b) => {
        let valueA, valueB;
        
        switch (column) {
            case 'nome':
                valueA = normalizeText(a.teamDto?.nome || a.originalName);
                valueB = normalizeText(b.teamDto?.nome || b.originalName);
                break;
            case 'cartoleiro':
                valueA = normalizeText(a.teamDto?.nome_cartola || a.teamDto?.nomeCartola || '');
                valueB = normalizeText(b.teamDto?.nome_cartola || b.teamDto?.nomeCartola || '');
                break;
            case 'pontos':
                valueA = extractNumber(a.details, 'pontosCampeonato', 'pontos_campeonato');
                valueB = extractNumber(b.details, 'pontosCampeonato', 'pontos_campeonato');
                break;
            case 'patrimonio':
                valueA = extractNumber(a.details, 'patrimonio', 'patrimonio');
                valueB = extractNumber(b.details, 'patrimonio', 'patrimonio');
                break;
            case 'pontosMensais':
                valueA = a.monthlyPoints?.pontos_mensais ?? 0;
                valueB = b.monthlyPoints?.pontos_mensais ?? 0;
                break;
            default:
                return 0;
        }
        
        if (!a.isValid) return 1;
        if (!b.isValid) return -1;
        
        let comparison = 0;
        if (typeof valueA === 'string') {
            comparison = valueA.localeCompare(valueB, 'pt-BR');
        } else {
            comparison = valueA - valueB;
        }
        
        return state.sortDirection === 'asc' ? comparison : -comparison;
    });
    
    renderTable();
}

const handleSearch = debounce((event) => {
    state.searchTerm = event.target.value;
    elements.clearSearchBtn.hidden = !state.searchTerm;
    filterTeams();
}, 250);

function clearSearch() {
    elements.searchInput.value = '';
    state.searchTerm = '';
    elements.clearSearchBtn.hidden = true;
    filterTeams();
    
    const total = state.teams.length;
    const found = state.teams.filter(t => t.isValid).length;
    setStatus(`${total} times (${found} encontrados via API)`);
}

async function loadTeams() {
    if (state.isLoading) return;
    
    state.isLoading = true;
    state.teams = [];
    state.filteredTeams = [];
    state.searchTerm = '';
    state.sortColumn = null;
    state.sortDirection = 'asc';
    
    setStatus('Iniciando...');
    showError(false);
    showTable(false);
    showLoading(true);
    elements.tableBody.innerHTML = '';
    elements.loadButton.disabled = true;
    elements.searchInput.value = '';
    elements.searchInput.disabled = true;
    elements.clearSearchBtn.hidden = true;
    
    sortableHeaders.forEach(header => {
        header.setAttribute('aria-sort', 'none');
    });

    try {
        setStatus('Carregando arquivo CSV...');
        setProgress(10);
        
        const csvContent = await fetchText(CSV_URL, 8000);
        if (!csvContent) throw new Error('Não foi possível carregar data/times.csv');

        const names = parseTeamsCsv(csvContent);
        if (names.length === 0) {
            setStatus('Nenhum time no CSV.');
            showLoading(false);
            state.isLoading = false;
            return;
        }

        setStatus(`Buscando ${names.length} times na API...`);
        setProgress(25);

        const teamResults = await Promise.all(names.map(name => fetchTeamByName(name)));
        setProgress(50);
        
        setStatus('Carregando detalhes dos times...');
        
        const detailResults = await Promise.all(
            teamResults.map(team => {
                const teamId = team?.time_id ?? team?.timeId;
                return teamId ? fetchTeamById(teamId) : Promise.resolve(null);
            })
        );
        setProgress(65);

        setStatus('Carregando pontos mensais...');
        const monthlyPointsResults = await Promise.all(
            teamResults.map(team => {
                const teamId = team?.time_id ?? team?.timeId;
                return teamId ? fetchMonthlyPoints(teamId) : Promise.resolve(null);
            })
        );
        setProgress(85);

        state.teams = names.map((name, index) => {
            const teamDto = teamResults[index];
            const details = detailResults[index];
            const monthlyPoints = monthlyPointsResults[index];
            const isValid = teamDto && typeof teamDto === 'object' && !Array.isArray(teamDto);
            
            return {
                originalName: name,
                teamDto: isValid ? teamDto : null,
                details: isValid ? details : null,
                monthlyPoints: isValid ? monthlyPoints : null,
                isValid
            };
        });
        
        state.filteredTeams = [...state.teams];
        setProgress(100);

        const foundCount = state.teams.filter(t => t.isValid).length;
        setStatus(`${names.length} times (${foundCount} encontrados via API)`);
        
        renderTable();
        updateStats();
        
        showLoading(false);
        showTable(true);
        elements.searchInput.disabled = false;
        
    } catch (e) {
        showError(true, e.message || 'Erro ao carregar os times.');
        setStatus('');
    } finally {
        showLoading(false);
        elements.loadButton.disabled = false;
        state.isLoading = false;
    }
}

function init() {
    elements.loadButton.addEventListener('click', loadTeams);
    elements.retryButton?.addEventListener('click', loadTeams);
    elements.searchInput.addEventListener('input', handleSearch);
    elements.clearSearchBtn.addEventListener('click', clearSearch);
    
    sortableHeaders.forEach(header => {
        header.addEventListener('click', () => {
            const column = header.dataset.sort;
            if (column && state.teams.length > 0) {
                sortTeams(column);
            }
        });
        
        header.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                const column = header.dataset.sort;
                if (column && state.teams.length > 0) {
                    sortTeams(column);
                }
            }
        });
    });
    
    loadTeams();
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}
