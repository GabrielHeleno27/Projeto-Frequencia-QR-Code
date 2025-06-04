import { LoginAluno, LogoutProfessor} from '../screenobjects/LoginAluno.js';


describe('Fluxo de Login', () => {

    it('Deve realizar o logout da conta do coordenador', async () => {
        await LogoutProfessor();
    });
    
    it('Deve realizar o login com sucesso', async () => {
        await LoginAluno('aluno@email.com', '123456');
    });
});