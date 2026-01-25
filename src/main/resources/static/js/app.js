const CSV_URL = 'data/times.csv';

const elements = {
    loadButton: document.getElementById('btnCarregar'),
    status: document.getElementById('status'),
    loading: document.getElementById('loading'),
    tableWrapper: document.getElementById('tabelaWrapper'),
    tableBody: document.getElementById('corpoTabela'),
    error: document.getElementById('erro'),
    errorMessage: document.getElementById('mensagemErro')
};

function setStatus(text) {
    elements.status.textContent = text;
}

function showLoading(show) {
    const el = elements.loading;
    if (show) {
        el.style.display = 'flex';
        el.removeAttribute('hidden');
    } else {
        el.style.display = 'none';
        el.hidden = true;
    }
}

function showTable(show) {
    elements.tableWrapper.hidden = !show;
}

function showError(show, msg) {
    elements.error.hidden = !show;
    if (msg) elements.errorMessage.textContent = msg;
}

function createTableRow(originalName, teamDto, details) {
    const tr = document.createElement('tr');
    const isValid = teamDto && typeof teamDto === 'object' && !Array.isArray(teamDto);

    if (!isValid) {
        tr.innerHTML = `
            <td class="escudo-cell"><div class="escudo-placeholder">—</div></td>
            <td>${escapeHtml(originalName)}</td>
            <td colspan="3" class="nao-encontrado">Não encontrado</td>
        `;
        return tr;
    }

    const shieldHtml = teamDto.url_escudo_png
        ? `<img class="escudo" src="${escapeHtml(teamDto.url_escudo_png)}" alt="" loading="lazy" onerror="this.outerHTML='<div class=&quot;escudo-placeholder&quot;>—</div>'">`
        : '<div class="escudo-placeholder">—</div>';

    const championshipPoints = formatNumber(details, 'pontosCampeonato', 'pontos_campeonato');
    const assets = formatNumber(details, 'patrimonio', 'patrimonio');

    tr.innerHTML = `
        <td class="escudo-cell">${shieldHtml}</td>
        <td>${escapeHtml(teamDto.nome || originalName)}</td>
        <td>${escapeHtml(teamDto.nome_cartola || teamDto.nomeCartola || '—')}</td>
        <td>${escapeHtml(championshipPoints)}</td>
        <td>${escapeHtml(assets)}</td>
    `;
    return tr;
}

async function loadTeams() {
    setStatus('');
    showError(false);
    showTable(false);
    showLoading(true);
    elements.tableBody.innerHTML = '';
    elements.loadButton.disabled = true;

    try {
        const csvContent = await fetchText(CSV_URL, 8000);
        if (!csvContent) throw new Error('Não foi possível carregar data/times.csv');

        const names = parseTeamsCsv(csvContent);
        if (names.length === 0) {
            setStatus('Nenhum time no CSV.');
            return;
        }

        setStatus(`Carregando ${names.length} times...`);

        const teamResults = await Promise.all(names.map(name => fetchTeamByName(name)));

        const detailResults = await Promise.all(
            teamResults.map(team => {
                const teamId = team?.time_id ?? team?.timeId;
                return teamId ? fetchTeamById(teamId) : Promise.resolve(null);
            })
        );

        names.forEach((name, index) => {
            const tr = createTableRow(name, teamResults[index], detailResults[index]);
            elements.tableBody.appendChild(tr);
        });

        const foundCount = teamResults.filter(r => r != null).length;
        if (foundCount === 0) {
            setStatus('Nenhum time encontrado');
        } else {
            setStatus(`${names.length} times (${foundCount} encontrados via API).`);
        }
        showLoading(false);
        showTable(true);
    } catch (e) {
        showError(true, e.message || 'Erro ao carregar os times.');
        setStatus('');
    } finally {
        showLoading(false);
        elements.loadButton.disabled = false;
    }
}

elements.loadButton.addEventListener('click', loadTeams);
document.addEventListener('DOMContentLoaded', loadTeams);
