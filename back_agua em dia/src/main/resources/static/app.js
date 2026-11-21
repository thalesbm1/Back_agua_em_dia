/**
 * ÁGUA EM DIA — Integração Frontend com APIs REST Spring Boot
 */

const API = {
    usuarios: '/api/usuarios',
    vazamentos: '/api/vazamentos',
    relatos: '/api/relatos'
};

// Inicialização da Página
document.addEventListener('DOMContentLoaded', () => {
    carregarUsuarios();
    carregarVazamentos();
    carregarRelatos();

    // Eventos dos Formulários
    document.getElementById('formUsuario').addEventListener('submit', cadastrarUsuario);
    document.getElementById('formRelato').addEventListener('submit', registrarRelato);
});

// Toast / Notificações
function exibirToast(mensagem, tipo = 'sucesso') {
    const toast = document.getElementById('toast');
    toast.textContent = mensagem;
    toast.className = `alerta-flutuante alerta-${tipo} visivel`;
    setTimeout(() => {
        toast.className = 'alerta-flutuante';
    }, 4000);
}

// 1. USUÁRIOS
async function carregarUsuarios() {
    try {
        const resp = await fetch(API.usuarios);
        if (!resp.ok) throw new Error('Falha ao obter lista de usuários');
        const usuarios = await resp.json();

        const selectRelato = document.getElementById('relatoUsuario');
        selectRelato.innerHTML = '';

        if (usuarios.length === 0) {
            selectRelato.innerHTML = '<option value="">Nenhum usuário cadastrado</option>';
            return;
        }

        usuarios.forEach(u => {
            const opt = document.createElement('option');
            opt.value = u.id;
            opt.textContent = `${u.nome} (${u.email})`;
            selectRelato.appendChild(opt);
        });
    } catch (err) {
        console.error('Erro ao carregar usuários:', err);
    }
}

async function cadastrarUsuario(e) {
    e.preventDefault();

    const payload = {
        nome: document.getElementById('usuarioNome').value.trim(),
        email: document.getElementById('usuarioEmail').value.trim(),
        telefone: document.getElementById('usuarioTelefone').value.trim(),
        senha: document.getElementById('usuarioSenha').value,
        situacao: document.getElementById('usuarioSituacao').value
    };

    try {
        const resp = await fetch(API.usuarios, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (resp.ok) {
            const novo = await resp.json();
            exibirToast(`Usuário "${novo.nome}" cadastrado com sucesso!`, 'sucesso');
            document.getElementById('formUsuario').reset();
            carregarUsuarios();
        } else {
            const erro = await resp.json();
            exibirToast(`Erro: ${erro.erro || 'Falha ao cadastrar usuário'}`, 'erro');
        }
    } catch (err) {
        console.error('Erro na requisição:', err);
        exibirToast('Não foi possível conectar ao servidor backend.', 'erro');
    }
}

// 2. VAZAMENTOS
async function carregarVazamentos() {
    try {
        const resp = await fetch(API.vazamentos);
        if (!resp.ok) throw new Error('Falha ao obter vazamentos');
        const vazamentos = await resp.json();

        // Atualizar Estatísticas
        document.getElementById('statTotalVazamentos').textContent = vazamentos.length;
        document.getElementById('statAbertos').textContent = vazamentos.filter(v => v.status === 'ABERTO').length;
        document.getElementById('statManutencao').textContent = vazamentos.filter(v => v.status === 'EM_MANUTENCAO').length;
        document.getElementById('statConcluidos').textContent = vazamentos.filter(v => v.status === 'CONCLUIDO' || v.status === 'RESOLVIDO').length;

        const tbody = document.getElementById('tabelaVazamentos');
        tbody.innerHTML = '';

        if (vazamentos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">Nenhum vazamento registrado no banco.</td></tr>';
            return;
        }

        vazamentos.forEach(v => {
            const tr = document.createElement('tr');
            
            let badgeClass = 'badge-aberto';
            if (v.status === 'EM_MANUTENCAO') badgeClass = 'badge-manutencao';
            if (v.status === 'CONCLUIDO' || v.status === 'RESOLVIDO') badgeClass = 'badge-concluido';

            const dataFormatada = v.primeiroRelatoValidoEm ? new Date(v.primeiroRelatoValidoEm).toLocaleString('pt-BR') : '-';

            tr.innerHTML = `
                <td><strong>#${v.id}</strong></td>
                <td>${v.bairro || '-'}</td>
                <td><span class="badge badge-tipo">${v.tipo || '-'}</span></td>
                <td>${v.porte || '-'}</td>
                <td><strong>${(v.vazaoEstimada || 0).toLocaleString('pt-BR')} L/h</strong></td>
                <td><span class="badge ${badgeClass}">${v.status}</span></td>
                <td>${dataFormatada}</td>
                <td>
                    <select class="select-status" onchange="atualizarStatusVazamento(${v.id}, this.value)">
                        <option value="ABERTO" ${v.status === 'ABERTO' ? 'selected' : ''}>ABERTO</option>
                        <option value="EM_MANUTENCAO" ${v.status === 'EM_MANUTENCAO' ? 'selected' : ''}>EM MANUTENÇÃO</option>
                        <option value="CONCLUIDO" ${v.status === 'CONCLUIDO' || v.status === 'RESOLVIDO' ? 'selected' : ''}>CONCLUÍDO</option>
                    </select>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error('Erro ao carregar vazamentos:', err);
    }
}

async function atualizarStatusVazamento(id, novoStatus) {
    try {
        const resp = await fetch(`${API.vazamentos}/${id}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: novoStatus })
        });

        if (resp.ok) {
            exibirToast(`Status do Vazamento #${id} atualizado para ${novoStatus}!`, 'sucesso');
            carregarVazamentos();
        } else {
            const erro = await resp.json();
            exibirToast(`Erro: ${erro.erro || 'Falha ao atualizar status'}`, 'erro');
        }
    } catch (err) {
        console.error('Erro ao atualizar status:', err);
        exibirToast('Erro de comunicação com o backend.', 'erro');
    }
}

