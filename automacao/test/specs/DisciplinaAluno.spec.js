import { VerificarDisciplinaAdicionada } from '../screenobjects/DisciplinaAluno.js';


describe('Fluxo de verificação da disciplina adicionada ao aluno', () => {

    it('Deve aparecer a disciplina adicionada ao aluno na listagem', async () => {
        await VerificarDisciplinaAdicionada('POO');
    });
});