import { LoginProfessor, LogoutCordenador} from '../screenobjects/LoginProfessor.js';


describe('Fluxo de Login', () => {

    it('Deve realizar o logout da conta do coordenador', async () => {
        await LogoutCordenador();
    });
    
    it('Deve realizar o login com sucesso', async () => {
        await LoginProfessor('professor@email.com', '123456');
    });
});