// 3. RELATOS
async function carregarRelatos() {
    try {
        const resp = await fetch(API.relatos);
        if (!resp.ok) throw new Error('Falha ao obter relatos');
        const relatos = await resp.json();

        document.getElementById('statTotalRelatos').textContent = relatos.length;

        const tbody = document.getElementById('tabelaRelatos');
        tbody.innerHTML = '';

        if (relatos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">Nenhum relato registrado.</td></tr>';
            return;
        }

        relatos.forEach(r => {
            const tr = document.createElement('tr');
            const dataEnvio = r.enviadoEm ? new Date(r.enviadoEm).toLocaleString('pt-BR') : '-';

            tr.innerHTML = `
                <td><span class="badge badge-protocolo">${r.protocolo}</span></td>
                <td>${r.usuarioNome || ('Cidadão #' + r.usuarioId)}</td>
                <td><strong>${r.bairro || '-'}</strong><br><small style="color:var(--texto-secundario);">${r.referencia || ''}</small></td>
                <td><span class="badge badge-tipo">${r.tipo || '-'}</span></td>
                <td>${r.descricao || '-'}</td>
                <td>${dataEnvio}</td>
                <td><a href="/api/vazamentos/${r.vazamentoId}" target="_blank" style="color:var(--azul-agua);">Vazamento #${r.vazamentoId}</a></td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error('Erro ao carregar relatos:', err);
    }
}

async function registrarRelato(e) {
    e.preventDefault();

    const payload = {
        usuarioId: parseInt(document.getElementById('relatoUsuario').value, 10),
        bairro: document.getElementById('relatoBairro').value,
        tipo: document.getElementById('relatoTipo').value,
        porte: document.getElementById('relatoPorte').value,
        referencia: document.getElementById('relatoReferencia').value.trim(),
        descricao: document.getElementById('relatoDescricao').value.trim()
    };

    try {
        const resp = await fetch(API.relatos, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (resp.ok) {
            const novo = await resp.json();
            exibirToast(`Relato criado com sucesso! Protocolo: ${novo.protocolo}`, 'sucesso');
            document.getElementById('relatoReferencia').value = '';
            document.getElementById('relatoDescricao').value = '';
            carregarRelatos();
            carregarVazamentos();
        } else {
            const erro = await resp.json();
            exibirToast(`Erro: ${erro.erro || 'Falha ao registrar relato'}`, 'erro');
        }
    } catch (err) {
        console.error('Erro na requisição:', err);
        exibirToast('Não foi possível conectar ao servidor backend.', 'erro');
    }
}
