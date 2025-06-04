import { TelaAdicionarAluno, AdicionarAluno, AdicionarSemPreencher, VerificarToastPorTexto, AlunoAdicionado  } from '../screenobjects/AdicionarAluno.js';

describe('Fluxo adicionar aluno a disciplina', () => {

    it('Deve ir até a tela de cadastro', async () => {
        await TelaAdicionarAluno();
    });

    it('Não deve cadastrar sem preencher os campos', async () => {
        await AdicionarSemPreencher();
        await VerificarToastPorTexto('Preencha todos os campos');
    });

    it('Não deve cadastrar com email de professor inexistente', async () => {
        await AdicionarAluno('emailerrado@test.com');
        await VerificarToastPorTexto('Aluno não encontrado');
    });

    it('Não deve cadastrar com email de aluno', async () => {
        await AdicionarAluno('professor@email.com');
        await VerificarToastPorTexto('O email informado não pertence a um aluno');
    });
        
    it('Deve realizar o cadastro com sucesso', async () => {
        await AdicionarAluno('aluno@email.com');
        await VerificarToastPorTexto('Aluno adicionado com sucesso');
    });

    it('Deve aparecer a disciplina cadastrada na listagem', async () => {
        await AlunoAdicionado('Aluno');        
    });
});