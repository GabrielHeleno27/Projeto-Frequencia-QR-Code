import { TelaCadastroDisciplina, CadastroSemPreencherCampos, VerificarToastPorTexto, Cadastro, TelaListagemDisciplina,  } from '../screenobjects/CadastroDisciplina.js';


describe('Fluxo de cadastro das disciplinas', () => {

    it('Deve ir até a tela de cadastro', async () => {
        await TelaCadastroDisciplina();
    });
    
    it('Não deve cadastrar sem preencher os campos', async () => {
        await CadastroSemPreencherCampos();
        await VerificarToastPorTexto('Preencha todos os campos');
    });

    it('Não deve cadastrar com email de professor inexistente', async () => {
        await Cadastro('POO', 'emailerrado@test.com');
        await VerificarToastPorTexto('Professor não encontrado');
    });

    it('Não deve cadastrar com email de aluno', async () => {
        await Cadastro('POO', 'aluno@email.com');
        await VerificarToastPorTexto('O email informado não pertence a um professor');
    });
        
    it('Deve realizar o cadastro com sucesso', async () => {
        await Cadastro('POO', 'professor@email.com');
        await VerificarToastPorTexto('Disciplina criada com sucesso');
    });

    it('Deve aparecer a disciplina cadastrada na listagem', async () => {
        await TelaListagemDisciplina('POO');
        
    });

});